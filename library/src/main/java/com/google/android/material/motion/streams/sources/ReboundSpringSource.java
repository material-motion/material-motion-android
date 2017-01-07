/*
 * Copyright 2016-present The Material Motion Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.motion.streams.sources;

import android.support.annotation.NonNull;

import com.facebook.rebound.OrigamiValueConverter;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.observable.IndefiniteObservable.Connector;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.SimpleMotionObserver;

/**
 * A source for rebound springs.
 */
public final class ReboundSpringSource extends SpringSource {

  private static final ReboundSpringSource SPRING_SOURCE = new ReboundSpringSource();
  private final SpringSystem springSystem = SpringSystem.create();

  /**
   * Creates a spring source for a float spring.
   */
  public static MotionObservable<Float> from(MaterialSpring<Float> spring) {
    return SPRING_SOURCE.create(spring);
  }

  @Override
  public MotionObservable<Float> create(final MaterialSpring<Float> spring) {
    return new MotionObservable<>(new Connector<MotionObserver<Float>>() {
      @NonNull
      @Override
      public IndefiniteObservable.Disconnector connect(MotionObserver<Float> observer) {
        final Spring reboundSpring = springSystem.createSpring();
        reboundSpring.setSpringConfig(new SpringConfig(0, 0));

        reboundSpring.setCurrentValue(spring.initialValue.read());

        spring.destination.subscribe(new SimpleMotionObserver<Float>() {
          @Override
          public void next(Float value) {
            reboundSpring.setEndValue(value);
          }
        });

        spring.tension.subscribe(new SimpleMotionObserver<Float>() {
          @Override
          public void next(Float value) {
            reboundSpring.getSpringConfig().tension =
              OrigamiValueConverter.tensionFromOrigamiValue(value);
          }
        });
        spring.friction.subscribe(new SimpleMotionObserver<Float>() {
          @Override
          public void next(Float value) {
            reboundSpring.getSpringConfig().friction =
              OrigamiValueConverter.frictionFromOrigamiValue(value);
          }
        });

        final SpringConnection connection = new SpringConnection(reboundSpring, observer);
        return new IndefiniteObservable.Disconnector() {
          @Override
          public void disconnect() {
            connection.disconnect();
          }
        };
      }
    });
  }

  private static class SpringConnection {

    private final Spring spring;
    private final MotionObserver<Float> observer;

    private SpringConnection(Spring spring, MotionObserver<Float> observer) {
      this.spring = spring;
      this.observer = observer;

      spring.addListener(springListener);

      propagate();
    }

    private void disconnect() {
      spring.removeListener(springListener);
    }

    private void propagate() {
      boolean isActive = !spring.isAtRest();

      if (isActive) {
        observer.state(MotionObservable.ACTIVE);
      }

      observer.next((float) spring.getCurrentValue());

      if (!isActive) {
        observer.state(MotionObservable.AT_REST);
      }
    }

    private final SpringListener springListener = new SimpleSpringListener() {

      @Override
      public void onSpringActivate(Spring spring) {
        propagate();
      }

      @Override
      public void onSpringUpdate(Spring spring) {
        propagate();
      }

      @Override
      public void onSpringAtRest(Spring spring) {
        propagate();
      }
    };
  }
}
