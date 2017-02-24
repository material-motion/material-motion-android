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
import android.view.View;

import com.google.android.reactive.motion.Interaction;
import com.google.android.reactive.motion.MotionRuntime;
import com.google.android.reactive.motion.ReactiveProperty;
import com.google.android.reactive.motion.sources.PhysicsSpringSource;
import com.google.android.reactive.motion.springs.FloatArrayTypeVectorizer;

import static com.google.android.reactive.motion.operators.GestureOperators.isAtRest;
import static com.google.android.reactive.motion.operators.GestureOperators.velocity;

public class Tossable extends Interaction<View, Void> {

  public final Draggable draggable;
  private final Property<View, Float[]> springProperty;
  public final ReactiveProperty<Float[]> anchor;

  public Tossable(Property<View, Float[]> springProperty, ReactiveProperty<Float[]> anchor) {
    this(new Draggable(), springProperty, anchor);
  }

  public Tossable(
    Draggable draggable, Property<View, Float[]> springProperty, ReactiveProperty<Float[]> anchor) {
    this.draggable = draggable;
    this.springProperty = springProperty;
    this.anchor = anchor;

    draggable.gestureRecognizer.dragSlop = 0;
  }

  @Override
  public void apply(MotionRuntime runtime, View target) {
    // TODO: Make spring a field so it can be customized.
    MaterialSpring<View, Float[]> spring = new MaterialSpring<>(
      springProperty,
      new FloatArrayTypeVectorizer(2),
      anchor,
      ReactiveProperty.of(target, springProperty),
      ReactiveProperty.of(new Float[]{0f, 0f}),
      ReactiveProperty.of(1f),
      ReactiveProperty.of(1f),
      ReactiveProperty.of(4f),
      PhysicsSpringSource.SYSTEM);

    runtime.addInteraction(draggable, target);
    runtime.addInteraction(spring, target);

    runtime.write(draggable.gestureStream.compose(velocity()), spring.initialVelocity);
    runtime.write(draggable.gestureStream.compose(isAtRest()), spring.enabled);
  }
}
