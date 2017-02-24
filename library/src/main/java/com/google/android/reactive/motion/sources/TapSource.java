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

import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.indefinite.observable.Observer;
import com.google.android.reactive.motion.MotionObservable;
import com.google.android.reactive.motion.MotionObservable.MotionObserver;
import com.google.android.reactive.motion.Source;
import com.google.android.reactive.motion.gestures.OnTouchListeners;
import com.google.android.reactive.motion.interactions.Tap;

public class TapSource extends Source<Float[]> {

  private final View container;
  private final GestureDetectorCompat detector;
  private final SimpleArrayMap<Observer<Float[]>, OnGestureListener> gestureListeners = new SimpleArrayMap<>();

  public TapSource(Tap tap) {
    super(tap);
    container = tap.container;
    detector = new GestureDetectorCompat(
      container.getContext(),
      new SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
          boolean consumed = false;
          for (int i = 0, count = gestureListeners.size(); i < count; i++) {
            consumed |= gestureListeners.get(i).onSingleTapUp(e);
          }
          return consumed;
        }
      });
    detector.setOnDoubleTapListener(null);
    detector.setIsLongpressEnabled(false);
  }

  @Override
  protected void onConnect(final MotionObserver<Float[]> observer) {
    gestureListeners.put(observer, new SimpleOnGestureListener() {
      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        observer.next(new Float[]{e.getX(), e.getY()});
        return true;
      }
    });
  }

  @Override
  protected void onEnable(MotionObserver<Float[]> observer) {
    OnTouchListeners.add(container, listener);
    // TODO: observer.state()?
  }

  @Override
  protected void onDisable(MotionObserver<Float[]> observer) {
    OnTouchListeners.remove(container, listener);
    // TODO: observer.state()?
  }

  @Override
  protected void onDisconnect(MotionObserver<Float[]> observer) {
    gestureListeners.remove(observer);
  }

  private final View.OnTouchListener listener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      detector.onTouchEvent(event);
      return true;
    }
  };
}
