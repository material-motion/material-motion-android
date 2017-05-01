package com.google.android.material.motion;

/**
 * A filter operation evaluates whether to pass a value downstream.
 */
public abstract class FilterOperation<T> extends Operation<T, T> {

  /**
   * Returns whether to pass the value.
   */
  public abstract boolean filter(T value);

  @Override
  public final void next(MotionObserver<T> observer, T value) {
    if (filter(value)) {
      observer.next(value);
    }
  }

  @Override
  public final void build(
    MotionObserver<T> observer, MotionBuilder<T> builder, T[] values) {
    // No-op. Cannot use builder pattern with filter.
  }
}
