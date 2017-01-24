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
package com.google.android.material.motion.streams.operators;

import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.RotateGestureRecognizer;
import com.google.android.material.motion.gestures.ScaleGestureRecognizer;
import com.google.android.material.motion.observable.Observer;
import com.google.android.material.motion.streams.MotionObservable.Operation;

/**
 * Created by markwei on 1/24/17.
 */
public final class GestureRecognizerOperators {

  @VisibleForTesting
  GestureRecognizerOperators() {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds the current translation to the initial translation of the given view and emits the
   * result while the gesture recognizer is active.
   */
  public static <T extends DragGestureRecognizer> Operation<T, Float[]> translated(final View view) {
    return new Operation<T, Float[]>() {

      private float initialTranslationX;
      private float initialTranslationY;

      @Override
      public void next(Observer<Float[]> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case GestureRecognizer.BEGAN:
            initialTranslationX = view.getTranslationX();
            initialTranslationY = view.getTranslationY();
            break;
          case GestureRecognizer.CHANGED:
            float translationX = gestureRecognizer.getTranslationX();
            float translationY = gestureRecognizer.getTranslationY();

            observer.next(
              new Float[]{initialTranslationX + translationX, initialTranslationY + translationY});
            break;
        }
      }
    };
  }

  /**
   * Adds the current rotation to the initial rotation of the given view and emits the result
   * while the gesture recognizer is active.
   */
  public static <T extends RotateGestureRecognizer> Operation<T, Float> rotated(final View view) {
    return new Operation<T, Float>() {

      private float initialRotation;

      @Override
      public void next(Observer<Float> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case GestureRecognizer.BEGAN:
            initialRotation = view.getRotation();
            break;
          case GestureRecognizer.CHANGED:
            float rotation = gestureRecognizer.getRotation();

            observer.next(initialRotation + rotation);
            break;
        }
      }
    };
  }

  /**
   * Multiplies the current scale onto the initial scale of the given view and emits the result
   * while the gesture recognizer is active.
   */
  public static <T extends ScaleGestureRecognizer> Operation<T, Float[]> scaled(final View view) {
    return new Operation<T, Float[]>() {

      private float initialScaleX;
      private float initialScaleY;

      @Override
      public void next(Observer<Float[]> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case GestureRecognizer.BEGAN:
            initialScaleX = view.getScaleX();
            initialScaleY = view.getScaleY();
            break;
          case GestureRecognizer.CHANGED:
            float scale = gestureRecognizer.getScale();

            observer.next(new Float[]{initialScaleX * scale, initialScaleY * scale});
            break;
        }
      }
    };
  }
}
