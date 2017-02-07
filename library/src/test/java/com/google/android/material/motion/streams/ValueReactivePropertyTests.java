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
package com.google.android.material.motion.streams;

import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.streams.testing.TrackingMotionObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ValueReactivePropertyTests {

  private static final float E = 0.0001f;

  private ReactiveProperty<Float> property;

  @Before
  public void setUp() {
    property = ReactiveProperty.of(5f);
  }

  @Test
  public void propertyReadsAndWrites() {
    assertThat(property.read()).isWithin(E).of(5f);

    property.write(7f);
    assertThat(property.read()).isWithin(E).of(7f);
  }

  @Test
  public void observerNotifiedOfInitialValue() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    property.subscribe(tracker);

    assertThat(tracker.values).isEqualTo(Arrays.asList(5f));
  }

  @Test
  public void observerNotifiedOnWrite() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    property.subscribe(tracker);
    property.write(7f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(5f, 7f));
  }

  @Test
  public void sameObserverTwiceIsNoOp() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    property.subscribe(tracker);
    property.subscribe(tracker);
    property.write(7f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(5f, 7f));
  }

  @Test
  public void observerCanUnsubscribe() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    IndefiniteObservable.Subscription subscription = property.subscribe(tracker);
    property.write(7f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(5f, 7f));

    subscription.unsubscribe();
    property.write(10f);

    assertThat(tracker.values).isEqualTo(Arrays.asList(5f, 7f));
  }
}
