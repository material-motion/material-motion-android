/*
 * Copyright 2017-present The Material Motion Authors. All Rights Reserved.
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
package com.google.android.material.motion.streams.interactions;

import android.view.View;

import com.google.android.material.motion.streams.Interaction;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.ReactiveProperty;
import com.google.android.material.motion.streams.sources.TapSource;

public class Tap extends Interaction<ReactiveProperty<Float[]>, Float[]> {

  public final View container;

  private final MotionObservable<Float[]> tapStream;

  public Tap(View container) {
    this.container = container;
    this.tapStream = TapSource.from(this);
  }

  @Override
  public void apply(MotionRuntime runtime, ReactiveProperty<Float[]> target) {
    runtime.write(flatten(tapStream), target);
  }
}
