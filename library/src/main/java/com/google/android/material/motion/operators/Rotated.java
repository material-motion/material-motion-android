package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.RotateGestureRecognizer;

import static com.google.android.material.motion.gestures.GestureRecognizer.BEGAN;
import static com.google.android.material.motion.gestures.GestureRecognizer.CHANGED;

public final class Rotated {

  @VisibleForTesting
  Rotated() {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds the current rotation to the initial rotation of the given view and emits the result
   * while the gesture recognizer is active.
   */
  public static <T extends RotateGestureRecognizer> Operation<T, Float> rotated(final View view) {
    return new Operation<T, Float>() {

      private float initialRotation;

      @Override
      public void next(MotionObserver<Float> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case BEGAN:
            initialRotation = view.getRotation();
            break;
          case CHANGED:
            float rotation = gestureRecognizer.getRotation();

            observer.next((float) (initialRotation + rotation * (180 / Math.PI)));
            break;
        }
      }
    };
  }
}
