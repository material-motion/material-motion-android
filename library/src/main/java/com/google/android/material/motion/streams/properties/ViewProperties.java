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
package com.google.android.material.motion.streams.properties;

import android.support.annotation.VisibleForTesting;
import android.util.Property;
import android.view.View;

public final class ViewProperties {

  @VisibleForTesting
  public ViewProperties() {
    throw new UnsupportedOperationException();
  }

  public static final Property<View, Float[]> TRANSLATION = new Property<View, Float[]>(
    Float[].class, "translation") {

    private final Float[] array = new Float[2];

    @Override
    public void set(View object, Float[] value) {
      object.setTranslationX(value[0]);
      object.setTranslationY(value[1]);
    }

    @Override
    public Float[] get(View object) {
      array[0] = object.getTranslationX();
      array[1] = object.getTranslationY();
      return array;
    }
  };
}
