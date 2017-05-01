package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.GestureRecognizer;

public final class Pivot {

  @VisibleForTesting
  Pivot() {
    throw new UnsupportedOperationException();
  }

  public static <T extends GestureRecognizer> Operation<T, PointF> pivot() {
    return new MapOperation<T, PointF>() {
      @Override
      public PointF transform(T gestureRecognizer) {
        PointF pivot = new PointF(
          gestureRecognizer.getCentroidX(),
          gestureRecognizer.getCentroidY()
        );
        return pivot;
      }
    };
  }
}
