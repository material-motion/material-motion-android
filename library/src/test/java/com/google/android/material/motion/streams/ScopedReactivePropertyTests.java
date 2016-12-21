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

import com.google.android.material.motion.streams.testing.TrackingMotionObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ScopedReactivePropertyTests {

  @Mock
  private MotionObservable.ScopedReadable<Float> readable;
  @Mock
  private MotionObservable.ScopedWritable<Float> writable;
  private ScopedReactiveProperty<Float> property;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    when(readable.read()).thenReturn(5f);

    property = new ScopedReactiveProperty<>(readable, writable);
  }

  @Test
  public void readsFromReadable() {
    property.read();
    verify(readable).read();
  }

  @Test
  public void writesToWritable() {
    property.write(5f);
    verify(writable).write(5f);
  }

  @Test
  public void observerIsNotified() {
    TrackingMotionObserver<Float> tracker = new TrackingMotionObserver<>();

    property.subscribe(tracker);

    property.write(7f);
    assertThat(tracker.values).isEqualTo(Arrays.asList(5f, 7f));
  }
}
