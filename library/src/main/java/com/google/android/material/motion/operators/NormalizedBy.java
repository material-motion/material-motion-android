package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.Operation;
import com.google.android.material.motion.SameTypedMapOperation;

public final class NormalizedBy {

  @VisibleForTesting
  NormalizedBy() {
    throw new UnsupportedOperationException();
  }

  public static Operation<Float, Float> normalizedBy(final float normal) {
    return ScaledBy.scaledBy(1 / normal);
  }

  public static Operation<PointF, PointF> normalizedAllBy(final float normal) {
    return new SameTypedMapOperation<PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x / normal, value.y / normal);
      }
    };
  }

  public static Operation<PointF, PointF> normalizedAllBy(final PointF normal) {
    return new SameTypedMapOperation<PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x / normal.x, value.y / normal.y);
      }
    };
  }
}
