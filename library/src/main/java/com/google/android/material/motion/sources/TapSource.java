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
package com.google.android.material.motion.sources;

import android.graphics.PointF;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionState;
import com.google.android.material.motion.Source;
import com.google.android.material.motion.gestures.OnTouchListeners;
import com.google.android.material.motion.interactions.SetPositionOnTap;

public class TapSource extends Source<PointF> {

  private final SetPositionOnTap interaction;

  private final View container;
  private final GestureDetectorCompat detector;
  private final SimpleArrayMap<Observer<PointF>, OnGestureListener> gestureListeners =
    new SimpleArrayMap<>();

  public TapSource(SetPositionOnTap interaction) {
    super(interaction);
    this.interaction = interaction;
    container = interaction.container;
    detector = new GestureDetectorCompat(
      container.getContext(),
      new SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
          boolean consumed = false;
          for (int i = 0, count = gestureListeners.size(); i < count; i++) {
            consumed |= gestureListeners.valueAt(i).onSingleTapUp(e);
          }
          return consumed;
        }
      });
    detector.setOnDoubleTapListener(null);
    detector.setIsLongpressEnabled(false);
  }

  @Override
  protected void onConnect(final MotionObserver<PointF> observer) {
    gestureListeners.put(observer, new SimpleOnGestureListener() {
      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        interaction.state.write(MotionState.ACTIVE);

        observer.next(new PointF(e.getX(), e.getY()));

        interaction.state.write(MotionState.AT_REST);
        return true;
      }
    });
  }

  @Override
  protected void onEnable() {
    OnTouchListeners.add(container, listener);
  }

  @Override
  protected void onDisable() {
    OnTouchListeners.remove(container, listener);
  }

  @Override
  protected void onDisconnect(MotionObserver<PointF> observer) {
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
