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

import com.google.android.material.motion.MotionObservable.MapOperation;
import com.google.android.material.motion.MotionObservable.Operation;

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

  /**
   * For an incoming translational Float[] stream, overwrites the x value with the given xValue.
   */
  public static Operation<Float[], Float[]> lockToYAxis(final float xValue) {
    return new MapOperation<Float[], Float[]>() {
      @Override
      public Float[] transform(Float[] value) {
        float x = xValue;
        float y = value[1];
        return new Float[]{x, y};
      }
    };
  }

  /**
   * For an incoming translational Float[] stream, overwrites the y value with the given yValue.
   */
  public static Operation<Float[], Float[]> lockToXAxis(final float yValue) {
    return new MapOperation<Float[], Float[]>() {
      @Override
      public Float[] transform(Float[] value) {
        float x = value[0];
        float y = yValue;
        return new Float[]{x, y};
      }
    };
  }

  /**
   * Applies resistance to values that fall outside of the given rect. Resistance increases until
   * the distance reaches length, where resistance becomes infinite.
   */
  public static Operation<Float[], Float[]> rubberBanded(final RectF rect, final float length) {
    return new MapOperation<Float[], Float[]>() {
      @Override
      public Float[] transform(Float[] value) {
        float x = rubberBand(value[0], rect.left, rect.right, length);
        float y = rubberBand(value[1], rect.top, rect.bottom, length);
        return new Float[]{x, y};
      }
    };
  }

  private static float rubberBand(float value, float min, float max, float bandLength) {
    if (value >= min && value <= max) {
      // While we're within range we don't rubber band the value.
      return value;
    }

    if (bandLength <= 0) {
      // The rubber band doesn't exist, return the minimum value so that we stay put.
      return min;
    }

    if (value > max) {
      return band(value - max, bandLength) + max;

    } else if (value < min) {
      return min - band(min - value, bandLength);
    }

    return value;
  }

  /**
   * Accepts values from [0...+inf] and ensures that f(x) < bandLength for all values.
   */
  private static float band(float value, float bandLength) {
    float rubberBandCoefficient = 0.55f;

    float demoninator = value * rubberBandCoefficient / bandLength + 1;
    return bandLength * (1 - 1 / demoninator);
  }
}
