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
package com.google.android.reactive.motion.sources;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.physics.Integrator;
import com.google.android.material.motion.physics.forces.Spring;
import com.google.android.material.motion.physics.integrators.Rk4Integrator;
import com.google.android.material.motion.physics.math.Vector;
import com.google.android.reactive.motion.MotionObservable;
import com.google.android.reactive.motion.MotionObservable.MotionObserver;
import com.google.android.reactive.motion.MotionObservable.SimpleMotionObserver;
import com.google.android.reactive.motion.interactions.MaterialSpring;

/**
 * A source for physics springs.
 */
public final class PhysicsSpringSource<T> extends SpringSource<T> {

  public static final System SYSTEM = new System() {
    @Override
    public <T> SpringSource<T> create(MaterialSpring<?, T> spring) {
      return new PhysicsSpringSource<>(spring);
    }
  };

  private final MaterialSpring<?, T> spring;

  private Integrator integrator;
  private Spring springForce;

  private Subscription destinationSubscription;
  private Subscription frictionSubscription;
  private Subscription tensionSubscription;

  public PhysicsSpringSource(MaterialSpring<?, T> spring) {
    super(spring);
    this.spring = spring;
  }

  @Override
  protected void onConnect(final MotionObserver<T> observer) {
    integrator = new Rk4Integrator();
    springForce = new Spring();
    integrator.addForce(springForce);

    integrator.addListener(new Integrator.SimpleListener() {
      @Override
      public void onStart() {
        observer.state(MotionObservable.ACTIVE);
      }

      @Override
      public void onUpdate(Vector x, Vector v) {
        T value = spring.vectorizer.compose(x.getValues());
        observer.next(value);
      }

      @Override
      public void onStop() {
        observer.state(MotionObservable.AT_REST);
      }
    });
  }

  @Override
  protected void onEnable(MotionObserver<T> observer) {
    tensionSubscription = spring.tension.subscribe(new SimpleMotionObserver<Float>() {
      @Override
      public void next(Float value) {
        springForce.k = Spring.tensionFromOrigamiValue(value);
        integrator.start();
      }
    });
    frictionSubscription = spring.friction.subscribe(new SimpleMotionObserver<Float>() {
      @Override
      public void next(Float value) {
        springForce.b = Spring.frictionFromOrigamiValue(value);
        integrator.start();
      }
    });

    final int count = spring.vectorizer.getVectorLength();

    float[] initialValues = new float[count];
    spring.vectorizer.vectorize(spring.initialValue.read(), initialValues);

    float[] initialVelocities = new float[count];
    spring.vectorizer.vectorize(spring.initialVelocity.read(), initialVelocities);

    for (int i = 0; i < count; i++) {
      integrator.setState(new Vector(initialValues), new Vector(initialVelocities));
    }

    final float[] endValues = new float[count];
    destinationSubscription = spring.destination.subscribe(new SimpleMotionObserver<T>() {
      @Override
      public void next(T value) {
        spring.vectorizer.vectorize(value, endValues);

        springForce.setAnchorPoint(new Vector(endValues));
        integrator.start();
      }
    });

    integrator.start();
  }

  @Override
  protected void onDisable(MotionObserver<T> observer) {
    integrator.stop();

    tensionSubscription.unsubscribe();
    frictionSubscription.unsubscribe();
    destinationSubscription.unsubscribe();
  }
}
