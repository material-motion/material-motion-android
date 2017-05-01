package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.android.material.motion.MapOperation;
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
    return new MapOperation<T, T>() {
      @Override
      public T transform(T value) {
        Log.println(priority, tag, prefix + value);
        return value;
      }
    };
  }
}
