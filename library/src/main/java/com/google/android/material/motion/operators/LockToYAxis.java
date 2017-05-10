package com.google.android.material.motion.operators;

import android.graphics.PointF;

import com.google.android.material.motion.Operation;
import com.google.android.material.motion.SameTypedMapOperation;

public final class LockToYAxis {
  /**
   * Lock the point's y value to the given value.
   */
  public static Operation<PointF, PointF> lockToYAxis(final float yValue) {
    return new SameTypedMapOperation<PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x, yValue);
      }
    };
  }
}
