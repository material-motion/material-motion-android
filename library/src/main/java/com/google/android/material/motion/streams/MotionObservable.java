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

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.observable.Observer;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A MotionObservable is a type of <a href="http://reactivex.io/documentation/observable.html">Observable</a>
 * that specializes in motion systems that can be either active or at rest.
 * <p>
 * Throughout this documentation we will treat the words "observable" and "stream" as synonyms.
 *
 * @see <a href="https://material-motion.github.io/material-motion/starmap/specifications/streams/MotionObservable">The
 * motion observable specification</a>
 */
public class MotionObservable<T> extends IndefiniteObservable<MotionObserver<T>> {

  /**
   * The stream is at rest.
   */
  public static final int AT_REST = 0;

  /**
   * The stream is currently active.
   */
  public static final int ACTIVE = 1;

  public MotionObservable(Subscriber<MotionObserver<T>> subscriber) {
    super(subscriber);
  }

  /**
   * The possible states that a stream can be in.
   * <p>
   * What "active" means is stream-dependant. The stream is active if you can answer yes to any of
   * the following questions: <ul> <li>Is my animation currently animating?</li> <li>Is my
   * physical simulation still moving?</li> <li>Is my gesture recognizer in the .began or .changed
   * state?</li> </ul> Momentary events such as taps may emit {@link #ACTIVE} immediately followed
   * by {@link #AT_REST}.
   */
  @IntDef({AT_REST, ACTIVE})
  @Retention(RetentionPolicy.SOURCE)
  public @interface MotionState {

  }

  /**
   * An observer with an additional {@link #state(int)} method.
   */
  public interface MotionObserver<T> extends Observer<T> {

    @Override
    void next(T value);

    /**
     * A method to handle new state values from upstream.
     */
    void state(@MotionState int state);
  }

  /**
   * An operation is able to transform incoming values before choosing whether or not to pass them
   * downstream.
   *
   * @param <T> The incoming value type.
   * @param <U> The downstream value type.
   */
  public interface Operation<T, U> {

    /**
     * Modifies the given value before passing it downstream, or blocks the value.
     *
     * @param observer Downstream.
     * @param value The value from upstream.
     */
    void next(MotionObserver<U> observer, T value);
  }

  /**
   * A light-weight operator builder.
   * <p>
   * This is the preferred method for building new operators. This builder can be used to create
   * any operator that only needs to modify or block values. All state events are forwarded
   * along.
   *
   * @see <a href="https://material-motion.github.io/material-motion/starmap/specifications/streams/operators/$._operator">The
   * operator() specification</a>
   */
  public <U> MotionObservable<U> operator(final Operation<T, U> operation) {
    final MotionObservable<T> upstream = MotionObservable.this;

    return new MotionObservable<>(new Subscriber<MotionObserver<U>>() {
      @Nullable
      @Override
      public Unsubscriber subscribe(final MotionObserver<U> observer) {
        final Subscription subscription = upstream.subscribe(new MotionObserver<T>() {
          @Override
          public void next(T value) {
            operation.next(observer, value);
          }

          @Override
          public void state(@MotionState int state) {
            observer.state(state);
          }
        });

        return new Unsubscriber() {
          @Override
          public void unsubscribe() {
            subscription.unsubscribe();
          }
        };
      }
    });
  }
}
