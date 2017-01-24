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
package com.google.android.material.motion.streams.sources;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureRecognizerState;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureStateChangeListener;
import com.google.android.material.motion.observable.IndefiniteObservable.Connector;
import com.google.android.material.motion.observable.IndefiniteObservable.Disconnector;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.MotionState;

/**
 * A source for gestures.
 */
public final class GestureSource {

  private static final GestureSource GESTURE_SOURCE = new GestureSource();

  /**
   * Creates a gesture source that will connect to the provided gesture recognizer.
   */
  public static <T extends GestureRecognizer> MotionObservable<T> from(T gesture) {
    return GESTURE_SOURCE.create(gesture);
  }

  /**
   * Creates a gesture source that will connect to the provided gesture recognizer.
   */
  public <T extends GestureRecognizer> MotionObservable<T> create(final T gesture) {
    return new MotionObservable<>(new Connector<MotionObserver<T>>() {
      @NonNull
      @Override
      public Disconnector connect(MotionObserver<T> observer) {
        final GestureConnection connection = new GestureConnection<>(gesture, observer);
        return new Disconnector() {
          @Override
          public void disconnect() {
            connection.disconnect();
          }
        };
      }
    });
  }

  private static class GestureConnection<T extends GestureRecognizer> {

    private final T gesture;
    private final MotionObserver<T> observer;
    @Nullable
    @MotionState
    private Integer lastPropagatedState = null;

    private GestureConnection(
      T gesture, MotionObserver<T> observer) {
      this.gesture = gesture;
      this.observer = observer;

      gesture.addStateChangeListener(gestureStateChangeListener);

      propagate();
    }

    private void disconnect() {
      gesture.removeStateChangeListener(gestureStateChangeListener);
    }

    private void propagate() {
      @GestureRecognizerState int state = gesture.getState();
      boolean isActive = state == GestureRecognizer.BEGAN || state == GestureRecognizer.CHANGED;
      boolean wasActive =
        lastPropagatedState != null && lastPropagatedState == MotionObservable.ACTIVE;
      boolean wasAtRest =
        lastPropagatedState != null && lastPropagatedState == MotionObservable.AT_REST;

      if (isActive && !wasActive) {
        observer.state(MotionObservable.ACTIVE);
        lastPropagatedState = MotionObservable.ACTIVE;
      }

      observer.next(gesture);

      if (!isActive && !wasAtRest) {
        observer.state(MotionObservable.AT_REST);
        lastPropagatedState = MotionObservable.AT_REST;
      }
    }

    private final GestureStateChangeListener gestureStateChangeListener =
      new GestureStateChangeListener() {
        @Override
        public void onStateChanged(GestureRecognizer gesture) {
          propagate();
        }
      };
  }
}
