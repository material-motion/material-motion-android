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

import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.Operation;
import com.google.android.material.motion.streams.MotionObservable.Transformation;

/**
 * Extended operators for gestures.
 *
 * @see MotionObservable#operator(Operation)
 */
public final class GestureOperators {

  /**
   * Extract centroid from the incoming gesture recognizer stream.
   *
   * @see MotionObservable#extend(Operation)
   */
  public static Operation<GestureRecognizer, PointF> centroid() {
    return new Transformation<GestureRecognizer, PointF>() {
      @Override
      public PointF transform(GestureRecognizer value) {
        return new PointF(value.getCentroidX(), value.getCentroidY());
      }
    };
  }
}
