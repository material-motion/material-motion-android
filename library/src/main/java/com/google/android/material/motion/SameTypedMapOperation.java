package com.google.android.material.motion;

public abstract class SameTypedMapOperation<T> extends MapOperation<T, T> {

  public final void build(MotionObserver<T> observer, MotionBuilder<T> builder, T[] values) {
    for (int i = 0; i < values.length; i++) {
      values[i] = transform(values[i]);
    }
    observer.build(builder, values);
  }
}
