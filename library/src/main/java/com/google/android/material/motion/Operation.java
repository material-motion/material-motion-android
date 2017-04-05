package com.google.android.material.motion;

import com.google.android.indefinite.observable.Observer;

/**
 * An operation is able to transform incoming values before choosing whether or not to pass them
 * downstream.
 *
 * @param <T> The incoming value type.
 * @param <U> The downstream value type.
 */
public abstract class Operation<T, U> {

  public void preConnect(MotionObserver<U> observer) {
  }

  /**
   * Transforms the incoming value before passing it to the observer, or blocks the value.
   *
   * @param value The incoming value.
   */
  public abstract void next(MotionObserver<U> observer, T value);

  public void postConnect(MotionObserver<U> observer) {
  }

  public void preDisconnect(MotionObserver<U> observer) {
  }

  public void postDisconnect(MotionObserver<U> observer) {
  }
}
