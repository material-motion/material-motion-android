package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.Operation;
import com.google.android.material.motion.SameTypedMapOperation;

public final class ScaledBy {

  @VisibleForTesting
  ScaledBy() {
    throw new UnsupportedOperationException();
  }

  public static Operation<Float, Float> scaledBy(final float scale) {
    return new SameTypedMapOperation<Float>() {
      @Override
      public Float transform(Float value) {
        return value * scale;
      }
    };
  }

  public static Operation<PointF, PointF> scaledAllBy(final float scale) {
    return new SameTypedMapOperation<PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x * scale, value.y * scale);
      }
    };
  }

  public static Operation<PointF, PointF> scaledAllBy(final PointF scale) {
    return new SameTypedMapOperation<PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x * scale.x, value.y * scale.y);
      }
    };
  }
}
