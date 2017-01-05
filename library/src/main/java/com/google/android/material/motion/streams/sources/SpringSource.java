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
     * The destination value of the spring represented as a property.
     */
    public final ReactiveProperty<T> destination;

    /**
     * The initial value of the spring represented as a property.
     */
    public final ReactiveProperty<T> initialValue;

    /**
     * The initial velocity of the spring represented as a property.
     */
    public final ReactiveProperty<T> initialVelocity;

    /**
     * The value used when determining completion of the spring simulation.
     */
    public final ReactiveProperty<Float> threshold;

    /**
     * The configuration of the spring represented as a property.
     */
    public final ReactiveProperty<SpringConfiguration> configuration;

    /**
     * Creates a spring with the provided values.
     */
    public MaterialSpring(
      T destination,
      T initialValue,
      T initialVelocity,
      float threshold,
      SpringConfiguration configuration) {
      this.destination = new ValueReactiveProperty<>(destination);
      this.initialValue = new ValueReactiveProperty<>(initialValue);
      this.initialVelocity = new ValueReactiveProperty<>(initialVelocity);
      this.threshold = new ValueReactiveProperty<>(threshold);
      this.configuration = new ValueReactiveProperty<>(configuration);
    }

    /**
     * Creates a spring with the provided properties.
     */
    public MaterialSpring(
      ReactiveProperty<T> destination,
      ReactiveProperty<T> initialValue,
      ReactiveProperty<T> initialVelocity,
      ReactiveProperty<Float> threshold,
      ReactiveProperty<SpringConfiguration> configuration) {
      this.destination = destination;
      this.initialValue = initialValue;
      this.initialVelocity = initialVelocity;
      this.threshold = threshold;
      this.configuration = configuration;
    }
  }

  /**
   * Configure the spring traits.
   */
  public static class SpringConfiguration {

    /**
     * The default spring configuration. Default extracted from a POP spring with speed = 12 and
     * bounciness = 4.
     */
    public static final SpringConfiguration DEFAULT = new SpringConfiguration(342, 30);

    /**
     * The tension coefficient for the property's spring.
     */
    public final float tension;

    /**
     * The friction coefficient for the property's spring.
     */
    public final float friction;

    /**
     * Initializes the configuration with a given tension and friction.
     */
    public SpringConfiguration(float tension, float friction) {
      this.tension = tension;
      this.friction = friction;
    }
  }
}
