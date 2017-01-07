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
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.observable.IndefiniteObservable.Connector;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.SimpleMotionObserver;
import com.google.android.material.motion.streams.springs.MaterialSpring;
import com.google.android.material.motion.streams.springs.MetaSpring;
import com.google.android.material.motion.streams.springs.MetaSpring.MetaSpringListener;
import com.google.android.material.motion.streams.springs.TypeVectorizer;

/**
 * A source for rebound springs.
 * <p>
 * Rebound springs only support animating between float values. This class supports arbitrary T
 * values by vectorizing the value into floats, and animating them individually using separate
 * rebound springs.
 */
public final class ReboundSpringSource extends SpringSource {

  private static final ReboundSpringSource SPRING_SOURCE = new ReboundSpringSource();
  private final SpringSystem springSystem = SpringSystem.create();

  /**
   * Creates a spring source for a float spring.
   * <p>
   * The properties on the <code>spring</code> param may be changed to dynamically modify the
   * behavior of this source.
   */
  public static MotionObservable<Float> from(MaterialSpring<Float> spring) {
    return SPRING_SOURCE.create(spring);
  }

  /**
   * Creates a spring source for a T valued spring.
   * <p>
   * The properties on the <code>spring</code> param may be changed to dynamically modify the
   * behavior of this source.
   */
  public static <T> MotionObservable<T> from(MaterialSpring<T> spring, TypeVectorizer<T> vectorizer) {
    return SPRING_SOURCE.create(spring, vectorizer);
  }

  @Override
  public <T> MotionObservable<T> create(
    final MaterialSpring<T> spring, final TypeVectorizer<T> vectorizer) {
    return new MotionObservable<>(new Connector<MotionObserver<T>>() {
      @NonNull
      @Override
      public IndefiniteObservable.Disconnector connect(MotionObserver<T> observer) {
        final SpringConfig springConfig = new SpringConfig(0, 0);

        spring.tension.subscribe(new SimpleMotionObserver<Float>() {
          @Override
          public void next(Float value) {
            springConfig.tension = OrigamiValueConverter.tensionFromOrigamiValue(value);
          }
        });

        spring.friction.subscribe(new SimpleMotionObserver<Float>() {
          @Override
          public void next(Float value) {
            springConfig.friction = OrigamiValueConverter.frictionFromOrigamiValue(value);
          }
        });

        final int count = vectorizer.getVectorLength();

        final Spring[] reboundSprings = new Spring[count];
        float[] initialValues = vectorizer.vectorize(spring.initialValue.read());

        for (int i = 0; i < count; i++) {
          reboundSprings[i] = springSystem.createSpring();
          reboundSprings[i].setSpringConfig(springConfig);
          reboundSprings[i].setCurrentValue(initialValues[i]);
        }

        spring.destination.subscribe(new SimpleMotionObserver<T>() {
          @Override
          public void next(T value) {
            float[] endValues = vectorizer.vectorize(value);

            for (int i = 0; i < count; i++) {
              reboundSprings[i].setEndValue(endValues[i]);
            }
          }
        });

        final SpringConnection<T> connection =
          new SpringConnection<>(reboundSprings, vectorizer, observer);
        return new IndefiniteObservable.Disconnector() {
          @Override
          public void disconnect() {
            connection.disconnect();
          }
        };
      }
    });
  }

  private static class SpringConnection<T> {

    private final MetaSpring spring;
    private final TypeVectorizer<T> vectorizer;
    private final MotionObserver<T> observer;

    private SpringConnection(
      Spring[] springs, TypeVectorizer<T> vectorizer, MotionObserver<T> observer) {
      this.spring = new MetaSpring(springs);
      this.vectorizer = vectorizer;
      this.observer = observer;

      this.spring.addListener(springListener);
    }

    private void disconnect() {
      spring.removeListener(springListener);
    }

    private final MetaSpringListener springListener = new MetaSpringListener() {
      @Override
      public void onMetaSpringActivate() {
        observer.state(MotionObservable.ACTIVE);
      }

      @Override
      public void onMetaSpringUpdate(float[] values) {
        T value = vectorizer.compose(values);
        observer.next(value);
      }

      @Override
      public void onMetaSpringAtRest() {
        observer.state(MotionObservable.AT_REST);
      }
    };
  }
}
