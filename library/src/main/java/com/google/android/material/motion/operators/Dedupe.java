package com.google.android.material.motion.operators;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.Operation;

public final class Dedupe {

  @VisibleForTesting
  Dedupe() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> dedupe() {
    return new Operation<T, T>() {

      private boolean dispatched;
      @Nullable
      private T lastValue;

      @Override
      public void next(Observer<T> observer, T value) {
        if (dispatched && lastValue == value) {
          return;
        }

        lastValue = value;
        dispatched = true;

        observer.next(value);
      }
    };
  }
}
