package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class X {

  @VisibleForTesting
  X() {
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
}
