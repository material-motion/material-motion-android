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
package com.google.android.material.motion.streams.interactions;

import android.view.View;

import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.streams.Interaction;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.ReactiveProperty;
import com.google.android.material.motion.streams.operators.GestureOperators;
import com.google.android.material.motion.streams.properties.ViewProperties;
import com.google.android.material.motion.streams.springs.FloatArrayTypeVectorizer;
import com.google.android.material.motion.streams.springs.MaterialSpring;

import static com.google.android.material.motion.gestures.GestureRecognizer.BEGAN;
import static com.google.android.material.motion.gestures.GestureRecognizer.CANCELLED;
import static com.google.android.material.motion.gestures.GestureRecognizer.CHANGED;
import static com.google.android.material.motion.gestures.GestureRecognizer.POSSIBLE;
import static com.google.android.material.motion.gestures.GestureRecognizer.RECOGNIZED;
import static com.google.android.material.motion.streams.operators.GestureOperators.onRecognitionState;
import static com.google.android.material.motion.streams.operators.GestureOperators.velocity;
import static com.google.android.material.motion.streams.operators.InteractionOperators.disable;
import static com.google.android.material.motion.streams.operators.InteractionOperators.enable;

public class Tossable extends Interaction<View, Void> {

  public final Draggable draggable;
  public final ReactiveProperty<Float[]> destination;

  public Tossable(ReactiveProperty<Float[]> destination) {
    this(new Draggable(), destination);
  }

  public Tossable(Draggable draggable, ReactiveProperty<Float[]> destination) {
    this.draggable = draggable;
    this.destination = destination;
  }

  @Override
  public void apply(MotionRuntime runtime, View target) {
    ReactiveProperty<Float[]> initialVelocity = ReactiveProperty.of(new Float[]{0f, 0f});

    MaterialSpring<View, Float[]> spring = new MaterialSpring<>(
      ViewProperties.TRANSLATION,
      new FloatArrayTypeVectorizer(2),
      destination,
      ReactiveProperty.of(target, ViewProperties.TRANSLATION),
      initialVelocity,
      ReactiveProperty.of(1f),
      ReactiveProperty.of(1f),
      ReactiveProperty.of(4f));

    runtime.addInteraction(draggable, target);
    runtime.addInteraction(spring, target);

    write(runtime,
      draggable.gestureStream
        .compose(GestureOperators.<DragGestureRecognizer>onRecognitionState(RECOGNIZED))
        .compose(velocity()),
      initialVelocity);

    MotionObservable<Object> disableStream = draggable.gestureStream
      .compose(onRecognitionState(BEGAN, CHANGED))
      .compose(disable(spring));

    MotionObservable<Object> enableStream = draggable.gestureStream
      .compose(onRecognitionState(POSSIBLE, RECOGNIZED, CANCELLED))
      .compose(enable(spring));

    write(runtime, disableStream, null, null);
    write(runtime, enableStream, null, null);
  }
}
