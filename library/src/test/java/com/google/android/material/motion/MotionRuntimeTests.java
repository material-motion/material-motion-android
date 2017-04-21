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
import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;

import com.google.android.material.motion.testing.SimulatedMotionSource;
import com.google.android.material.motion.testing.TrackingMotionObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
  public void writeAndReadCenterProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).center().write(new PointF(15, 40));
    assertThat(runtime.get(target).center().read()).isEqualTo(new PointF(15, 40));

    runtime.get(target).center().write(new PointF(1, 2));
    assertThat(runtime.get(target).center().read()).isEqualTo(new PointF(1, 2));
  }

  @Test
  public void readWriteSubscribeCenterProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).center().write(new PointF(1,2));

    TrackingMotionObserver<PointF> tracker = new TrackingMotionObserver<>();
    runtime.get(target).center().subscribe(tracker);

    runtime.get(target).center().write(new PointF(15,40));
    runtime.get(target).center().write(new PointF(7,13));

    assertThat(tracker.values).containsAllOf(new PointF(1,2), new PointF(15,40), new PointF(7,13));
  }

  @Test
  public void readWritePositionProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).translation().write(new PointF(10,21));

    assertThat(runtime.get(target).translation().read()).isEqualTo(new PointF(10, 21));
  }

  @Test
  public void readWriteScaleProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).scale().write(new PointF(80,2));

    assertThat(runtime.get(target).scale().read()).isEqualTo(new PointF(80, 2));
  }

  @Test
  public void readWritePivotProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).pivot().write(new PointF(74,52));

    assertThat(runtime.get(target).pivot().read()).isEqualTo(new PointF(74, 52));
  }

  @Test
  public void readWriteBackgroundColorProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).backgroundColor().write(new Integer(Color.RED));

    assertThat(runtime.get(target).backgroundColor().read()).isEqualTo(new Integer(Color.RED));
  }

  @Test
  public void readWriteTranslationXProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).translationX().write(5.0f);

    assertThat(runtime.get(target).translationX().read()).isWithin(0.5f).of(5.0f);
  }

  @Test
  public void readWriteTranslationYProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).translationY().write(9.0f);

    assertThat(runtime.get(target).translationY().read()).isWithin(0.5f).of(9.0f);
  }

  @Test
  public void readWriteTranslationZProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).translationZ().write(7.0f);

    assertThat(runtime.get(target).translationZ().read()).isWithin(0.5f).of(7.0f);
  }

  @Test
  public void readWriteRotationXProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).rotationX().write(17.0f);

    assertThat(runtime.get(target).rotationX().read()).isWithin(0.5f).of(17.0f);
  }

  @Test
  public void readWriteRotationYProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).rotationY().write(33.0f);

    assertThat(runtime.get(target).rotationY().read()).isWithin(0.5f).of(33.0f);
  }

  @Test
  public void readWriteScaleXProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).scaleX().write(11.0f);

    assertThat(runtime.get(target).scaleX().read()).isWithin(0.5f).of(11.0f);
  }

  @Test
  public void readWriteScaleYProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).scaleY().write(55.0f);

    assertThat(runtime.get(target).scaleY().read()).isWithin(0.5f).of(55.0f);
  }

  @Test
  public void readWriteXProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).x().write(62.0f);

    assertThat(runtime.get(target).x().read()).isWithin(0.5f).of(62.0f);
  }

  @Test
  public void readWriteYProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).y().write(23.0f);

    assertThat(runtime.get(target).y().read()).isWithin(0.5f).of(23.0f);
  }

  @Test
  public void readWriteZProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).z().write(54.0f);

    assertThat(runtime.get(target).z().read()).isWithin(0.5f).of(54.0f);
  }

  @Test
  public void readAlphaProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    runtime.get(target).alpha().write(0.5f);

    assertThat(runtime.get(target).alpha().read()).isWithin(0.5f).of(0.5f);
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
}
