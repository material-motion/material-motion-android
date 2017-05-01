package com.google.android.material.motion.operators;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.GestureRecognizer;

public final class Anchored {

  /* Allocate local variables once and reuse across invocations. */
  private static final float[] array = new float[2];
  private static final Matrix matrix = new Matrix();
  private static final Matrix inverse = new Matrix();

  @VisibleForTesting
  Anchored() {
    throw new UnsupportedOperationException();
  }

  public static <T extends GestureRecognizer> Operation<T, PointF> anchored(final View view) {
    return new MapOperation<T, PointF>() {
      @Override
      public PointF transform(T gestureRecognizer) {
        array[0] = view.getPivotX();
        array[1] = view.getPivotY();
        GestureRecognizer.getTransformationMatrix(view, matrix, inverse);
        matrix.mapPoints(array);

        PointF adjustment = new PointF(
          gestureRecognizer.getUntransformedCentroidX() - array[0],
          gestureRecognizer.getUntransformedCentroidY() - array[1]
        );
        return adjustment;
      }
    };
  }
}
