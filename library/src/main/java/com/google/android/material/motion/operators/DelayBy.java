package com.google.android.material.motion.operators;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;

import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;

import java.util.ArrayDeque;
import java.util.Deque;

public class DelayBy {

  @VisibleForTesting
  DelayBy() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> delayBy(final long delayMillis) {
    return new Operation<T, T>() {

      private final Handler handler = new Handler(Looper.getMainLooper());
      private SimpleArrayMap<MotionObserver<T>, Deque<Runnable>> runnables = new SimpleArrayMap<>();

      @Override
      public void preConnect(MotionObserver<T> observer) {
        if (!runnables.containsKey(observer)) {
          runnables.put(observer, new ArrayDeque<>());
        }
      }

      @Override
      public void next(final MotionObserver<T> observer, final T value) {
        final Runnable runnable = new Runnable() {
          @Override
          public void run() {
            runnables.get(observer).remove(this);
            observer.next(value);
          }
        };

        runnables.get(observer).add(runnable);
        handler.postDelayed(runnable, delayMillis);
      }

      @Override
      public void preDisconnect(MotionObserver<T> observer) {
        while (!runnables.get(observer).isEmpty()) {
          handler.removeCallbacks(runnables.get(observer).poll());
        }
      }
    };
  }
}
