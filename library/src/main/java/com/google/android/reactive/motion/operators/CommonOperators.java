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
package com.google.android.reactive.motion.operators;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.observable.Observer;
import com.google.android.reactive.motion.MotionObservable.Operation;

public class CommonOperators {

  @VisibleForTesting
  public CommonOperators() {
    throw new UnsupportedOperationException();
  }

  public static <T> Operation<T, T> dedupe() {
    return new Operation<T, T>() {

      private boolean dispatched;
      @Nullable
      private T lastValue;

      @Override
      public void next(Observer<T> observer, T value) {
        if (dispatched && lastValue == value) {
          return;
        }

        lastValue = value;
        dispatched = true;

        observer.next(value);
      }
    };
  }
}
