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
package com.google.android.material.motion.streams;

import com.google.android.material.motion.streams.MotionObservable.Operation;

import java.util.ArrayList;
import java.util.List;

public abstract class Interaction<T, O> {

  private final List<Operation<T, T>> operations = new ArrayList<>();

  public abstract void apply(MotionRuntime runtime, O target);

  public final Interaction<T, O> constrain(Operation<T, T> operation) {
    operations.add(operation);
    return this;
  }

  protected final MotionObservable<T> flatten(MotionObservable<T> stream) {
    for (int i = 0, count = operations.size(); i < count; i++) {
      stream = stream.compose(operations.get(i));
    }
    return stream;
  }
}
