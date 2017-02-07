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
package com.google.android.material.motion.streams.springs;

import com.google.android.material.motion.streams.ReactiveProperty;
import com.google.android.material.motion.streams.ReactiveReadable;

/**
 * A spring can pull a value from an initial position to a destination using a physical simulation.
 * <p>
 * This class defines the spring type for use in creating a spring source.
 */
public class MaterialSpring<T> {

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
  public static final ReactiveReadable<Float> DEFAULT_TENSION_PROPERTY = ReactiveProperty.of(DEFAULT_TENSION);

  /**
   * The default spring friction coefficient. Represents {@link #DEFAULT_FRICTION}.
   * <p>
   * Default extracted from a POP spring with speed = 12 and bounciness = 4.
   */
  public static final ReactiveReadable<Float> DEFAULT_FRICTION_PROPERTY = ReactiveProperty.of(DEFAULT_FRICTION);

  /**
   * The destination value of the spring represented as a property.
   */
  public final ReactiveProperty<T> destination;

  /**
   * The initial value of the spring represented as a readable.
   */
  public final ReactiveReadable<T> initialValue;

  /**
   * The initial velocity of the spring represented as a readable.
   */
  public final ReactiveReadable<T> initialVelocity;

  /**
   * The value used when determining completion of the spring simulation.
   */
  public final ReactiveReadable<Float> threshold;

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
    this.destination = ReactiveProperty.of(destination);
    this.initialValue = ReactiveProperty.of(initialValue);
    this.initialVelocity = ReactiveProperty.of(initialVelocity);
    this.threshold = ReactiveProperty.of(threshold);
    this.tension = ReactiveProperty.of(tension);
    this.friction = ReactiveProperty.of(friction);
  }

  /**
   * Creates a spring with the provided properties.
   */
  public MaterialSpring(
    ReactiveProperty<T> destination,
    ReactiveReadable<T> initialValue,
    ReactiveReadable<T> initialVelocity,
    ReactiveReadable<Float> threshold,
    ReactiveProperty<Float> tension,
    ReactiveProperty<Float> friction) {
    this.destination = destination;
    this.initialValue = initialValue;
    this.initialVelocity = initialVelocity;
    this.threshold = threshold;
    this.tension = tension == DEFAULT_TENSION_PROPERTY
      ? ReactiveProperty.of(DEFAULT_TENSION) : tension;
    this.friction = friction == DEFAULT_FRICTION_PROPERTY
      ? ReactiveProperty.of(DEFAULT_FRICTION) : friction;
  }
}
