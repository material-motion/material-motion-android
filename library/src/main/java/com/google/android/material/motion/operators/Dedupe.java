package com.google.android.material.motion.operators;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.FilterOperation;
import com.google.android.material.motion.Operation;

public final class Dedupe {

  @VisibleForTesting
  Dedupe() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> dedupe() {
    return new FilterOperation<T>() {

      private boolean emitted;
      @Nullable
      private T lastValue;

      @Override
      public boolean filter(T value) {
        if (emitted && lastValue == value) {
          return false;
        }

        lastValue = value;
        emitted = true;

        return true;
      }
    };
  }
}
