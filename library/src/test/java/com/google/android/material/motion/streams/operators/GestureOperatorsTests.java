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
package com.google.android.material.motion.streams.operators;

import android.app.Activity;
import android.graphics.PointF;
import android.view.View;

import com.google.android.material.motion.gestures.BuildConfig;
import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.testing.SimulatedGestureRecognizer;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.sources.GestureSource;
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
import static com.google.android.material.motion.gestures.GestureRecognizer.RECOGNIZED;
import static com.google.android.material.motion.streams.MotionObservable.ACTIVE;
import static com.google.android.material.motion.streams.MotionObservable.AT_REST;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GestureOperatorsTests {

  private SimulatedGestureRecognizer gesture;

  @Before
  public void setUp() {
    View view = new View(Robolectric.setupActivity(Activity.class));
    gesture = new SimulatedGestureRecognizer(view);
    view.setOnTouchListener(gesture);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void constructorIsDisabled() {
    new GestureOperators();
  }

  @Test
  public void extractsCentroid() {
    TrackingMotionObserver<PointF> tracker = new TrackingMotionObserver<>();

    GestureSource
      .from(gesture)
      .compose(GestureOperators.centroid())
      .subscribe(tracker);

    gesture.setCentroid(5f, 5f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(new PointF(0f, 0f), new PointF(5f, 5f)));
    assertThat(tracker.states).isEqualTo(Arrays.asList(AT_REST, ACTIVE));
  }

  @Test
  public void extractsCentroidX() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    GestureSource
      .from(gesture)
      .compose(GestureOperators.centroidX())
      .subscribe(tracker);

    gesture.setCentroid(5f, 10f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(0f, 5f));
  }

  @Test
  public void extractsCentroidY() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    GestureSource
      .from(gesture)
      .compose(GestureOperators.centroidY())
      .subscribe(tracker);

    gesture.setCentroid(5f, 10f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(0f, 10f));
  }

  @Test
  public void forwardsOnState() {
    TrackingMotionObserver<Integer> tracker = new TrackingMotionObserver<>();

    GestureSource
      .from(gesture)
      .compose(GestureOperators.onRecognitionState(BEGAN))
      .compose(new MotionObservable.MapOperation<GestureRecognizer, Integer>() {
        @Override
        public Integer transform(GestureRecognizer value) {
          return value.getState();
        }
      })
      .subscribe(tracker);

    gesture.setState(BEGAN);
    gesture.setState(CHANGED);
    gesture.setState(RECOGNIZED);

    assertThat(tracker.values).isEqualTo(Arrays.asList(BEGAN));
  }

  @Test
  public void forwardsOnStates() {
    TrackingMotionObserver<Integer> tracker = new TrackingMotionObserver<>();

    GestureSource
      .from(gesture)
      .compose(GestureOperators.onRecognitionState(BEGAN, RECOGNIZED))
      .compose(new MotionObservable.MapOperation<GestureRecognizer, Integer>() {
        @Override
        public Integer transform(GestureRecognizer value) {
          return value.getState();
        }
      })
      .subscribe(tracker);

    gesture.setState(BEGAN);
    gesture.setState(CHANGED);
    gesture.setState(RECOGNIZED);

    assertThat(tracker.values).isEqualTo(Arrays.asList(BEGAN, RECOGNIZED));
  }
}
