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

import com.google.android.indefinite.observable.IndefiniteObservable.Disconnector;
import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.RawOperation;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.gestures.BuildConfig;
import com.google.android.material.motion.testing.TrackingMotionObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RememberTests {

  private ReactiveProperty<Float> property;
  private MotionObservable<Float> stream;
  private TrackingMotionObserver<Float> observer;

  @Before
  public void setUp() {
    property = ReactiveProperty.of(5f);
    stream = property.getStream().compose(Remember.remember());
    observer = new TrackingMotionObserver<>();
  }

  @Test
  public void passesThroughValues() {
    assertThat(observer.values).isEqualTo(Collections.emptyList());

    stream.subscribe(observer);
    assertThat(observer.values).isEqualTo(Collections.singletonList(5f));

    property.write(10f);
    property.write(15f);
    assertThat(observer.values).isEqualTo(Arrays.asList(5f, 10f, 15f));
  }

  @Test
  public void onlySubscribesOnce() {
    RawOperation<Float, Float> remember = Remember.remember();
    //noinspection unchecked
    MotionObservable<Float> upstream = mock(MotionObservable.class);

    MotionObservable<Float> downstream = remember.compose(upstream);

    verify(upstream, times(0)).subscribe(any());

    // First downstream subscription also subscribes upstream.
    downstream.subscribe(new TrackingMotionObserver<>());
    verify(upstream, times(1)).subscribe(any());

    // Second downstream subscription does not.
    downstream.subscribe(new TrackingMotionObserver<>());
    verify(upstream, times(1)).subscribe(any());
  }

  @Test
  public void onlyUnsubscribesOnce() {
    RawOperation<Float, Float> remember = Remember.remember();
    //noinspection unchecked
    MotionObservable<Float> upstream = mock(MotionObservable.class);
    Disconnector upstreamDisconnector = mock(Disconnector.class);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        return new Subscription(upstreamDisconnector);
      }
    }).when(upstream).subscribe(any());

    MotionObservable<Float> downstream = remember.compose(upstream);

    Subscription downstreamSubscription1 = downstream.subscribe(new TrackingMotionObserver<>());
    Subscription downstreamSubscription2 = downstream.subscribe(new TrackingMotionObserver<>());

    verify(upstreamDisconnector, times(0)).disconnect();

    // First downstream unsubscription does nothing.
    downstreamSubscription1.unsubscribe();
    verify(upstreamDisconnector, times(0)).disconnect();

    // Second downstream unsubscription also unsubscribes upstream.
    downstreamSubscription2.unsubscribe();
    verify(upstreamDisconnector, times(1)).disconnect();
  }
}
