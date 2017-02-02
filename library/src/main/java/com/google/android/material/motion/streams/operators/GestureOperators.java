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
package com.google.android.material.motion.streams.operators;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureRecognizerState;
import com.google.android.material.motion.gestures.RotateGestureRecognizer;
import com.google.android.material.motion.gestures.ScaleGestureRecognizer;
import com.google.android.material.motion.observable.IndefiniteObservable.Connector;
import com.google.android.material.motion.observable.IndefiniteObservable.Disconnector;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.observable.Observer;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.FilterOperation;
import com.google.android.material.motion.streams.MotionObservable.MapOperation;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.MotionState;
import com.google.android.material.motion.streams.MotionObservable.Operation;
import com.google.android.material.motion.streams.MotionObservable.RawOperation;
import com.google.android.material.motion.streams.MotionObservable.SimpleMotionObserver;
import com.google.android.material.motion.streams.ReactiveProperty;

/**
 * Extended operators for gestures.
 *
 * @see MotionObservable#compose(Operation)
 */
public final class GestureOperators {

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

  /**
   * Adds the current translation to the initial translation of the given view and emits the
   * result while the gesture recognizer is active.
   */
  public static <T extends DragGestureRecognizer> RawOperation<T, Float[]> translated(
    final ReactiveProperty<Float[]> initialTranslation) {
    return new RawOperation<T, Float[]>() {

      @Override
      public MotionObservable<Float[]> connect(final MotionObservable<T> upstream) {
        return new MotionObservable<>(
          new Connector<MotionObserver<Float[]>>() {

            private float lastTranslationX;
            private float lastTranslationY;

            @NonNull
            @Override
            public Disconnector connect(final MotionObserver<Float[]> downstream) {
              final Subscription propertySubscription = initialTranslation.subscribe(
                new SimpleMotionObserver<Float[]>() {
                  @Override
                  public void next(Float[] value) {
                    lastTranslationX = value[0];
                    lastTranslationY = value[1];

                    // TODO: Call observer.next()?
                  }
                });

              final Subscription upstreamSubscription = upstream.subscribe(
                new MotionObserver<T>() {

                  private float initialTranslationX;
                  private float initialTranslationY;

                  @Override
                  public void next(T gestureRecognizer) {
                    switch (gestureRecognizer.getState()) {
                      case GestureRecognizer.BEGAN:
                        initialTranslationX = lastTranslationX;
                        initialTranslationY = lastTranslationY;
                        break;
                      case GestureRecognizer.CHANGED:
                        float translationX = gestureRecognizer.getTranslationX();
                        float translationY = gestureRecognizer.getTranslationY();

                        downstream.next(
                          new Float[]{
                            initialTranslationX + translationX,
                            initialTranslationY + translationY});
                        break;
                    }
                  }

                  @Override
                  public void state(@MotionState int state) {
                    downstream.state(state);
                  }
                });

              return new Disconnector() {
                @Override
                public void disconnect() {
                  propertySubscription.unsubscribe();
                  upstreamSubscription.unsubscribe();
                }
              };
            }
          });
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
      public void next(Observer<Float> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case GestureRecognizer.BEGAN:
            initialRotation = view.getRotation();
            break;
          case GestureRecognizer.CHANGED:
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
      public void next(Observer<Float[]> observer, T gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case GestureRecognizer.BEGAN:
            initialScaleX = view.getScaleX();
            initialScaleY = view.getScaleY();
            break;
          case GestureRecognizer.CHANGED:
            float scale = gestureRecognizer.getScale();

            observer.next(new Float[]{initialScaleX * scale, initialScaleY * scale});
            break;
        }
      }
    };
  }
}
