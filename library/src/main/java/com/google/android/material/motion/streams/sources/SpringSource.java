/*
 * Copyright 2017-present The Material Motion Authors. All Rights Reserved.
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

import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.ConstantProperty;
import com.google.android.material.motion.streams.MotionObservable.ScopedReadable;
import com.google.android.material.motion.streams.ReactiveProperty;
import com.google.android.material.motion.streams.ReactiveProperty.ValueReactiveProperty;

/**
 * The abstract base class for all spring sources.
 */
public abstract class SpringSource {

  /**
   * A spring can pull a value from an initial position to a destination using a physical
   * simulation.
   * <p>
   * This class defines the spring type for use in creating a spring source.
   */
  public static class MaterialSpring<T> {

    /**
     * The default spring tension coefficient.
     * <p>
     * Default extracted from a POP spring with speed = 12 and bounciness = 4.
     */
    public static final float DEFAULT_TENSION = 342f;

    /**
     * The default spring friction coefficient.
     * <p>
     * Default extracted from a POP spring with speed = 12 and bounciness = 4.
     */
    public static final float DEFAULT_FRICTION = 30f;

    /**
     * The default spring tension coefficient. Represents {@link #DEFAULT_TENSION}.
     * <p>
     * Default extracted from a POP spring with speed = 12 and bounciness = 4.
     */
    public static final ReactiveProperty<Float> DEFAULT_TENSION_PROPERTY = null;

    /**
     * The default spring friction coefficient. Represents {@link #DEFAULT_FRICTION}.
     * <p>
     * Default extracted from a POP spring with speed = 12 and bounciness = 4.
     */
    public static final ReactiveProperty<Float> DEFAULT_FRICTION_PROPERTY = null;

    /**
     * The destination value of the spring represented as a property.
     */
    public final ReactiveProperty<T> destination;

    /**
     * The initial value of the spring represented as a readable.
     */
    public final ScopedReadable<T> initialValue;

    /**
     * The initial velocity of the spring represented as a readable.
     */
    public final ScopedReadable<T> initialVelocity;

    /**
     * The value used when determining completion of the spring simulation.
     */
    public final ScopedReadable<Float> threshold;

    /**
     * The tension coefficient of the spring represented as a property.
     */
    public final ReactiveProperty<Float> tension;

    /**
     * The friction coefficient of the spring represented as a property.
     */
    public final ReactiveProperty<Float> friction;

    /**
     * Creates a spring with the provided values.
     */
    public MaterialSpring(
      T destination,
      T initialValue,
      T initialVelocity,
      float threshold,
      float tension,
      float friction) {
      this.destination = new ValueReactiveProperty<>(destination);
      this.initialValue = new ConstantProperty<>(initialValue);
      this.initialVelocity = new ConstantProperty<>(initialVelocity);
      this.threshold = new ConstantProperty<>(threshold);
      this.tension = new ValueReactiveProperty<>(tension);
      this.friction = new ValueReactiveProperty<>(friction);
    }

    /**
     * Creates a spring with the provided properties.
     */
    public MaterialSpring(
      ReactiveProperty<T> destination,
      ScopedReadable<T> initialValue,
      ScopedReadable<T> initialVelocity,
      ScopedReadable<Float> threshold,
      ReactiveProperty<Float> tension,
      ReactiveProperty<Float> friction) {
      this.destination = destination;
      this.initialValue = initialValue;
      this.initialVelocity = initialVelocity;
      this.threshold = threshold;
      this.tension = tension == DEFAULT_TENSION_PROPERTY
        ? new ValueReactiveProperty<>(DEFAULT_TENSION) : tension;
      this.friction = friction == DEFAULT_FRICTION_PROPERTY
        ? new ValueReactiveProperty<>(DEFAULT_FRICTION) : friction;
    }
  }

  /**
   * Creates a spring source for a float spring.
   */
  public abstract MotionObservable<Float> create(MaterialSpring<Float> spring);
}
