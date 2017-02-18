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

import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureStateChangeListener;
import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.ReactiveProperty;
import com.google.android.material.motion.streams.gestures.OnTouchListeners;
import com.google.android.material.motion.streams.interactions.MaterialSpring;
import com.google.android.material.motion.streams.properties.ViewProperties;
import com.google.android.material.motion.streams.sources.PhysicsSpringSource;
import com.google.android.material.motion.streams.sources.ReboundSpringSource;
import com.google.android.material.motion.streams.springs.FloatArrayTypeVectorizer;

import static com.google.android.material.motion.gestures.GestureRecognizer.BEGAN;
import static com.google.android.material.motion.gestures.GestureRecognizer.CHANGED;

/**
 * Streams for Android sample Activity.
 */
public class SpringsActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  private View container;
  private View reboundDestination;
  private View physicsDestination;
  private View reboundTarget;
  private View physicsTarget;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.springs_activity);

    container = findViewById(android.R.id.content);
    reboundDestination = findViewById(R.id.rebound_destination);
    physicsDestination = findViewById(R.id.physics_destination);
    reboundTarget = findViewById(R.id.rebound_target);
    physicsTarget = findViewById(R.id.physics_target);

    reboundDestination.setBackgroundDrawable(new CheckerboardDrawable());
    physicsDestination.setBackgroundDrawable(new CheckerboardDrawable());

    runDemo();
  }

  private void runDemo() {
    final MaterialSpring<View, Float[]> reboundSpring = new MaterialSpring<>(
      ViewProperties.TRANSLATION,
      new FloatArrayTypeVectorizer(2),
      new Float[]{0f, 0f},
      new Float[]{0f, 0f},
      new Float[]{0f, 0f},
      0.01f,
      1f,
      4f,
      ReboundSpringSource.SPRING_SOURCE);
    final MaterialSpring<View, Float[]> physicsSpring = new MaterialSpring<>(
      ViewProperties.TRANSLATION,
      new FloatArrayTypeVectorizer(2),
      new Float[]{0f, 0f},
      new Float[]{0f, 0f},
      new Float[]{0f, 0f},
      0.01f,
      1f,
      4f,
      PhysicsSpringSource.SPRING_SOURCE);

    OnTouchListeners.add(container, new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = MotionEventCompat.getActionMasked(motionEvent);

        switch (action) {
          case MotionEvent.ACTION_DOWN:
            reboundSpring.destination.write(new Float[]{600f, 0f});
            physicsSpring.destination.write(new Float[]{600f, 0f});
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            reboundSpring.destination.write(new Float[]{0f, 0f});
            physicsSpring.destination.write(new Float[]{0f, 0f});
            break;
        }

        return true;
      }
    });

    final DragGestureRecognizer dragRecognizer = new DragGestureRecognizer();
    dragRecognizer.addStateChangeListener(new GestureStateChangeListener() {

      private float initialDestinationX;
      private float initialDestinationY;

      @Override
      public void onStateChanged(GestureRecognizer gestureRecognizer) {
        switch (gestureRecognizer.getState()) {
          case BEGAN:
            Float[] initialDestination = reboundSpring.destination.read();
            initialDestinationX = initialDestination[0];
            initialDestinationY = initialDestination[1];
            break;
          case CHANGED:
            Float[] newDestination = {
              initialDestinationX + dragRecognizer.getTranslationX(),
              initialDestinationY + dragRecognizer.getTranslationY(),
            };
            reboundSpring.destination.write(newDestination);
            physicsSpring.destination.write(newDestination);
            break;
        }
      }
    });
    OnTouchListeners.add(container, dragRecognizer);

    runtime.addInteraction(reboundSpring, reboundTarget);
    runtime.addInteraction(physicsSpring, physicsTarget);

    runtime.write(
      reboundSpring.destination.getStream(),
      ReactiveProperty.of(reboundDestination, ViewProperties.TRANSLATION));
    runtime.write(
      physicsSpring.destination.getStream(),
      ReactiveProperty.of(physicsDestination, ViewProperties.TRANSLATION));
  }
}
