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

import com.google.android.material.motion.streams.MotionObservable.ScopedReadable;
import com.google.android.material.motion.streams.MotionObservable.ScopedWritable;

/**
 * A scoped reactive property.
 */
public final class ScopedReactiveProperty<T> extends ReactiveProperty<T> {

  private final ScopedReadable<T> readable;
  private final ScopedWritable<T> writable;

  public ScopedReactiveProperty(ScopedReadable<T> readable, ScopedWritable<T> writable) {
    this.readable = readable;
    this.writable = writable;
  }

  @Override
  public T read() {
    return readable.read();
  }

  @Override
  public void write(T value) {
    writable.write(value);

    onWrite(value);
  }
}
