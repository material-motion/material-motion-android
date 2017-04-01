package com.google.android.material.motion;

import com.google.android.indefinite.observable.Observer;

/**
 * A filter operation evaluates whether to pass a value downstream.
 */
public abstract class FilterOperation<T> extends Operation<T, T> {

  /**
   * Returns whether to pass the value.
   */
  public abstract boolean filter(T value);

  @Override
  public void next(Observer<T> observer, T value) {
    if (filter(value)) {
      observer.next(value);
    }
  }
}
