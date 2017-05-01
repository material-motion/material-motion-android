package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.Operation;
import com.google.android.material.motion.SameTypedMapOperation;

public final class UpperBound {

  @VisibleForTesting
  UpperBound() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Comparable<T>> Operation<T, T> upperBound(final T upperBound) {
    return new SameTypedMapOperation<T>() {
      @Override
      public T transform(T value) {
        if (value.compareTo(upperBound) > 0) {
          return upperBound;
        }
        return value;
      }
    };
  }
}
