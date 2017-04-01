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

import com.google.android.indefinite.observable.IndefiniteObservable;
import com.google.android.indefinite.observable.testing.SimulatedSource;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionObservable.MotionObserver;
import com.google.android.material.motion.MotionObservable.MotionState;


/**
 * A {@link SimulatedSource} that supports the additional {@link #state(int)} channel.
 */
public class SimulatedMotionSource<T> extends SimulatedSource<T, MotionObserver<T>> {

  @Override
  protected IndefiniteObservable<MotionObserver<T>> createObservable(IndefiniteObservable.Connector<MotionObserver<T>> connector) {
    return new MotionObservable<>(connector);
  }

  @Override
  public MotionObservable<T> getObservable() {
    return (MotionObservable<T>) super.getObservable();
  }

  public void state(@MotionState int state) {
    for (MotionObserver<T> observer : observers) {
      observer.state(state);
    }
  }
}
