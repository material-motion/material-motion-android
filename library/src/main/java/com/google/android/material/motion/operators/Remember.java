package com.google.android.material.motion.operators;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.android.indefinite.observable.IndefiniteObservable.Connector;
import com.google.android.indefinite.observable.IndefiniteObservable.Disconnector;
import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.RawOperation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Remember {

  @VisibleForTesting
  Remember() {
    throw new UnsupportedOperationException();
  }

  public static <T> RawOperation<T, T> remember() {
    return new RawOperation<T, T>() {
      @Override
      public MotionObservable<T> compose(MotionObservable<T> stream) {
        return new MotionObservable<>(new Connector<MotionObserver<T>>() {

          private final List<Observer<T>> observers = new CopyOnWriteArrayList<>();
          @Nullable
          private Subscription subscription;
          @Nullable
          private T lastValue;

          @NonNull
          @Override
          public Disconnector connect(MotionObserver<T> observer) {
            if (observers.isEmpty()) {
              subscription = stream.subscribe(new MotionObserver<T>() {
                @Override
                public void next(T value) {
                  lastValue = value;
                  for (Observer<T> observer : observers) {
                    observer.next(value);
                  }
                }
              });
            }

            if (!observers.contains(observer)) {
              observers.add(observer);
            }
            if (lastValue != null) {
              observer.next(lastValue);
            }

            return new Disconnector() {
              @Override
              public void disconnect() {
                observers.remove(observer);
                if (observers.isEmpty() && subscription != null) {
                  subscription.unsubscribe();
                  subscription = null;
                }
              }
            };
          }
        });
      }
    };
  }
}
