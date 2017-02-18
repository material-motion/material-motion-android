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
package com.google.android.reactive.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.reactive.motion.MotionObservable;
import com.google.android.reactive.motion.MotionObservable.MapOperation;
import com.google.android.reactive.motion.MotionObservable.Operation;

/**
 * Extended operators for PointF.
 *
 * @see MotionObservable#compose(Operation)
 */
public final class PointFOperators {

  @VisibleForTesting
  PointFOperators() {
    throw new UnsupportedOperationException();
  }

  /**
   * Extract the x value from the incoming PointF stream.
   */
  public static Operation<PointF, Float> x() {
    return new MapOperation<PointF, Float>() {
      @Override
      public Float transform(PointF value) {
        return value.x;
      }
    };
  }

  /**
   * Extract the y value from the incoming PointF stream.
   */
  public static Operation<PointF, Float> y() {
    return new MapOperation<PointF, Float>() {
      @Override
      public Float transform(PointF value) {
        return value.y;
      }
    };
  }
}
