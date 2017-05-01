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
import android.support.v4.util.SimpleArrayMap;
import android.util.Property;

import com.google.android.indefinite.observable.IndefiniteObservable.Connector;
import com.google.android.indefinite.observable.IndefiniteObservable.Disconnector;
import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A reactive property represents a subscribable, readable/writable value. Subscribers will receive
 * updates whenever {@link #onWrite(Object)} is invoked.
 */
public abstract class ReactiveProperty<T> {

  private static final WeakHashMap<Object, SimpleArrayMap<Property<?, ?>, ReactiveProperty<?>>>
    targetProperties =
    new WeakHashMap<>();

  public static <T, O> ReactiveProperty<T> of(O target, Property<O, T> property) {
    SimpleArrayMap<Property<?, ?>, ReactiveProperty<?>> properties = targetProperties.get(target);
    if (properties == null) {
      properties = new SimpleArrayMap<>();
      targetProperties.put(target, properties);
    }

    ReactiveProperty<?> reactiveProperty = properties.get(property);
    if (reactiveProperty == null) {
      reactiveProperty = new PropertyReactiveProperty<>(target, property);
      properties.put(property, reactiveProperty);
    }

    //noinspection unchecked
    return (ReactiveProperty<T>) reactiveProperty;
  }

  public static <T> ReactiveProperty<T> of(T initialValue) {
    return new ValueReactiveProperty<>(initialValue);
  }

  public static <T> ReactiveProperty<T> ofImmutableValue(T value) {
    return new ImmutableValueReactiveProperty<>(value);
  }

  private final List<MotionObserver<T>> observers = new CopyOnWriteArrayList<>();

  /**
   * Reads the property's value.
   */
  public abstract T read();

  /**
   * Writes the property with the given value.
   */
  public abstract void write(T value);

  /**
   * Subscribes to the property's value.
   * <p>
   * The given observer will be notified of the property's current value and every time the
   * property is written to.
   */
  public final Subscription subscribe(@NonNull final MotionObserver<T> observer) {
    return getStream().subscribe(observer);
  }

  public MotionObservable<T> getStream() {
    return new MotionObservable<>(new Connector<MotionObserver<T>>() {

      @NonNull
      @Override
      public Disconnector connect(final MotionObserver<T> observer) {
        if (!observers.contains(observer)) {
          observers.add(observer);

          observer.next(read());
        }

        return new Disconnector() {

          @Override
          public void disconnect() {
            observers.remove(observer);
          }
        };
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
   * A reactive property backed by a {@link Property}.
   */
  public static final class PropertyReactiveProperty<O, T> extends ReactiveProperty<T> {

    public final O target;
    public final Property<O, T> property;

    public PropertyReactiveProperty(O target, Property<O, T> property) {
      this.target = target;
      this.property = property;
    }

    @Override
    public T read() {
      return property.get(target);
    }

    @Override
    public void write(T value) {
      property.set(target, value);

      onWrite(value);
    }
  }

  /**
   * A reactive property backed by a value.
   */
  private static class ValueReactiveProperty<T> extends ReactiveProperty<T> {

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

  private static class ImmutableValueReactiveProperty<T> extends ValueReactiveProperty<T> {

    public ImmutableValueReactiveProperty(T initialValue) {
      super(initialValue);
    }

    @Override
    public void write(T value) {
      throw new UnsupportedOperationException();
    }
  }
}
