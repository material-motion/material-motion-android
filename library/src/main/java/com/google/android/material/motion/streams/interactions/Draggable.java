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
import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.SimpleMotionObserver;

/**
 * A draggable interaction.
 */
public class Draggable extends GestureInteraction {

  public Draggable() {
    this(new DragGestureRecognizer());
  }

  public Draggable(DragGestureRecognizer gestureRecognizer) {
    super(gestureRecognizer);
  }

  @Override
  public MotionObserver<GestureRecognizer> handle(final View target) {
    return new SimpleMotionObserver<GestureRecognizer>() {

      private float initialTranslationX;
      private float initialTranslationY;

      @Override
      public void next(GestureRecognizer gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case GestureRecognizer.BEGAN:
            initialTranslationX = target.getTranslationX();
            initialTranslationY = target.getTranslationY();
            break;
          case GestureRecognizer.CHANGED:
            float translationX = ((DragGestureRecognizer) gestureRecognizer).getTranslationX();
            float translationY = ((DragGestureRecognizer) gestureRecognizer).getTranslationY();

            target.setTranslationX(initialTranslationX + translationX);
            target.setTranslationY(initialTranslationY + translationY);
            break;
        }
      }
    };
  }
}
