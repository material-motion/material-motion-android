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
public class UpperBoundTests {

  private ReactiveProperty<Float> property;

  @Before
  public void setUp() {
    property = ReactiveProperty.of(0.50f);
  }

  @Test
  public void calculatesUpperBoundCorrectly() {
    MotionObservable<Float> stream =
      property
        .getStream()
        .compose(CommonOperators.upperBound(1f));

    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();
    stream.subscribe(tracker);

    property.write(0.75f);
    property.write(1.00f);
    property.write(1.25f);
    property.write(1.50f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(
      0.50f,
      0.75f,
      1.00f,
      1.00f,
      1.00f));
  }
}
