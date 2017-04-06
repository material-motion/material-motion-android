package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;

public final class InitialValue {

  @VisibleForTesting
  InitialValue() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> initialValue(final T initialValue) {
    return new Operation<T, T>() {
      @Override
      public void preConnect(MotionObserver<T> observer) {
        observer.next(initialValue);
      }

      @Override
      public void next(MotionObserver<T> observer, T value) {
        observer.next(value);
      }
    };
  }
}
