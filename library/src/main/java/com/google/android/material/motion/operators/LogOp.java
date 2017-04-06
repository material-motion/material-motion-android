package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.Operation;

public final class LogOp {

  @VisibleForTesting
  LogOp() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> log(final String tag) {
    return log(tag, "");
  }

  public static <T> Operation<T, T> log(final String tag, final String prefix) {
    return log(Log.DEBUG, tag, prefix);
  }

  public static <T> Operation<T, T> log(final int priority, final String tag, final String prefix) {
    return new Operation<T, T>() {
      @Override
      public void next(Observer<T> observer, T value) {
        Log.println(priority, tag, prefix + value);
        observer.next(value);
      }
    };
  }
}
