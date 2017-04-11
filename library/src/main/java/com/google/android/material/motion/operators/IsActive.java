package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer;

import static com.google.android.material.motion.gestures.GestureRecognizer.BEGAN;
import static com.google.android.material.motion.gestures.GestureRecognizer.CHANGED;

public final class IsActive {

  @VisibleForTesting
  IsActive() {
    throw new UnsupportedOperationException();
  }

  public static <T extends GestureRecognizer> Operation<T, Boolean> isActive() {
    return new MapOperation<T, Boolean>() {
      @Override
      public Boolean transform(T value) {
        int state = value.getState();
        boolean active = state == BEGAN || state == CHANGED;
        return active;
      }
    };
  }
}
