package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.RawOperation;
import com.google.android.material.motion.gestures.DragGestureRecognizer;

import static com.google.android.material.motion.operators.Inverted.inverted;

public final class IsAtRest {

  @VisibleForTesting
  IsAtRest() {
    throw new UnsupportedOperationException();
  }

  public static <T extends DragGestureRecognizer> RawOperation<T, Boolean> isAtRest() {
    return new RawOperation<T, Boolean>() {
      @Override
      public MotionObservable<Boolean> compose(MotionObservable<? extends T> stream) {
        return stream
          .compose(IsActive.<T>isActive())
          .compose(inverted());
      }
    };
  }
}
