package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class FloatOperators {

  @VisibleForTesting
  FloatOperators() {
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

  public static Operation<Float, Float> scaledBy(final float scale) {
    return new MapOperation<Float, Float>() {
      @Override
      public Float transform(Float value) {
        return value * scale;
      }
    };
  }

  public static Operation<Float, Float> normalizedBy(final float normal) {
    return scaledBy(1 / normal);
  }

  public static Operation<Float, Float> rewriteRange(
    final float start, final float end, final float destinationStart, final float destinationEnd) {
    return new MapOperation<Float, Float>() {
      @Override
      public Float transform(Float value) {
        float position = value - start;

        float vector = end - start;
        if (vector == 0) {
          return destinationStart;
        }
        float progress = position / vector;

        float destinationVector = destinationEnd - destinationStart;
        float destinationPosition = destinationVector * progress;

        return destinationStart + destinationPosition;
      }
    };
  }
}
