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
package com.google.android.material.motion.operators;

import android.graphics.RectF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class FloatArrayOperators {

  @VisibleForTesting
  FloatArrayOperators() {
    throw new UnsupportedOperationException();
  }

  /**
   * Extract the x value from the incoming Float[] stream.
   */
  public static Operation<Float[], Float> x() {
    return new MapOperation<Float[], Float>() {
      @Override
      public Float transform(Float[] value) {
        return value[0];
      }
    };
  }

  /**
   * Extract the y value from the incoming Float[] stream.
   */
  public static Operation<Float[], Float> y() {
    return new MapOperation<Float[], Float>() {
      @Override
      public Float transform(Float[] value) {
        return value[1];
      }
    };
  }
}
