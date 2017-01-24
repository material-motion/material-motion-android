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
package com.google.android.material.motion.streams.interactions;

import android.support.v4.util.SimpleArrayMap;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.streams.Interaction;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.R;
import com.google.android.material.motion.streams.sources.GestureSource;

/**
 * Abstract base class for all gesture interactions.
 */
public abstract class GestureInteraction<T extends GestureRecognizer> extends Interaction<View> {

  private final T gestureRecognizer;
  private final MotionObservable<T> stream;

  protected GestureInteraction(T gestureRecognizer) {
    this.gestureRecognizer = gestureRecognizer;
    this.stream = GestureSource.from(gestureRecognizer);
  }

  @Override
  public final void apply(View target) {
    // View.getOnTouchListener() does not exist, so we store the listener in a tag.
    GestureListener gestureListener = (GestureListener) target.getTag(R.id.gesture_listener_tag);
    if (gestureListener == null) {
      gestureListener = new GestureListener();
      target.setTag(R.id.gesture_listener_tag, gestureListener);
    }
    target.setOnTouchListener(gestureListener);

    gestureListener.gestureRecognizers.put(gestureRecognizer.getClass(), gestureRecognizer);

    apply(stream, target);
  }

  /**
   * Applies the values of the gesture recognizer stream to the target view.
   */
  protected abstract void apply(MotionObservable<T> stream, View target);

  private static class GestureListener implements OnTouchListener {
    private final SimpleArrayMap<Class<? extends GestureRecognizer>, GestureRecognizer> gestureRecognizers =
      new SimpleArrayMap<>();

    @Override
    public boolean onTouch(View view, MotionEvent event) {
      boolean handled = false;

      for (int i = 0, count = gestureRecognizers.size(); i < count; i++) {
        handled |= gestureRecognizers.valueAt(i).onTouch(view, event);
      }

      return handled;
    }
  }
}
