package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.GestureRecognizer;

public final class Pivot {

  @VisibleForTesting
  Pivot() {
    throw new UnsupportedOperationException();
  }

  public static <T extends GestureRecognizer> Operation<T, PointF> pivot() {
    return new Operation<T, PointF>() {

      @Override
      public void next(MotionObserver<PointF> observer, T gestureRecognizer) {
        PointF pivot = new PointF(
          gestureRecognizer.getCentroidX(),
          gestureRecognizer.getCentroidY()
        );
        observer.next(pivot);
      }
    };
  }
}
