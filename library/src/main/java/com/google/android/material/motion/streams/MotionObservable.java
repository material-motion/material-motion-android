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
import android.util.Property;
import android.view.View;
import android.widget.TextView;

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
   * Subscribes to the IndefiniteObservable and ignores all incoming values.
   *
   * @see {@link #subscribe(Observer)}
   */
  public Subscription subscribe() {
    return super.subscribe(new MotionObserver<T>() {
      @Override
      public void next(T value) {
      }

      @Override
      public void state(@MotionState int state) {
      }
    });
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
  public static abstract class MotionObserver<T> extends Observer<T> {

    @Override
    public abstract void next(T value);

    /**
     * A method to handle new incoming state values.
     */
    public abstract void state(@MotionState int state);
  }

  /**
   * An operation is able to transform incoming values before choosing whether or not to pass them
   * downstream.
   *
   * @param <T> The incoming value type.
   * @param <U> The downstream value type.
   */
  public static abstract class Operation<T, U> {

    /**
     * Transforms the incoming value before passing it to the observer, or blocks the value.
     *
     * @param value The incoming value.
     */
    public abstract void next(MotionObserver<U> observer, T value);
  }

  /**
   * A transformation transforms incoming values before they are passed downstream.
   *
   * @param <T> The incoming value type.
   * @param <U> The downstream value type.
   */
  public static abstract class Transformation<T, U> extends Operation<T, U> {

    /**
     * Transforms the given value.
     */
    public abstract U transform(T value);

    @Override
    public final void next(MotionObserver<U> observer, T value) {
      observer.next(transform(value));
    }
  }

  /**
   * A predicate evaluates whether to pass a value downstream.
   */
  public static abstract class Predicate<T> extends Operation<T, T> {

    /**
     * Evaluates whether to pass the value.
     */
    public abstract boolean evaluate(T value);

    @Override
    public void next(MotionObserver<T> observer, T value) {
      if (evaluate(value)) {
        observer.next(value);
      }
    }
  }

  /**
   * A property that can be read into a MotionObservable stream.
   */
  public static abstract class ScopedReadable<T> extends Operation<T, T> {

    /**
     * Reads the property's value.
     */
    public abstract T read();

    @Override
    public void next(MotionObserver<T> observer, T value) {
      observer.next(read());
    }
  }

  /**
   * A property that can be written from a MotionObservable stream.
   */
  public static abstract class ScopedWritable<T> extends Operation<T, T> {

    /**
     * Writes the property with the given value.
     */
    public abstract void write(T value);

    @Override
    public void next(MotionObserver<T> observer, T value) {
      write(value);
      observer.next(value);
    }
  }

  /**
   * A property that can be read into a MotionObservable stream.
   *
   * @deprecated in #develop#. Use {@link ScopedReadable} instead.
   */
  @Deprecated
  public static abstract class InlineReadable<T> extends ScopedReadable<T> {
  }

  /**
   * A property that can be written from a MotionObservable stream.
   *
   * @deprecated in #develop#. Use {@link ScopedWritable} instead.
   */
  @Deprecated
  public static abstract class InlineWritable<T> extends ScopedWritable<T> {
  }

  /**
   * @deprecated in #develop. Use {@link #extend(Operation)} for extended operators instead.
   */
  @Deprecated
  public <U> MotionObservable<U> operator(final Operation<? super T, U> operation) {
    return apply(operation);
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
  private <U> MotionObservable<U> apply(final Operation<? super T, U> operation) {
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

  /**
   * Transforms the items emitted by an Observable by applying a function to each item.
   */
  public <U> MotionObservable<U> map(final Transformation<T, U> transformation) {
    return apply(transformation);
  }

  /**
   * Only emits those values from an Observable that satisfy a predicate.
   */
  public MotionObservable<T> filter(final Predicate<T> predicate) {
    return apply(predicate);
  }

  /**
   * Writes the values from an Observable onto the given unscoped property.
   */
  public <O> MotionObservable<T> write(final O target, final Property<O, T> property) {
    return apply(new ScopedWritable<T>() {
      @Override
      public void write(T value) {
        property.set(target, value);
      }
    });
  }

  /**
   * Writes the values from an Observable onto the given inline property.
   */
  public MotionObservable<T> write(final ScopedWritable<T> property) {
    return apply(property);
  }

  /**
   * Supports extended operators. Pass in an extended operator to execute it.
   */
  public <U> MotionObservable<U> extend(final Operation<? super T, U> operation) {
    return apply(operation);
  }
}
