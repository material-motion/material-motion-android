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

import android.support.annotation.NonNull;

import com.google.android.indefinite.observable.IndefiniteObservable;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.gestures.BuildConfig;
import com.google.android.material.motion.testing.TrackingMotionObserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.google.android.material.motion.operators.Dedupe.dedupe;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DedupeTests {

  @Test
  public void testReceivesInitialValue() {
    TrackingMotionObserver<Integer> tracker = new TrackingMotionObserver<>();
    ReactiveProperty<Integer> property = ReactiveProperty.of(0);
    property.getStream()
      .compose(dedupe())
      .subscribe(tracker);

    assertThat(tracker.values).isEqualTo(Arrays.asList(0));
  }

  @Test
  public void testReceivesNoDuplicates() {
    TrackingMotionObserver<Integer> tracker = new TrackingMotionObserver<>();

    int[] input = {10, 10, 5, 2, 10};
    intSource(input)
      .compose(dedupe())
      .subscribe(tracker);

    assertThat(tracker.values).isEqualTo(Arrays.asList(10, 5, 2, 10));
  }

  @NonNull
  private MotionObservable<Integer> intSource(int[] input) {

    return new MotionObservable<>(new IndefiniteObservable.Connector<MotionObserver<Integer>>() {
      @NonNull
      @Override
      public IndefiniteObservable.Disconnector connect(MotionObserver<Integer> observer) {
        for (int f : input) {
          observer.next(f);
        }
        return IndefiniteObservable.Disconnector.NO_OP;
      }
    });
  }
}
