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
import android.view.View;

import com.google.android.material.motion.testing.SimulatedMotionSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.android.material.motion.MotionState.ACTIVE;
import static com.google.android.material.motion.MotionState.AT_REST;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MotionRuntimeTests {

  private static final float E = 0.0001f;

  private MotionRuntime runtime;

  @Before
  public void setUp() {
    runtime = new MotionRuntime();
  }

  @Test
  public void defaultStateAtRest() {
    assertThat(runtime.getState()).isEqualTo(AT_REST);
  }

  @Test
  public void writesCorrectValueToReactiveProperty() {
    SimulatedMotionSource<Float> source = new SimulatedMotionSource<>();
    MotionObservable<Float> stream = source.getObservable();

    ReactiveProperty<Float> property = ReactiveProperty.of(0f);
    runtime.write(stream, property);

    source.next(5f);
    assertThat(property.read()).isWithin(0f).of(5f);
  }

  @Test
  public void writesCorrectValueToUnscopedProperty() {
    SimulatedMotionSource<Float> source = new SimulatedMotionSource<>();
    MotionObservable<Float> stream = source.getObservable();

    View target = new View(Robolectric.setupActivity(Activity.class));

    runtime.write(stream, target, View.TRANSLATION_X);
    source.next(5f);
    assertThat(target.getTranslationX()).isWithin(E).of(5f);
  }

  @Test
  public void activeIfObserverActive() {
    SimulatedMotionSource<Float> source = new SimulatedMotionSource<>();
    MotionObservable<Float> stream = source.getObservable();

    runtime.write(stream, ReactiveProperty.of(0f));

    assertThat(runtime.getState()).isEqualTo(AT_REST);
    source.state(MotionState.ACTIVE);
    assertThat(runtime.getState()).isEqualTo(ACTIVE);
  }

  @Test
  public void atRestIfObserverAtRest() {
    SimulatedMotionSource<Float> source = new SimulatedMotionSource<>();
    MotionObservable<Float> stream = source.getObservable();

    runtime.write(stream, null, null);

    assertThat(runtime.getState()).isEqualTo(AT_REST);
    source.state(MotionState.ACTIVE);
    assertThat(runtime.getState()).isEqualTo(ACTIVE);
    source.state(MotionState.AT_REST);
    assertThat(runtime.getState()).isEqualTo(AT_REST);
  }

  @Test
  public void observerCanPassActiveTwice() {
    SimulatedMotionSource<Float> source = new SimulatedMotionSource<>();
    MotionObservable<Float> stream = source.getObservable();

    runtime.write(stream, null, null);

    assertThat(runtime.getState()).isEqualTo(AT_REST);
    source.state(MotionState.ACTIVE);
    source.state(MotionState.ACTIVE);
    assertThat(runtime.getState()).isEqualTo(ACTIVE);
    source.state(MotionState.AT_REST);
    assertThat(runtime.getState()).isEqualTo(AT_REST);
  }

  @Test
  public void observerCanPassAtRestTwice() {
    SimulatedMotionSource<Float> source = new SimulatedMotionSource<>();
    MotionObservable<Float> stream = source.getObservable();

    runtime.write(stream, null, null);

    assertThat(runtime.getState()).isEqualTo(AT_REST);
    source.state(MotionState.ACTIVE);
    assertThat(runtime.getState()).isEqualTo(ACTIVE);
    source.state(MotionState.AT_REST);
    source.state(MotionState.AT_REST);
    assertThat(runtime.getState()).isEqualTo(AT_REST);
  }

  @Test
  public void activeIfAtLeastOneObserverIsActive() {
    SimulatedMotionSource<Float> source1 = new SimulatedMotionSource<>();
    SimulatedMotionSource<Float> source2 = new SimulatedMotionSource<>();

    runtime.write(source1.getObservable(), null, null);
    runtime.write(source2.getObservable(), null, null);

    assertThat(runtime.getState()).isEqualTo(AT_REST);

    source1.state(MotionState.ACTIVE);
    assertThat(runtime.getState()).isEqualTo(ACTIVE);
    source2.state(MotionState.ACTIVE);
    assertThat(runtime.getState()).isEqualTo(ACTIVE);

    source1.state(MotionState.AT_REST);
    assertThat(runtime.getState()).isEqualTo(ACTIVE);
    source2.state(MotionState.AT_REST);
    assertThat(runtime.getState()).isEqualTo(AT_REST);
  }
}
