package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.Operation;
import com.google.android.material.motion.SameTypedMapOperation;

public final class LowerBound {

  @VisibleForTesting
  LowerBound() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Comparable<T>> Operation<T, T> lowerBound(final T lowerBound) {
    return new SameTypedMapOperation<T>() {
      @Override
      public T transform(T value) {
        if (value.compareTo(lowerBound) < 0) {
          return lowerBound;
        }
        return value;
      }
    };
  }
}
