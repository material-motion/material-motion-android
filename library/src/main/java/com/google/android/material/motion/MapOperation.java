package com.google.android.material.motion;

/**
 * A map operation transforms incoming values before they are passed downstream.
 *
 * @param <T> The incoming value type.
 * @param <U> The downstream value type.
 */
public abstract class MapOperation<T, U> extends Operation<T, U> {

  /**
   * Transforms the given value to another value.
   */
  public abstract U transform(T value);

  @Override
  public final void next(MotionObserver<U> observer, T value) {
    observer.next(transform(value));
  }
}
