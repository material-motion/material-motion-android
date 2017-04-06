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
package com.google.android.material.motion.interactions;

import android.graphics.PointF;
import android.view.View;

import com.google.android.material.motion.ConstraintApplicator;
import com.google.android.material.motion.Interaction;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionRuntime;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.sources.TapSource;

public class SetPositionOnTap extends Interaction<ReactiveProperty<PointF>, PointF> {

  public final View container;
  public final MotionObservable<PointF> tapStream;

  public SetPositionOnTap(View container) {
    this.container = container;
    this.tapStream = new TapSource(this).getStream();
  }

  @Override
  public void apply(
    MotionRuntime runtime,
    ReactiveProperty<PointF> target,
    ConstraintApplicator<PointF> constraints) {
    runtime.write(constraints.apply(tapStream), target);
  }
}
