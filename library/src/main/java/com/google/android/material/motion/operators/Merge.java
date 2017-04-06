package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;

public final class Merge {

  @VisibleForTesting
  Merge() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> merge(final MotionObservable<T> stream) {
    return new Operation<T, T>() {

      private Subscription subscription;

      @Override
      public void next(MotionObserver<T> observer, T value) {
        observer.next(value);
      }

      @Override
      public void postConnect(MotionObserver<T> observer) {
        subscription = stream.subscribe(observer);
      }

      @Override
      public void preDisconnect(MotionObserver<T> observer) {
        subscription.unsubscribe();
      }
    };
  }
}
