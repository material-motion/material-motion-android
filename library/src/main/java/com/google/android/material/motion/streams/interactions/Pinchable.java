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

import com.google.android.material.motion.gestures.ScaleGestureRecognizer;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.SimpleMotionObserver;

import static com.google.android.material.motion.streams.operators.GestureRecognizerOperators.scaled;

/**
 * A pinchable interaction.
 */
public class Pinchable extends GestureInteraction<ScaleGestureRecognizer> {

  public Pinchable() {
    this(new ScaleGestureRecognizer());
  }

  public Pinchable(ScaleGestureRecognizer gestureRecognizer) {
    super(gestureRecognizer);
  }

  @Override
  protected void apply(MotionObservable<ScaleGestureRecognizer> stream, final View target) {
    stream.compose(scaled(target)).subscribe(new SimpleMotionObserver<Float[]>() {
      @Override
      public void next(Float[] value) {
        target.setScaleX(value[0]);
        target.setScaleY(value[1]);
      }
    });
  }
}
