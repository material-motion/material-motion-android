package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class OffsetBy {

  @VisibleForTesting
  OffsetBy() {
    throw new UnsupportedOperationException();
  }

  public static Operation<Float, Float> offsetBy(final float offset) {
    return new MapOperation<Float, Float>() {
      @Override
      public Float transform(Float value) {
        return value + offset;
      }
    };
  }

  public static Operation<PointF, PointF> offsetAllBy(final float offset) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x + offset, value.y + offset);
      }
    };
  }

  public static Operation<PointF, PointF> offsetAllBy(final PointF offset) {
    return new MapOperation<PointF, PointF>() {
      @Override
      public PointF transform(PointF value) {
        return new PointF(value.x + offset.x, value.y + offset.y);
      }
    };
  }
}
