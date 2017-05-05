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
import com.google.android.material.motion.gestures.BuildConfig;
import com.google.android.material.motion.testing.TrackingMotionObserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.google.android.material.motion.operators.IgnoreUntil.ignoreUntil;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IgnoreUntilTests {
  @Test
  public void testIgnoreUntil() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    floatSource()
      .compose(ignoreUntil(50f))
      .subscribe(tracker);

    assertThat(tracker.values).isEqualTo(Arrays.asList(50f, 10f, 20f, 80f));
  }

  @NonNull
  private MotionObservable<Float> floatSource() {
    float input[] = {20, 10, 60, 50, 10, 20, 80};
    return new MotionObservable<>(new IndefiniteObservable.Connector<MotionObserver<Float>>() {
      @NonNull
      @Override
      public IndefiniteObservable.Disconnector connect(MotionObserver<Float> observer) {
        for(Float f : input){
          observer.next(f);
        }
        return IndefiniteObservable.Disconnector.NO_OP;
      }
    });
  }
}
