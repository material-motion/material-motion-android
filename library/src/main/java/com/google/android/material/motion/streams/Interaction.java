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

import android.util.Property;

import com.google.android.material.motion.streams.MotionObservable.Operation;
import com.google.android.material.motion.streams.MotionObservable.SimpleMotionObserver;
import com.google.android.material.motion.streams.operators.CommonOperators;

import java.util.ArrayList;
import java.util.List;

public abstract class Interaction<O, T> {

  public final ReactiveProperty<Boolean> enabled = ReactiveProperty.of(true);

  private final List<Operation<T, T>> operations = new ArrayList<>();
  private final List<PendingWrite<?>> writes = new ArrayList<>();

  public Interaction() {
    enabled
      .compose(CommonOperators.<Boolean>dedupe())
      .subscribe(new SimpleMotionObserver<Boolean>() {
        @Override
        public void next(Boolean value) {
          for (int i = 0, count = writes.size(); i < count; i++) {
            PendingWrite pendingWrite = writes.get(i);
            if (value) {
              pendingWrite.runtime.write(pendingWrite.observable, pendingWrite.property);
            } else {
              pendingWrite.runtime.unwrite(pendingWrite.observable, pendingWrite.property);
            }
          }
        }
      });
  }

  public abstract void apply(MotionRuntime runtime, O target);

  public final Interaction<O, T> constrain(Operation<T, T> operation) {
    operations.add(operation);
    return this;
  }

  protected final MotionObservable<T> flatten(MotionObservable<T> stream) {
    for (int i = 0, count = operations.size(); i < count; i++) {
      stream = stream.compose(operations.get(i));
    }
    return stream;
  }

  protected final <T> void write(MotionRuntime runtime, MotionObservable<T> stream, O target, Property<O, T> property) {
    write(runtime, stream, ReactiveProperty.of(target, property));
  }

  protected final <T> void write(MotionRuntime runtime, MotionObservable<T> stream, ReactiveWritable<T> property) {
    writes.add(new PendingWrite<>(runtime, stream, property));

    if (enabled.read()) {
      runtime.write(stream, property);
    }
  }
}
