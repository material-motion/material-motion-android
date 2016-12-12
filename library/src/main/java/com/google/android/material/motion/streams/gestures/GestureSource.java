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
package com.google.android.material.motion.streams.gestures;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureStateChangeListener;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscriber;
import com.google.android.material.motion.observable.IndefiniteObservable.Unsubscriber;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;

/**
 * A source for gestures.
 */
public final class GestureSource {

  @VisibleForTesting
  GestureSource() {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a gesture source that will connect to the provided gesture recognizer.
   */
  public static <T extends GestureRecognizer> MotionObservable<T> of(final T gesture) {
    return new MotionObservable<>(new Subscriber<MotionObserver<T>>() {
      @Nullable
      @Override
      public Unsubscriber subscribe(MotionObserver<T> observer) {
        final GestureConnection connection = new GestureConnection(gesture, observer);
        return new Unsubscriber() {
          @Override
          public void unsubscribe() {
            connection.disconnect();
          }
        };
      }
    });
  }

  private static class GestureConnection<T extends GestureRecognizer> {

    private final T gesture;
    private final MotionObserver<T> observer;

    public GestureConnection(
      T gesture, MotionObserver<T> observer) {
      this.gesture = gesture;
      this.observer = observer;

      gesture.addStateChangeListener(gestureStateChangeListener);

      propagate();
    }

    public void disconnect() {
      gesture.removeStateChangeListener(gestureStateChangeListener);
    }

    private void propagate() {
      int state = gesture.getState();
      boolean isActive = state == GestureRecognizer.BEGAN || state == GestureRecognizer.CHANGED;

      if (isActive) {
        observer.state(MotionObservable.ACTIVE);
      }

      observer.next(gesture);

      if (!isActive) {
        observer.state(MotionObservable.AT_REST);
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
