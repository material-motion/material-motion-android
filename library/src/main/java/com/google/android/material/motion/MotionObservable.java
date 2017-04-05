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
package com.google.android.material.motion;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.android.indefinite.observable.IndefiniteObservable;
import com.google.android.indefinite.observable.Observer;

/**
 * A MotionObservable is a type of <a href="http://reactivex.io/documentation/observable.html">Observable</a>
 * that specializes in motion systems that can be either active or at rest.
 * <p>
 * Throughout this documentation we will treat the words "observable" and "stream" as synonyms.
 */
public class MotionObservable<T> extends IndefiniteObservable<MotionObserver<T>> {

  public MotionObservable(Connector<MotionObserver<T>> connector) {
    super(connector);
  }

  /**
   * Subscribes to the IndefiniteObservable and ignores all incoming values.
   *
   * @see #subscribe(Observer)
   */
  public Subscription subscribe() {
    return super.subscribe(new MotionObserver<T>() {
      @Override
      public void next(T value) {
      }
    });
  }

  /**
   * A light-weight operator builder. Applies the given operation to the incoming stream.
   * <p>
   * This is the preferred method for building new operators. This builder can be used to create
   * any operator that only needs to modify or block values. All state events are forwarded
   * along.
   *
   * @param operation An operation to apply to each incoming value. The operation must handle
   * values of type {@link T} or more general types. For example, an operation that handles {@link
   * View}s can be applied to a stream of {@link TextView}s.
   * @param <U> The returned stream contains values of this type. The operation must output values
   * of this type.
   */
  public <U> MotionObservable<U> compose(final Operation<? super T, U> operation) {
    final MotionObservable<T> upstream = MotionObservable.this;

    return new MotionObservable<>(new Connector<MotionObserver<U>>() {

      @NonNull
      @Override
      public Disconnector connect(final MotionObserver<U> observer) {
        operation.preConnect(observer);
        final Subscription subscription = upstream.subscribe(new MotionObserver<T>() {

          @Override
          public void next(T value) {
            operation.next(observer, value);
          }
        });
        operation.postConnect(observer);

        return new Disconnector() {

          @Override
          public void disconnect() {
            operation.preDisconnect(observer);
            subscription.unsubscribe();
            operation.postDisconnect(observer);
          }
        };
      }
    });
  }
}
