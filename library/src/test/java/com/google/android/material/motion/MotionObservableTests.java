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
package com.google.android.material.motion;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.indefinite.observable.IndefiniteObservable;
import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.MotionObservable.MotionObserver;
import com.google.android.material.motion.testing.TrackingMotionObserver;
import com.google.android.material.motion.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.google.android.material.motion.MotionObservable.ACTIVE;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MotionObservableTests {

  private static final float E = 0.0001f;

  private MotionRuntime runtime;
  private MotionObservable<Float> observable;

  @Before
  public void setUp() {
    runtime = new MotionRuntime();
    observable = new MotionObservable<>(
      new IndefiniteObservable.Connector<MotionObserver<Float>>() {
        @NonNull
        @Override
        public IndefiniteObservable.Disconnector connect(MotionObserver<Float> observer) {
          observer.next(5f);
          observer.state(ACTIVE);
          return IndefiniteObservable.Disconnector.NO_OP;
        }
      });
  }

  @Test
  public void mapByHalf() {
    observable.compose(new MotionObservable.MapOperation<Float, Float>() {
      @Override
      public Float transform(Float value) {
        return value / 2f; // Half.
      }
    }).subscribe(new MotionObserver<Float>() {
      @Override
      public void next(Float value) {
        assertThat(value).isWithin(E).of(2.5f);
      }

      @Override
      public void state(@MotionObservable.MotionState int state) {
        assertThat(state).isEqualTo(ACTIVE);
      }
    });
  }

  @Test
  public void filterAll() {
    observable.compose(new MotionObservable.FilterOperation<Float>() {
      @Override
      public boolean filter(Float value) {
        return false; // All are filtered.
      }
    }).subscribe(new MotionObserver<Float>() {
      @Override
      public void next(Float value) {
        throw new AssertionError("Should never arrive here.");
      }

      @Override
      public void state(@MotionObservable.MotionState int state) {
        assertThat(state).isEqualTo(ACTIVE);
      }
    }).unsubscribe();
  }

  @Test
  public void filterNone() {
    observable.compose(new MotionObservable.FilterOperation<Float>() {
      @Override
      public boolean filter(Float value) {
        return true; // None are filtered.
      }
    }).subscribe(new MotionObserver<Float>() {
      @Override
      public void next(Float value) {
        assertThat(value).isWithin(E).of(5f);
      }

      @Override
      public void state(@MotionObservable.MotionState int state) {
        assertThat(state).isEqualTo(ACTIVE);
      }
    }).unsubscribe();
  }

  @Test
  public void writeUnscopedProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    target.setTranslationX(0);

    runtime.write(observable, target, View.TRANSLATION_X);

    assertThat(target.getTranslationX()).isWithin(E).of(5f);
  }

  @Test
  public void writeReactiveProperty() {
    ReactiveProperty<Float> property = ReactiveProperty.of(100f);

    property.write(5f);

    assertThat(property.read()).isWithin(0f).of(5f);
  }

  @Test
  public void readReactiveProperty() {
    ReactiveProperty<Float> reader = ReactiveProperty.of(6f);

    assertThat(reader.read()).isWithin(E).of(6f);
  }

  @Test
  public void subscribeReactiveProperty() {
    ReactiveProperty<Float> property = ReactiveProperty.of(6f);

    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();
    Subscription subscription = property.subscribe(tracker);

    property.write(7f);
    property.write(8f);
    subscription.unsubscribe();

    assertThat(tracker.values).isEqualTo(Arrays.asList(6f, 7f, 8f));
  }
}
