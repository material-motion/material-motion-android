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
package com.google.android.material.motion.interactions;

import android.graphics.PointF;
import android.util.Property;
import android.view.View;

import com.google.android.material.motion.ConstraintApplicator;
import com.google.android.material.motion.Interaction;
import com.google.android.material.motion.MotionRuntime;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.sources.DynamicSpringSource;
import com.google.android.material.motion.sources.PhysicsSpringSource;
import com.google.android.material.motion.springs.PointFTypeVectorizer;

import static com.google.android.material.motion.operators.IsAtRest.isAtRest;
import static com.google.android.material.motion.operators.Velocity.velocity;

public class Tossable extends Interaction<View, PointF> {

  public final Draggable draggable;
  private final Property<View, PointF> springProperty;
  public final ReactiveProperty<PointF> anchor;

  public Tossable(Property<View, PointF> springProperty, ReactiveProperty<PointF> anchor) {
    this(new Draggable(), springProperty, anchor);
  }

  public Tossable(
    Draggable draggable, Property<View, PointF> springProperty, ReactiveProperty<PointF> anchor) {
    this.draggable = draggable;
    this.springProperty = springProperty;
    this.anchor = anchor;

    draggable.gestureRecognizer.dragSlop = 0;
  }

  @Override
  public void apply(MotionRuntime runtime, View target, ConstraintApplicator<PointF> constraints) {
    // TODO: Make spring a field so it can be customized.
    MaterialSpring<View, PointF> spring = new MaterialSpring<>(
      springProperty,
      new PointFTypeVectorizer(),
      anchor,
      ReactiveProperty.of(target, springProperty),
      ReactiveProperty.of(new PointF(0f, 0f)),
      ReactiveProperty.of(1f),
      ReactiveProperty.of(1f),
      ReactiveProperty.of(4f),
      DynamicSpringSource.SYSTEM);

    runtime.addInteraction(draggable, target, constraints);
    // TODO: Cannot apply constraints to spring because while draggable acts on translation
    // (0-relative), spring acts on position (0-absolute).
    runtime.addInteraction(spring, target);

    runtime.write(draggable.gestureStream.compose(velocity()), spring.initialVelocity);
    runtime.write(draggable.gestureStream.compose(isAtRest()), spring.enabled);
  }
}
