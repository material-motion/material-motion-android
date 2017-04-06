package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.ScaleGestureRecognizer;

import static com.google.android.material.motion.gestures.GestureRecognizer.BEGAN;
import static com.google.android.material.motion.gestures.GestureRecognizer.CHANGED;

public final class Scaled {

  @VisibleForTesting
  Scaled() {
    throw new UnsupportedOperationException();
  }

  /**
   * Multiplies the current scale onto the initial scale of the given view and emits the result
   * while the gesture recognizer is active.
   */
  public static <T extends ScaleGestureRecognizer> Operation<T, PointF> scaled(final View view) {
    return new Operation<T, PointF>() {

      private float initialScaleX;
      private float initialScaleY;

      @Override
      public void next(Observer<PointF> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case BEGAN:
            initialScaleX = view.getScaleX();
            initialScaleY = view.getScaleY();
            break;
          case CHANGED:
            float scale = gestureRecognizer.getScale();

            observer.next(new PointF(initialScaleX * scale, initialScaleY * scale));
            break;
        }
      }
    };
  }
}
