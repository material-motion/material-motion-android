package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.FilterOperation;
import com.google.android.material.motion.Operation;

public final class IgnoreUntil {

  @VisibleForTesting
  IgnoreUntil() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> ignoreUntil(final T expected) {
    return new FilterOperation<T>() {

      private boolean received;

      @Override
      public boolean filter(T value) {
        if (expected.equals(value)) {
          received = true;
        }

        return received;
      }
    };
  }
}
