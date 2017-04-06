package com.google.android.material.motion.operators;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.GestureRecognizer;

public final class Anchored {

  /* Temporary variables. */
  private static final float[] array = new float[2];
  private static final Matrix matrix = new Matrix();
  private static final Matrix inverse = new Matrix();

  @VisibleForTesting
  Anchored() {
    throw new UnsupportedOperationException();
  }

  public static <T extends GestureRecognizer> Operation<T, PointF> anchored(final View view) {
    return new Operation<T, PointF>() {

      @Override
      public void next(Observer<PointF> observer, T gestureRecognizer) {
        array[0] = view.getPivotX();
        array[1] = view.getPivotY();
        GestureRecognizer.getTransformationMatrix(view, matrix, inverse);
        matrix.mapPoints(array);

        PointF adjustment = new PointF(
          gestureRecognizer.getUntransformedCentroidX() - array[0],
          gestureRecognizer.getUntransformedCentroidY() - array[1]
        );
        observer.next(adjustment);
      }
    };
  }
}
