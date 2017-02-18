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
package com.google.android.reactive.motion.gestures;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.reactive.motion.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OnTouchListeners {

  public static void add(View view, OnTouchListener listener) {
    getMultiListener(view).addOnTouchListener(listener);
  }

  public static void remove(View view, OnTouchListener listener) {
    getMultiListener(view).removeOnTouchListener(listener);
  }

  @NonNull
  private static MultiOnTouchListener getMultiListener(View view) {
    // View.getOnTouchListener() does not exist, so we store the listener in a tag.
    MultiOnTouchListener multiListener = (MultiOnTouchListener) view.getTag(R.id.multi_listener_tag);
    if (multiListener == null) {
      multiListener = new MultiOnTouchListener();
      view.setTag(R.id.multi_listener_tag, multiListener);
    }
    view.setOnTouchListener(multiListener);
    return multiListener;
  }

  private static class MultiOnTouchListener implements OnTouchListener {

    private final List<OnTouchListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      boolean handled = false;

      for (OnTouchListener listener : listeners) {
        handled |= listener.onTouch(v, event);
      }

      return handled;
    }

    public void addOnTouchListener(OnTouchListener listener) {
      if (!listeners.contains(listener)) {
        listeners.add(listener);
      }
    }

    public void removeOnTouchListener(OnTouchListener listener) {
      listeners.remove(listener);
    }
  }
}
