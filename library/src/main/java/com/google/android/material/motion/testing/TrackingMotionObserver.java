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
package com.google.android.material.motion.testing;

import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionState;

import java.util.ArrayList;
import java.util.List;

/**
 * An observer useful in tests. Will track the incoming values and states and save them to be
 * asserted on.
 */
public class TrackingMotionObserver<T> extends MotionObserver<T> {

  public final List<T> values = new ArrayList<>();
  public final List<Integer> states = new ArrayList<>();

  @Override
  public void next(T value) {
    values.add(value);
  }

  @Override
  public void state(@MotionState int state) {
    states.add(state);
  }
}
