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

import android.view.View;

import com.google.android.material.motion.ConstraintApplicator;
import com.google.android.material.motion.MotionRuntime;
import com.google.android.material.motion.Interaction;

public class DirectlyManipulable extends Interaction<View, Void> {

  public final Draggable draggable = new Draggable();
  public final Pinchable pinchable = new Pinchable();
  public final Rotatable rotatable = new Rotatable();
  public final AdjustsAnchorPoint adjustsAnchorPoint = new AdjustsAnchorPoint(pinchable, rotatable);

  @Override
  public void apply(MotionRuntime runtime, View target, ConstraintApplicator<Void> constraints) {
    runtime.addInteraction(draggable, target);
    runtime.addInteraction(pinchable, target);
    runtime.addInteraction(rotatable, target);
    runtime.addInteraction(adjustsAnchorPoint, target);
  }
}
