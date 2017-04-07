package com.google.android.material.motion.operators;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;

import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;

public class DelayBy {

  @VisibleForTesting
  DelayBy() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> delayBy(final long delayMillis) {
    return new Operation<T, T>() {

      private final Handler handler = new Handler(Looper.getMainLooper());
      private boolean connected;

      @Override
      public void preConnect(MotionObserver<T> observer) {
        connected = true;
      }

      @Override
      public void next(final Observer<T> observer, final T value) {
        handler.postDelayed(() -> {
          if (connected) {
            observer.next(value);
          }
        }, delayMillis);
      }

      @Override
      public void preDisconnect(MotionObserver<T> observer) {
        connected = false;
      }
    };
  }
}
