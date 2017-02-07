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

import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;
import android.util.Property;

import java.util.WeakHashMap;

public final class ReactiveProperties {

  private static final WeakHashMap<Object, SimpleArrayMap<Property<?, ?>, ReactiveProperty<?>>> targetProperties = new WeakHashMap<>();

  @VisibleForTesting
  public ReactiveProperties() {
    throw new UnsupportedOperationException();
  }

  public static <T, O> ReactiveProperty<T> of(O target, Property<O, T> property) {
    SimpleArrayMap<Property<?, ?>, ReactiveProperty<?>> properties = targetProperties.get(target);
    if (properties == null) {
      properties = new SimpleArrayMap<>();
      targetProperties.put(target, properties);
    }

    ReactiveProperty<?> reactiveProperty = properties.get(property);
    if (reactiveProperty == null) {
      reactiveProperty = new UnscopedReactiveProperty<>(target, property);
      properties.put(property, reactiveProperty);
    }

    //noinspection unchecked
    return (ReactiveProperty<T>) reactiveProperty;
  }
}
