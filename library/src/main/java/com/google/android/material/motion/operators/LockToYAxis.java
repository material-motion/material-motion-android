package com.google.android.material.motion.operators;

import android.graphics.PointF;

import com.google.android.material.motion.Operation;
import com.google.android.material.motion.SameTypedMapOperation;

public final class LockToYAxis {
  /**
   * For an incoming translational PointF stream, overwrites the x value with the given xValue.
   */
  public static Operation<PointF, PointF> lockToYAxis(final float xValue) {
    return new SameTypedMapOperation<PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(xValue, value.y);
      }
    };
  }
}
