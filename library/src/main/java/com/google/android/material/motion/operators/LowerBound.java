package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class LowerBound {

  @VisibleForTesting
  LowerBound() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Comparable<T>> Operation<T, T> lowerBound(final T lowerBound) {
    return new MapOperation<T, T>() {
      @Override
      public T transform(T value) {
        if (lowerBound.compareTo(value) > 0) {
          return lowerBound;
        }
        return value;
      }
    };
  }
}
