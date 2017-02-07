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

import android.app.Activity;
import android.view.View;

import com.google.android.material.motion.streams.testing.TrackingMotionObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PropertyReactivePropertyTests {

  private static final float E = 0.0001f;

  private View target;
  private ReactiveProperty<Float> property;

  @Before
  public void setUp() {
    target = new View(Robolectric.setupActivity(Activity.class));
    property = ReactiveProperty.of(target, View.ALPHA);
  }

  @Test
  public void readsFromTargetProperty() {
    target.setAlpha(1f);
    assertThat(property.read()).isWithin(E).of(1f);

    target.setAlpha(.5f);
    assertThat(property.read()).isWithin(E).of(.5f);
  }

  @Test
  public void writesToTargetProperty() {
    property.write(.5f);
    assertThat(target.getAlpha()).isWithin(E).of(.5f);

    property.write(0f);
    assertThat(target.getAlpha()).isWithin(E).of(0f);
  }

  @Test
  public void observerIsNotified() {
    target.setAlpha(1f);
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    property.subscribe(tracker);

    property.write(.5f);
    assertThat(tracker.values).isEqualTo(Arrays.asList(1f, .5f));
  }
}
