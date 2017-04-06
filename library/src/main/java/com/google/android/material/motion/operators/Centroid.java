package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.GestureRecognizer;

public final class Centroid {

  @VisibleForTesting
  Centroid() {
    throw new UnsupportedOperationException();
  }

  /**
   * Extract the centroid from the incoming gesture recognizer stream.
   */
  public static <T extends GestureRecognizer> Operation<T, PointF> centroid() {
    return new MapOperation<T, PointF>() {
      @Override
      public PointF transform(T value) {
        return new PointF(value.getCentroidX(), value.getCentroidY());
      }
    };
  }

  /**
   * Extract the centroidX from the incoming gesture recognizer stream.
   */
  public static <T extends GestureRecognizer> Operation<T, Float> centroidX() {
    return new MapOperation<T, Float>() {
      @Override
      public Float transform(T value) {
        return value.getCentroidX();
      }
    };
  }

  /**
   * Extract the centroidY from the incoming gesture recognizer stream.
   */
  public static <T extends GestureRecognizer> Operation<T, Float> centroidY() {
    return new MapOperation<T, Float>() {
      @Override
      public Float transform(T value) {
        return value.getCentroidY();
      }
    };
  }
}
