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

import android.app.Activity;
import android.view.View;

import com.google.android.material.motion.gestures.BuildConfig;
import com.google.android.material.motion.gestures.testing.SimulatedGestureRecognizer;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.testing.TrackingMotionObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.google.android.material.motion.gestures.GestureRecognizer.BEGAN;
import static com.google.android.material.motion.gestures.GestureRecognizer.CHANGED;
import static com.google.android.material.motion.gestures.GestureRecognizer.POSSIBLE;
import static com.google.android.material.motion.gestures.GestureRecognizer.RECOGNIZED;
import static com.google.android.material.motion.streams.MotionObservable.ACTIVE;
import static com.google.android.material.motion.streams.MotionObservable.AT_REST;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GestureSourceTests {

  private SimulatedGestureRecognizer gesture;

  @Before
  public void setUp() {
    View view = new View(Robolectric.setupActivity(Activity.class));
    gesture = new SimulatedGestureRecognizer(view);
    view.setOnTouchListener(gesture);
  }

  @Test
  public void createSource() {
    GestureSource
      .from(gesture)
      .subscribe()
      .unsubscribe();
  }

  @Test
  public void propogatesInitialState() {
    TrackingMotionObserver<SimulatedGestureRecognizer> tracker = new TrackingMotionObserver<>();

    GestureSource
      .from(gesture)
      .subscribe(tracker);

    assertThat(tracker.states).isEqualTo(Arrays.asList(POSSIBLE));
  }

  @Test
  public void propogatesValueChanges() {
    TrackingMotionObserver<Integer> tracker = new TrackingMotionObserver<>();

    GestureSource
      .from(gesture)
      .map(new MotionObservable.Transformation<SimulatedGestureRecognizer, Integer>() {
        @Override
        public Integer transform(SimulatedGestureRecognizer value) {
          return value.getState();
        }
      })
      .subscribe(tracker);

    gesture.setState(BEGAN);
    assertThat(tracker.values).isEqualTo(Arrays.asList(POSSIBLE, BEGAN));

    gesture.setState(CHANGED);
    assertThat(tracker.values).isEqualTo(Arrays.asList(POSSIBLE, BEGAN, CHANGED));

    gesture.setState(RECOGNIZED);
    assertThat(tracker.values).isEqualTo(Arrays.asList(POSSIBLE, BEGAN, CHANGED, RECOGNIZED, POSSIBLE));
  }

  @Test
  public void propogatesMotionStateChanges() {
    TrackingMotionObserver<SimulatedGestureRecognizer> tracker = new TrackingMotionObserver<>();

    GestureSource
      .from(gesture)
      .subscribe(tracker);

    assertThat(tracker.states).isEqualTo(Arrays.asList(AT_REST));

    gesture.setState(BEGAN);
    assertThat(tracker.states).isEqualTo(Arrays.asList(AT_REST, ACTIVE));

    gesture.setState(CHANGED);
    assertThat(tracker.states).isEqualTo(Arrays.asList(AT_REST, ACTIVE, ACTIVE));

    gesture.setState(RECOGNIZED);
    assertThat(tracker.states).isEqualTo(Arrays.asList(AT_REST, ACTIVE, ACTIVE, AT_REST, AT_REST));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void constructorIsDisabled() {
    new GestureSource();
  }
}
