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
import com.google.android.material.motion.observable.IndefiniteObservable.Disconnector;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.SimpleMotionObserver;
import com.google.android.material.motion.streams.operators.CommonOperators;
import com.google.android.material.motion.streams.springs.CompositeReboundSpring;
import com.google.android.material.motion.streams.springs.CompositeReboundSpring.CompositeSpringListener;
import com.google.android.material.motion.streams.springs.MaterialSpring;
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
  public static <O> MotionObservable<Float> from(MaterialSpring<O, Float> spring) {
    return SPRING_SOURCE.create(spring);
  }

  /**
   * Creates a spring source for a T valued spring.
   * <p>
   * The properties on the <code>spring</code> param may be changed to dynamically modify the
   * behavior of this source.
   */
  public static <O, T> MotionObservable<T> from(MaterialSpring<O, T> spring, TypeVectorizer<T> vectorizer) {
    return SPRING_SOURCE.create(spring, vectorizer);
  }

  @Override
  public <O, T> MotionObservable<T> create(
    final MaterialSpring<O, T> spring, final TypeVectorizer<T> vectorizer) {
    return new MotionObservable<>(new Connector<MotionObserver<T>>() {

      private Spring[] reboundSprings;

      private Subscription destinationSubscription;
      private Subscription frictionSubscription;
      private Subscription tensionSubscription;

      @NonNull
      @Override
      public Disconnector connect(MotionObserver<T> observer) {
        reboundSprings = new Spring[vectorizer.getVectorLength()];
        for (int i = 0; i < reboundSprings.length; i++) {
          reboundSprings[i] = springSystem.createSpring();
        }

        final Subscription enabledSubscription =
          spring.enabled.getStream()
            .compose(CommonOperators.<Boolean>dedupe())
            .subscribe(new SimpleMotionObserver<Boolean>() {
              @Override
              public void next(Boolean enabled) {
                if (enabled) {
                  start();
                } else {
                  stop();
                }
              }
            });

        final SpringConnection<T> connection =
          new SpringConnection<>(reboundSprings, vectorizer, observer);

        return new IndefiniteObservable.Disconnector() {
          @Override
          public void disconnect() {
            connection.disconnect();
            enabledSubscription.unsubscribe();
            stop();
          }
        };
      }

      private void start() {
        final SpringConfig springConfig = new SpringConfig(0, 0);
        tensionSubscription = spring.tension.subscribe(new SimpleMotionObserver<Float>() {
          @Override
          public void next(Float value) {
            springConfig.tension = OrigamiValueConverter.tensionFromOrigamiValue(value);
          }
        });
        frictionSubscription = spring.friction.subscribe(new SimpleMotionObserver<Float>() {
          @Override
          public void next(Float value) {
            springConfig.friction = OrigamiValueConverter.frictionFromOrigamiValue(value);
          }
        });

        float[] initialValues = new float[reboundSprings.length];
        vectorizer.vectorize(spring.initialValue.read(), initialValues);

        float[] initialVelocities = new float[reboundSprings.length];
        vectorizer.vectorize(spring.initialVelocity.read(), initialVelocities);

        for (int i = 0; i < reboundSprings.length; i++) {
          reboundSprings[i].setSpringConfig(springConfig);
          reboundSprings[i].setCurrentValue(initialValues[i]);
          reboundSprings[i].setVelocity(initialVelocities[i]);
        }

        final float[] endValues = new float[reboundSprings.length];
        destinationSubscription = spring.destination.subscribe(new SimpleMotionObserver<T>() {
          @Override
          public void next(T value) {
            vectorizer.vectorize(value, endValues);

            for (int i = 0; i < reboundSprings.length; i++) {
              reboundSprings[i].setEndValue(endValues[i]);
            }
          }
        });
      }

      private void stop() {
        tensionSubscription.unsubscribe();
        frictionSubscription.unsubscribe();
        destinationSubscription.unsubscribe();

        for (int i = 0; i < reboundSprings.length; i++) {
          reboundSprings[i].setAtRest();
        }
      }
    });
  }

  private static class SpringConnection<T> {

    private final CompositeReboundSpring spring;
    private final TypeVectorizer<T> vectorizer;
    private final MotionObserver<T> observer;

    private SpringConnection(
      Spring[] springs, TypeVectorizer<T> vectorizer, MotionObserver<T> observer) {
      this.spring = new CompositeReboundSpring(springs);
      this.vectorizer = vectorizer;
      this.observer = observer;

      this.spring.addListener(springListener);
    }

    private void disconnect() {
      spring.removeListener(springListener);
    }

    private final CompositeSpringListener springListener = new CompositeSpringListener() {
      @Override
      public void onCompositeSpringActivate() {
        observer.state(MotionObservable.ACTIVE);
      }

      @Override
      public void onCompositeSpringUpdate(float[] values) {
        T value = vectorizer.compose(values);
        observer.next(value);
      }

      @Override
      public void onCompositeSpringAtRest() {
        observer.state(MotionObservable.AT_REST);
      }
    };
  }
}
