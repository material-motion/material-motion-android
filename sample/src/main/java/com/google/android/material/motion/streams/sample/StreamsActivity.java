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
package com.google.android.material.motion.streams.sample;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.springs.FloatVectorizer;
import com.google.android.material.motion.streams.springs.MaterialSpring;

/**
 * Streams for Android sample Activity.
 */
public class StreamsActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  private View tweenTarget;
  private View springTarget;

  private MotionObserver<String> callback;
  private int index = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.streams_activity);

    tweenTarget = findViewById(R.id.tween_target);
    springTarget = findViewById(R.id.spring_target);

    runDemo();
  }

  private void runDemo() {
    final MaterialSpring<View, Float> spring = new MaterialSpring<>(
      View.TRANSLATION_X,
      new FloatVectorizer(),
      0f,
      0f,
      0f,
      0.01f,
      1,
      10);

    springTarget.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = MotionEventCompat.getActionMasked(motionEvent);

        switch (action) {
          case MotionEvent.ACTION_DOWN:
            spring.destination.write(650f);
            ObjectAnimator.ofFloat(tweenTarget, View.ALPHA, 0f).start();
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            spring.destination.write(0f);
            ObjectAnimator.ofFloat(tweenTarget, View.ALPHA, 1f).start();
            break;
        }

        return true;
      }
    });

    runtime.addInteraction(spring, springTarget);
  }
}
