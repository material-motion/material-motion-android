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
package com.google.android.material.motion.operators;

import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.gestures.BuildConfig;
import com.google.android.material.motion.testing.TrackingMotionObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class LowerBoundTests {

  private ReactiveProperty<Float> property;

  @Before
  public void setUp() {
    property = ReactiveProperty.of(0.50f);
  }

  @Test
  public void calculatesLowerBoundCorrectly() {
    MotionObservable<Float> stream =
      property
        .getStream()
        .compose(CommonOperators.lowerBound(0f));

    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();
    stream.subscribe(tracker);

    property.write(0.25f);
    property.write(0.00f);
    property.write(-0.25f);
    property.write(-0.50f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(
      0.50f,
      0.25f,
      0.00f,
      0.00f,
      0.00f));
  }
}
