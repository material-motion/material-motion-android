package com.google.android.material.motion;

public abstract class RawOperation<T, U> {

  public abstract MotionObservable<U> compose(MotionObservable<? extends T> stream);
}
