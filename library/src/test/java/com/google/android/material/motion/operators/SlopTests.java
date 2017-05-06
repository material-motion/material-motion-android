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
public class SlopTests {
  private ReactiveProperty<Integer> property;

  @Before
  public void setUp() {
    property = ReactiveProperty.of(0);
  }

  @Test
  public void testInitializedWithinRegionEmitsNothing() {
    TrackingMotionObserver<Integer> tracker = new TrackingMotionObserver<>();
    property.getStream().compose(Slop.slop(0, 0))
      .subscribe(tracker);

    assertThat(tracker.values).isEqualTo(Arrays.asList());
  }

  @Test
  public void testEmptySlopSize() {
    TrackingMotionObserver<Integer> tracker = new TrackingMotionObserver<>();
    property.getStream().compose(Slop.slop(0, 0))
      .subscribe(tracker);

    int input[] = {-10, 0, -10};
    for (int i : input) {
      property.write(i);
    }

    assertThat(tracker.values).isEqualTo(
      Arrays.asList(Slop.SlopEvent.EXIT,
        Slop.SlopEvent.RETURN,
        Slop.SlopEvent.EXIT));
  }

  @Test
  public void testWithPositiveSlopSize() {
    TrackingMotionObserver<Integer> tracker = new TrackingMotionObserver<>();
    property.getStream().compose(Slop.slop(-10, 10))
      .subscribe(tracker);

    int input[] = {-10, -20, -10, 10, 20, 0};
    for (int i : input) {
      property.write(i);
    }

    assertThat(tracker.values).isEqualTo(
      Arrays.asList(Slop.SlopEvent.EXIT,
        Slop.SlopEvent.RETURN,
        Slop.SlopEvent.EXIT,
        Slop.SlopEvent.RETURN));
  }
}
