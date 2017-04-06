package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class UpperBound {

  @VisibleForTesting
  UpperBound() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Comparable<T>> Operation<T, T> upperBound(final T upperBound) {
    return new MapOperation<T, T>() {
      @Override
      public T transform(T value) {
        if (upperBound.compareTo(value) > 0) {
          return upperBound;
        }
        return value;
      }
    };
  }
}
