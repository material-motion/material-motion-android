package com.google.android.material.motion;

public abstract class MotionBuilder<T> {

  public abstract void start(ReactiveProperty<T> property, T[] values);

  public abstract void stop();
}
