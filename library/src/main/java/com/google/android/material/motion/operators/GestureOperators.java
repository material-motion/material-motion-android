/*
 * Copyright 2016-present The Material Motion Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.motion.operators;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.FilterOperation;
import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionObserver.SimpleMotionObserver;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.RawOperation;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureRecognizerState;
import com.google.android.material.motion.gestures.RotateGestureRecognizer;
import com.google.android.material.motion.gestures.ScaleGestureRecognizer;
import com.google.android.material.motion.properties.ViewProperties;

import static com.google.android.material.motion.gestures.GestureRecognizer.BEGAN;
import static com.google.android.material.motion.gestures.GestureRecognizer.CHANGED;
import static com.google.android.material.motion.operators.BooleanOperators.inverted;

/**
 * Extended operators for gestures.
 *
 * @see MotionObservable#compose(Operation)
 */
public final class GestureOperators {

  /* Temporary variables. */
  private static final float[] array = new float[2];
  private static final Matrix matrix = new Matrix();
  private static final Matrix inverse = new Matrix();

  @VisibleForTesting
  GestureOperators() {
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

  public static <T extends DragGestureRecognizer> Operation<T, Float[]> velocity() {
    return new Operation<T, Float[]>() {
      @Override
      public void next(MotionObserver<Float[]> observer, T value) {
        if (value.getState() == GestureRecognizer.RECOGNIZED) {
          observer.next(new Float[]{value.getVelocityX(), value.getVelocityY()});
        }
      }
    };
  }

  /**
   * Only forwards the gesture recognizer if its state matches the provided value.
   */
  public static <T extends GestureRecognizer> Operation<T, T> onRecognitionState(
    @GestureRecognizerState final int state) {
    return new FilterOperation<T>() {
      @Override
      public boolean filter(T value) {
        return value.getState() == state;
      }
    };
  }

  /**
   * Only forwards the gesture recognizer if its state matches any of the provided values.
   */
  public static <T extends GestureRecognizer> Operation<T, T> onRecognitionState(
    @GestureRecognizerState final int... states) {
    return new FilterOperation<T>() {
      @Override
      public boolean filter(T value) {
        int s = value.getState();
        for (@GestureRecognizerState int state : states) {
          if (state == s) {
            return true;
          }
        }
        return false;
      }
    };
  }

  public static <T extends DragGestureRecognizer> MapOperation<T, Boolean> isActive() {
    return new MapOperation<T, Boolean>() {
      @Override
      public Boolean transform(T value) {
        int state = value.getState();
        boolean active = state == BEGAN || state == CHANGED;
        return active;
      }
    };
  }

  public static <T extends DragGestureRecognizer> RawOperation<T, Boolean> isAtRest() {
    return new RawOperation<T, Boolean>() {
      @Override
      public MotionObservable<Boolean> compose(MotionObservable<? extends T> stream) {
        return stream
          .compose(GestureOperators.<T>isActive())
          .compose(inverted());
      }
    };
  }

  /**
   * Adds the current translation to the initial translation of the given view and emits the
   * result while the gesture recognizer is active.
   */
  public static <T extends DragGestureRecognizer> Operation<T, Float[]> translated(
    final View view) {
    return new Operation<T, Float[]>() {

      private Subscription adjustmentSubscription;

      private float initialTranslationX;
      private float initialTranslationY;
      private float adjustmentX;
      private float adjustmentY;

      @Override
      public void preConnect(MotionObserver<Float[]> observer) {
        adjustmentSubscription =
          ReactiveProperty.of(view, ViewProperties.ANCHOR_POINT_ADJUSTMENT)
            .subscribe(new SimpleMotionObserver<Float[]>() {
              @Override
              public void next(Float[] value) {
                adjustmentX += value[0];
                adjustmentY += value[1];
              }
            });
      }

      @Override
      public void next(MotionObserver<Float[]> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case BEGAN:
            initialTranslationX = view.getTranslationX();
            initialTranslationY = view.getTranslationY();
            adjustmentX = 0f;
            adjustmentY = 0f;
            break;
          case CHANGED:
            float translationX = gestureRecognizer.getTranslationX();
            float translationY = gestureRecognizer.getTranslationY();

            observer.next(new Float[]{
              initialTranslationX + adjustmentX + translationX,
              initialTranslationY + adjustmentY + translationY,
            });
            break;
        }
      }

      @Override
      public void preDisconnect(MotionObserver<Float[]> observer) {
        adjustmentSubscription.unsubscribe();
      }
    };
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

  /**
   * Multiplies the current scale onto the initial scale of the given view and emits the result
   * while the gesture recognizer is active.
   */
  public static <T extends ScaleGestureRecognizer> Operation<T, Float[]> scaled(final View view) {
    return new Operation<T, Float[]>() {

      private float initialScaleX;
      private float initialScaleY;

      @Override
      public void next(MotionObserver<Float[]> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case BEGAN:
            initialScaleX = view.getScaleX();
            initialScaleY = view.getScaleY();
            break;
          case CHANGED:
            float scale = gestureRecognizer.getScale();

            observer.next(new Float[]{initialScaleX * scale, initialScaleY * scale});
            break;
        }
      }
    };
  }

  public static <T extends GestureRecognizer> Operation<T, Float[]> pivot() {
    return new Operation<T, Float[]>() {

      @Override
      public void next(MotionObserver<Float[]> observer, T gestureRecognizer) {
        Float[] pivot = new Float[]{
          gestureRecognizer.getCentroidX(),
          gestureRecognizer.getCentroidY()
        };
        observer.next(pivot);
      }
    };
  }

  public static <T extends GestureRecognizer> Operation<T, Float[]> anchored(final View view) {
    return new Operation<T, Float[]>() {

      @Override
      public void next(MotionObserver<Float[]> observer, T gestureRecognizer) {
        array[0] = view.getPivotX();
        array[1] = view.getPivotY();
        GestureRecognizer.getTransformationMatrix(view, matrix, inverse);
        matrix.mapPoints(array);

        Float[] adjustment = new Float[]{
          gestureRecognizer.getUntransformedCentroidX() - array[0],
          gestureRecognizer.getUntransformedCentroidY() - array[1],
        };
        observer.next(adjustment);
      }
    };
  }
}
