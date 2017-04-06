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
package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.Operation;

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

  /**
   * For an incoming translational PointF stream, overwrites the x value with the given xValue.
   */
  public static Operation<PointF, PointF> lockToYAxis(final float xValue) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(xValue, value.y);
      }
    };
  }

  /**
   * For an incoming translational PointF stream, overwrites the y value with the given yValue.
   */
  public static Operation<PointF, PointF> lockToXAxis(final float yValue) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x, yValue);
      }
    };
  }

  /**
   * Applies resistance to values that fall outside of the given rect. Resistance increases until
   * the distance reaches length, where resistance becomes infinite.
   */
  public static Operation<PointF, PointF> rubberBanded(final RectF rect, final float length) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        float x = rubberBand(value.x, rect.left, rect.right, length);
        float y = rubberBand(value.y, rect.top, rect.bottom, length);
        return new PointF(x, y);
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

    float denominator = value * rubberBandCoefficient / bandLength + 1;
    return bandLength * (1 - 1 / denominator);
  }

  public static Operation<PointF, PointF> offsetBy(final float offset) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x + offset, value.y + offset);
      }
    };
  }

  public static Operation<PointF, PointF> offsetBy(final PointF offset) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x + offset.x, value.y + offset.y);
      }
    };
  }

  public static Operation<PointF, PointF> scaledBy(final float scale) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x * scale, value.y * scale);
      }
    };
  }

  public static Operation<PointF, PointF> scaledBy(final PointF scale) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x * scale.x, value.y * scale.y);
      }
    };
  }

  public static Operation<PointF, PointF> normalizedBy(final float normal) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x / normal, value.y / normal);
      }
    };
  }

  public static Operation<PointF, PointF> normalizedBy(final PointF normal) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x / normal.x, value.y / normal.y);
      }
    };
  }
}
