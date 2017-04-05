package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class FloatOperators {

  @VisibleForTesting
  FloatOperators() {
    throw new UnsupportedOperationException();
  }

  public static Operation<Float, Float> normalizedBy(final float normal) {
    return new MapOperation<Float, Float>() {
      @Override
      public Float transform(Float value) {
        return value / normal;
      }
    };
  }
}
