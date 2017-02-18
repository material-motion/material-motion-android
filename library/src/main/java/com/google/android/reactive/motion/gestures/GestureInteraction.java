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
package com.google.android.reactive.motion.gestures;

import android.view.View;

import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.reactive.motion.Interaction;
import com.google.android.reactive.motion.MotionObservable;
import com.google.android.reactive.motion.MotionRuntime;
import com.google.android.reactive.motion.sources.GestureSource;

/**
 * Abstract base class for all gesture interactions.
 */
public abstract class GestureInteraction<GR extends GestureRecognizer, T>
  extends Interaction<View, T> {

  public final GR gestureRecognizer;
  public final MotionObservable<GR> gestureStream;

  protected GestureInteraction(GR gestureRecognizer) {
    this.gestureRecognizer = gestureRecognizer;
    this.gestureStream = GestureSource.from(gestureRecognizer);
  }

  @Override
  public final void apply(MotionRuntime runtime, View target) {
    OnTouchListeners.add(target, gestureRecognizer);

    onApply(runtime, gestureStream, target);
  }

  /**
   * Applies the values of the gesture recognizer stream to the target view.
   */
  protected abstract void onApply(MotionRuntime runtime, MotionObservable<GR> stream, View target);

}
