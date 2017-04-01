package com.google.android.material.motion;

import com.google.android.indefinite.observable.Observer;

/**
 * An observer with an additional {@link #state(int)} method.
 */
public abstract class MotionObserver<T> extends Observer<T> {

  @Override
  public abstract void next(T value);

  /**
   * A method to handle new incoming state values.
   */
  public abstract void state(@MotionState int state);

  /**
   * A simple observer for when you only want to implement {@link #next(Object)}.
   */
  public static abstract class SimpleMotionObserver<T> extends MotionObserver<T> {

    public void state(@MotionState int state) {
      // No-op.
    }
  }
}
