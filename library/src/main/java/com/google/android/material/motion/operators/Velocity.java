package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer;

public final class Velocity {

  @VisibleForTesting
  Velocity() {
    throw new UnsupportedOperationException();
  }

  public static <T extends DragGestureRecognizer> Operation<T, PointF> velocity() {
    return new Operation<T, PointF>() {
      @Override
      public void next(MotionObserver<PointF> observer, T value) {
        if (value.getState() == GestureRecognizer.RECOGNIZED) {
          observer.next(new PointF(value.getVelocityX(), value.getVelocityY()));
        }
      }
    };
  }
}
