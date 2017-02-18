/*
 * Copyright 2017-present The Material Motion Authors. All Rights Reserved.
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
package com.google.android.reactive.motion.sources;

import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.motion.observable.IndefiniteObservable.Connector;
import com.google.android.material.motion.observable.IndefiniteObservable.Disconnector;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;
import com.google.android.reactive.motion.MotionObservable;
import com.google.android.reactive.motion.MotionObservable.MotionObserver;
import com.google.android.reactive.motion.MotionObservable.SimpleMotionObserver;
import com.google.android.reactive.motion.gestures.OnTouchListeners;
import com.google.android.reactive.motion.interactions.Tap;
import com.google.android.reactive.motion.operators.CommonOperators;

public class TapSource {

  public static MotionObservable<Float[]> from(final Tap tap) {
    return new MotionObservable<>(new Connector<MotionObserver<Float[]>>() {

      private View container;
      private GestureDetectorCompat detector;

      @NonNull
      @Override
      public Disconnector connect(final MotionObserver<Float[]> observer) {
        container = tap.container;
        detector = new GestureDetectorCompat(
          container.getContext(),
          new SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
              observer.next(new Float[]{e.getX(), e.getY()});
              return true;
            }
          });
        detector.setOnDoubleTapListener(null);
        detector.setIsLongpressEnabled(false);

        final Subscription enabledSubscription =
          tap.enabled.getStream()
            .compose(CommonOperators.<Boolean>dedupe())
            .subscribe(new SimpleMotionObserver<Boolean>() {
              @Override
              public void next(Boolean value) {
                if (value) {
                  start();
                } else {
                  stop();
                }
              }
            });

        return new Disconnector() {
          @Override
          public void disconnect() {
            enabledSubscription.unsubscribe();
            stop();
          }
        };
      }

      private void start() {
        OnTouchListeners.add(container, listener);
      }

      private void stop() {
        OnTouchListeners.remove(container, listener);
      }

      private final View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          detector.onTouchEvent(event);
          return true;
        }
      };
    });
  }
}
