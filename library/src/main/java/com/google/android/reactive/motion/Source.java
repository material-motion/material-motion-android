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
package com.google.android.reactive.motion;

import android.support.annotation.NonNull;

import com.google.android.indefinite.observable.IndefiniteObservable.Connector;
import com.google.android.indefinite.observable.IndefiniteObservable.Disconnector;
import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.reactive.motion.MotionObservable.MotionObserver;
import com.google.android.reactive.motion.MotionObservable.SimpleMotionObserver;
import com.google.android.reactive.motion.operators.CommonOperators;

public abstract class Source<T> {

  private final Interaction<?, T> interaction;

  public Source(Interaction<?, T> interaction) {
    this.interaction = interaction;
  }

  public final MotionObservable<T> getStream() {
    return new MotionObservable<>(new Connector<MotionObserver<T>>() {

      @NonNull
      @Override
      public Disconnector connect(final MotionObserver<T> observer) {
        onConnect(observer);

        final Subscription enabledSubscription = interaction.enabled.getStream()
          .compose(CommonOperators.<Boolean>dedupe())
          .subscribe(new SimpleMotionObserver<Boolean>() {
            @Override
            public void next(Boolean enabled) {
              if (enabled) {
                onEnable(observer);
              } else {
                onDisable(observer);
              }
            }
          });

        return new Disconnector() {

          @Override
          public void disconnect() {
            enabledSubscription.unsubscribe();
            onDisable(observer);
          }
        };
      }
    });
  }

  protected abstract void onConnect(MotionObserver<T> observer);

  protected abstract void onEnable(MotionObserver<T> observer);

  protected abstract void onDisable(MotionObserver<T> observer);

  protected void onDisconnect(MotionObserver<T> observer) {
  }
}
