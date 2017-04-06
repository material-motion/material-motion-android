package com.google.android.material.motion.operators;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionObserver.SimpleMotionObserver;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.properties.ViewProperties;

import static com.google.android.material.motion.gestures.GestureRecognizer.BEGAN;
import static com.google.android.material.motion.gestures.GestureRecognizer.CHANGED;

public final class Translated {

  @VisibleForTesting
  Translated() {
    throw new UnsupportedOperationException();
  }

  /**
   * Adds the current translation to the initial translation of the given view and emits the
   * result while the gesture recognizer is active.
   */
  public static <T extends DragGestureRecognizer> Operation<T, PointF> translated(
    final View view) {
    return new Operation<T, PointF>() {

      private Subscription adjustmentSubscription;

      private float initialTranslationX;
      private float initialTranslationY;
      private float adjustmentX;
      private float adjustmentY;

      @Override
      public void preConnect(MotionObserver<PointF> observer) {
        adjustmentSubscription =
          ReactiveProperty.of(view, ViewProperties.ANCHOR_POINT_ADJUSTMENT)
            .subscribe(new SimpleMotionObserver<PointF>() {
              @Override
              public void next(PointF value) {
                adjustmentX += value.x;
                adjustmentY += value.y;
              }
            });
      }

      @Override
      public void next(MotionObserver<PointF> observer, T gestureRecognizer) {
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

            observer.next(new PointF(
              initialTranslationX + adjustmentX + translationX,
              initialTranslationY + adjustmentY + translationY
            ));
            break;
        }
      }

      @Override
      public void preDisconnect(MotionObserver<PointF> observer) {
        adjustmentSubscription.unsubscribe();
      }
    };
  }
}
