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

import android.support.annotation.NonNull;

import com.google.android.material.motion.observable.IndefiniteObservable;

/**
 * A property that can be read into a MotionObservable stream.
 */
public interface ReactiveReadable<T> {

  /**
   * Reads the property's value.
   */
  T read();

  /**
   * Subscribes to the property's value.
   * <p>
   * The given observer will be notified of the property's current value and every time the
   * property is written to.
   */
  IndefiniteObservable.Subscription subscribe(@NonNull final MotionObservable.MotionObserver<T> observer);
}
