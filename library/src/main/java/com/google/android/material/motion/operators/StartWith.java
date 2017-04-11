package com.google.android.material.motion.operators;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.android.indefinite.observable.IndefiniteObservable.Connector;
import com.google.android.indefinite.observable.IndefiniteObservable.Disconnector;
import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.RawOperation;

import static com.google.android.material.motion.operators.Remember.remember;

public final class StartWith {

  @VisibleForTesting
  StartWith() {
    throw new UnsupportedOperationException();
  }

  public static <T> RawOperation<T, T> startWith(final T initialValue) {
    return new RawOperation<T, T>() {
      @Override
      public MotionObservable<T> compose(MotionObservable<T> stream) {
        return new MotionObservable<>(new Connector<MotionObserver<T>>() {
          @NonNull
          @Override
          public Disconnector connect(MotionObserver<T> observer) {
            observer.next(initialValue);
            Subscription subscription = stream.subscribe(new MotionObserver<T>() {
              @Override
              public void next(T value) {
                observer.next(value);
              }
            });
            return new Disconnector() {
              @Override
              public void disconnect() {
                subscription.unsubscribe();
              }
            };
          }
        }).compose(remember());
      }
    };
  }
}
