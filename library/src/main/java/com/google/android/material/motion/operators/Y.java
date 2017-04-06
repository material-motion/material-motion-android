package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class Y {

  @VisibleForTesting
  Y() {
    throw new UnsupportedOperationException();
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
