package com.google.android.material.motion;

public class ConstraintApplicator<T> {

  private final Operation<T, T>[] constraints;

  public ConstraintApplicator(Operation<T, T>[] constraints) {
    this.constraints = constraints;
  }

  public MotionObservable<T> apply(MotionObservable<T> stream) {
    for (Operation<T, T> constraint : constraints) {
      stream = stream.compose(constraint);
    }
    return stream;
  }
}
