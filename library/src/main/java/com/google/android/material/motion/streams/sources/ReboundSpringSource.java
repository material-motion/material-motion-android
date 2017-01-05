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

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.observable.IndefiniteObservable.Connector;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;

/**
 * A source for rebound springs.
 */
public final class ReboundSpringSource extends SpringSource {

  private static final ReboundSpringSource SPRING_SOURCE = new ReboundSpringSource();

  /**
   * Creates a spring source that will connect to the provided rebound spring.
   */
  public static MotionObservable<Double> from(final Spring spring) {
    return SPRING_SOURCE.create(spring);
  }

  /**
   * Creates a spring source that will connect to the provided rebound spring.
   */
  public MotionObservable<Double> create(final Spring spring) {
    return new MotionObservable<>(new Connector<MotionObserver<Double>>() {
      @NonNull
      @Override
      public IndefiniteObservable.Disconnector connect(MotionObserver<Double> observer) {
        final SpringConnection connection = new SpringConnection(spring, observer);
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
    private final MotionObserver<Double> observer;

    private SpringConnection(Spring spring, MotionObserver<Double> observer) {
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

      observer.next(spring.getCurrentValue());

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
