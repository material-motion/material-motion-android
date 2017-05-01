package com.google.android.material.motion;

import com.google.android.indefinite.observable.Observer;

/**
 * An observer with possible additional channels.
 */
public abstract class MotionObserver<T> extends Observer<T> {

  @Override
  public abstract void next(T value);

  public abstract void build(MotionBuilder<T> builder, T... values);

  /**
   * A simple observer for when you only want to implement {@link #next(Object)}.
   */
  public static abstract class SimpleMotionObserver<T> extends MotionObserver<T> {
    @Override
    public void build(MotionBuilder<T> builder, T[] values) {
    }
  }
}
