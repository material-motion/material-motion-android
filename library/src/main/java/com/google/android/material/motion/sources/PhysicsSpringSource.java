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
package com.google.android.material.motion.sources;

import android.support.v4.util.SimpleArrayMap;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionObserver.SimpleMotionObserver;
import com.google.android.material.motion.MotionState;
import com.google.android.material.motion.interactions.MaterialSpring;
import com.google.android.material.motion.physics.Integrator;
import com.google.android.material.motion.physics.Integrator.Listener;
import com.google.android.material.motion.physics.Integrator.SimpleListener;
import com.google.android.material.motion.physics.forces.Spring;
import com.google.android.material.motion.physics.integrators.Rk4Integrator;
import com.google.android.material.motion.physics.math.Vector;

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

  private final MaterialSpring<?, T> interaction;

  private final Integrator integrator;
  private final Spring springForce;
  private final SimpleArrayMap<Observer<T>, Listener> integratorListeners = new SimpleArrayMap<>();

  private Subscription destinationSubscription;
  private Subscription frictionSubscription;
  private Subscription tensionSubscription;

  public PhysicsSpringSource(MaterialSpring<?, T> interaction) {
    super(interaction);
    this.interaction = interaction;
    integrator = new Rk4Integrator();
    springForce = new Spring();
    integrator.addForce(springForce);
    integrator.addListener(new SimpleListener() {
      @Override
      public void onStart() {
        for (int i = 0, count = integratorListeners.size(); i < count; i++) {
          integratorListeners.valueAt(i).onStart();
        }
      }

      @Override
      public void onUpdate(Vector x, Vector v) {
        for (int i = 0, count = integratorListeners.size(); i < count; i++) {
          integratorListeners.valueAt(i).onUpdate(x, v);
        }
      }

      @Override
      public void onStop() {
        for (int i = 0, count = integratorListeners.size(); i < count; i++) {
          integratorListeners.valueAt(i).onStop();
        }
      }
    });
  }

  @Override
  protected void onConnect(final MotionObserver<T> observer) {
    integratorListeners.put(observer, new SimpleListener() {

      @Override
      public void onStart() {
        interaction.state.write(MotionState.ACTIVE);
      }

      @Override
      public void onUpdate(Vector x, Vector v) {
        T value = interaction.vectorizer.compose(x.getValues());
        observer.next(value);
      }

      @Override
      public void onStop() {
        interaction.state.write(MotionState.AT_REST);
      }
    });
  }

  @Override
  protected void onEnable() {
    tensionSubscription = interaction.tension.subscribe(new SimpleMotionObserver<Float>() {
      @Override
      public void next(Float value) {
        springForce.k = Spring.tensionFromOrigamiValue(value);
        integrator.start();
      }
    });
    frictionSubscription = interaction.friction.subscribe(new SimpleMotionObserver<Float>() {
      @Override
      public void next(Float value) {
        springForce.b = Spring.frictionFromOrigamiValue(value);
        integrator.start();
      }
    });

    final int count = interaction.vectorizer.getVectorLength();

    float[] initialValues = new float[count];
    interaction.vectorizer.vectorize(interaction.initialValue.read(), initialValues);

    float[] initialVelocities = new float[count];
    interaction.vectorizer.vectorize(interaction.initialVelocity.read(), initialVelocities);

    for (int i = 0; i < count; i++) {
      integrator.setState(new Vector(initialValues), new Vector(initialVelocities));
    }

    final float[] endValues = new float[count];
    destinationSubscription = interaction.destination.subscribe(new SimpleMotionObserver<T>() {
      @Override
      public void next(T value) {
        interaction.vectorizer.vectorize(value, endValues);

        springForce.setAnchorPoint(new Vector(endValues));
        integrator.start();
      }
    });

    integrator.start();
  }

  @Override
  protected void onDisable() {
    integrator.stop();

    tensionSubscription.unsubscribe();
    frictionSubscription.unsubscribe();
    destinationSubscription.unsubscribe();
  }

  @Override
  protected void onDisconnect(MotionObserver<T> observer) {
    integratorListeners.remove(observer);
  }
}
