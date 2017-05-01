package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.Operation;
import com.google.android.material.motion.SameTypedMapOperation;

public final class LockToXAxis {

  @VisibleForTesting
  LockToXAxis() {
    throw new UnsupportedOperationException();
  }

  /**
   * For an incoming translational PointF stream, overwrites the y value with the given yValue.
   */
  public static Operation<PointF, PointF> lockToXAxis(final float yValue) {
    return new SameTypedMapOperation<PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x, yValue);
      }
    };
  }
}
