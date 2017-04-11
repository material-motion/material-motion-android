package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.RawOperation;
import com.google.android.material.motion.gestures.GestureRecognizer;

import static com.google.android.material.motion.operators.Inverted.inverted;
import static com.google.android.material.motion.operators.IsActive.isActive;

public final class IsAtRest {

  @VisibleForTesting
  IsAtRest() {
    throw new UnsupportedOperationException();
  }

  public static <T extends GestureRecognizer> RawOperation<T, Boolean> isAtRest() {
    return new RawOperation<T, Boolean>() {
      @Override
      public MotionObservable<Boolean> compose(MotionObservable<T> stream) {
        return stream
          .compose(isActive())
          .compose(inverted());
      }
    };
  }
}
