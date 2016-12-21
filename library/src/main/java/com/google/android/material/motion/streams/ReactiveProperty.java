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

import android.support.annotation.NonNull;

import com.google.android.material.motion.observable.IndefiniteObservable.Disconnector;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.ScopedReadable;
import com.google.android.material.motion.streams.MotionObservable.ScopedWritable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A reactive property represents a subscribable, readable/writable value. Subscribers will receive
 * updates whenever {@link #write(Object)} is invoked.
 */
public abstract class ReactiveProperty<T> implements ScopedReadable<T>, ScopedWritable<T> {

  private final List<MotionObserver<T>> observers = new CopyOnWriteArrayList<>();

  public final Subscription subscribe(@NonNull final MotionObserver<T> observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);

      observer.next(read());
    }

    return new Subscription(new Disconnector() {
      @Override
      public void disconnect() {
        observers.remove(observer);
      }
    });
  }

  /**
   * Subclasses should call this after every {@link #write(Object)}.
   */
  protected final void onWrite(T value) {
    for (MotionObserver<T> observer : observers) {
      observer.next(value);
    }
  }

  /**
   * A simple reactive property that holds a single value.
   */
  public static class ValueReactiveProperty<T> extends ReactiveProperty<T> {

    private T value;

    public ValueReactiveProperty(T initialValue) {
      this.value = initialValue;
    }

    @Override
    public T read() {
      return value;
    }

    @Override
    public void write(T value) {
      this.value = value;

      onWrite(value);
    }
  }
}
