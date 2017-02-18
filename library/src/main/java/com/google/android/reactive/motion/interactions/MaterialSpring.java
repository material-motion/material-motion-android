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
package com.google.android.reactive.motion.interactions;

import android.util.Property;

import com.google.android.reactive.motion.Interaction;
import com.google.android.reactive.motion.MotionObservable;
import com.google.android.reactive.motion.MotionRuntime;
import com.google.android.reactive.motion.ReactiveProperty;
import com.google.android.reactive.motion.ReactiveReadable;
import com.google.android.reactive.motion.sources.SpringSource;
import com.google.android.reactive.motion.springs.TypeVectorizer;

/**
 * A spring can pull a value from an initial position to a destination using a physical simulation.
 * <p>
 * This class defines the spring type for use in creating a spring source.
 */
public class MaterialSpring<O, T> extends Interaction<O, T> {

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
  public final ReactiveProperty<T> initialValue;

  /**
   * The initial velocity of the spring represented as a readable.
   */
  public final ReactiveProperty<T> initialVelocity;

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

  public final TypeVectorizer<T> vectorizer;
  private final Property<O, T> property;
  private final MotionObservable<T> stream;

  /**
   * Creates a spring with the provided values.
   */
  public MaterialSpring(
    Property<O, T> property,
    TypeVectorizer<T> vectorizer,
    T destination,
    T initialValue,
    T initialVelocity,
    float threshold,
    float tension,
    float friction,
    SpringSource springSource) {
    this(property,
      vectorizer,
      ReactiveProperty.of(destination),
      ReactiveProperty.of(initialValue),
      ReactiveProperty.of(initialVelocity),
      ReactiveProperty.of(threshold),
      ReactiveProperty.of(tension),
      ReactiveProperty.of(friction),
      springSource);
  }

  /**
   * Creates a spring with the provided properties.
   */
  public MaterialSpring(
    Property<O, T> property,
    TypeVectorizer<T> vectorizer,
    ReactiveProperty<T> destination,
    ReactiveProperty<T> initialValue,
    ReactiveProperty<T> initialVelocity,
    ReactiveReadable<Float> threshold,
    ReactiveProperty<Float> tension,
    ReactiveProperty<Float> friction,
    SpringSource springSource) {
    this.property = property;
    this.vectorizer = vectorizer;
    this.destination = destination;
    this.initialValue = initialValue;
    this.initialVelocity = initialVelocity;
    this.threshold = threshold;
    this.tension = tension;
    this.friction = friction;

    this.stream = springSource.create(this);
  }

  @Override
  public void apply(MotionRuntime runtime, O target) {
    runtime.write(flatten(stream), target, property);
  }
}
