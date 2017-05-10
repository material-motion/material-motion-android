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

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class StartWithTests {

  @Test
  public void testOverwrittenByReactivePropertyDefaultValue() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();
    ReactiveProperty<Float> property = ReactiveProperty.of(0f);

    property.getStream().compose(StartWith.startWith(100f))
      .subscribe(tracker);

    property.write(-10f);
    assertThat(tracker.values).isEqualTo(Arrays.asList(0f, -10f));
  }

  @Test
  public void testInitializedWithInitialValue() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    MotionObserver<Float> [] valueObserver = new MotionObserver [1];
    MotionObservable observable = new MotionObservable<>(new IndefiniteObservable.Connector<MotionObserver<Float>>() {
      @NonNull
      @Override
      public IndefiniteObservable.Disconnector connect(MotionObserver<Float> observer) {
        valueObserver[0] = observer;
        return IndefiniteObservable.Disconnector.NO_OP;
      }
    });

    observable.compose(StartWith.startWith(10f))
      .subscribe(tracker);

    valueObserver[0].next(50f);
    assertThat(tracker.values).isEqualTo(Arrays.asList(10f, 50f));
  }

  @Test
  public void testAdditionalSubscriptionsReceiveLatestValue() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    MotionObserver<Float> [] valueObserver = new MotionObserver [1];
    MotionObservable observable = new MotionObservable<>(new IndefiniteObservable.Connector<MotionObserver<Float>>() {
      @NonNull
      @Override
      public IndefiniteObservable.Disconnector connect(MotionObserver<Float> observer) {
        valueObserver[0] = observer;
        return IndefiniteObservable.Disconnector.NO_OP;
      }
    });

    MotionObservable<Float> stream = observable.compose(StartWith.startWith(10f));
    stream.subscribe(new MotionObserver.SimpleMotionObserver<Float>() {

      @Override
      public void next(Float value) {

      }
    });
    valueObserver[0].next(50f);

    stream.subscribe(tracker);
    assertThat(tracker.values).isEqualTo(Arrays.asList(50f));
  }
}
