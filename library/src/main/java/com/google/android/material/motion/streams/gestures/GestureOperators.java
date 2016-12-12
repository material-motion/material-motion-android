/*
 * Copyright 2016-present The Material Motion Authors. All Rights Reserved.
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
package com.google.android.material.motion.streams.gestures;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureRecognizerState;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.Operation;
import com.google.android.material.motion.streams.MotionObservable.Predicate;
import com.google.android.material.motion.streams.MotionObservable.Transformation;

/**
 * Extended operators for gestures.
 *
 * @see MotionObservable#operator(Operation)
 */
public final class GestureOperators {

  @VisibleForTesting
  GestureOperators() {
    throw new UnsupportedOperationException();
  }

  /**
   * Extract centroid from the incoming gesture recognizer stream.
   *
   * @see MotionObservable#extend(Operation)
   */
  public static <T extends GestureRecognizer> Operation<T, PointF> centroid() {
    return new Transformation<T, PointF>() {
      @Override
      public PointF transform(T value) {
        return new PointF(value.getCentroidX(), value.getCentroidY());
      }
    };
  }

  /**
   * Only forwards the gesture recognizer if its state matches the provided value.
   */
  public static <T extends GestureRecognizer> Operation<T, T> onRecognitionState(
    @GestureRecognizerState final int state) {
    return new Predicate<T>() {
      @Override
      public boolean evaluate(T value) {
        return value.getState() == state;
      }
    };
  }

  /**
   * Only forwards the gesture recognizer if its state matches any of the provided values.
   */
  public static <T extends GestureRecognizer> Operation<T, T> onRecognitionState(
    @GestureRecognizerState final int... states) {
    return new Predicate<T>() {
      @Override
      public boolean evaluate(T value) {
        int s = value.getState();
        for (@GestureRecognizerState int state : states) {
          if (state == s) {
            return true;
          }
        }
        return false;
      }
    };
  }
}
