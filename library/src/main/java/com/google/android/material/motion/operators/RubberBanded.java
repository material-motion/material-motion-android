package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class RubberBanded {

  @VisibleForTesting
  RubberBanded() {
    throw new UnsupportedOperationException();
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
}
