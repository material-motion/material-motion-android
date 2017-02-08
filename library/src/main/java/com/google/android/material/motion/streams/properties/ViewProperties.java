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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.VisibleForTesting;
import android.util.Property;
import android.view.View;

import com.google.android.material.motion.streams.R;

public final class ViewProperties {

  @VisibleForTesting
  public ViewProperties() {
    throw new UnsupportedOperationException();
  }

  public static final Property<View, Float[]> TRANSLATION =
    new Property<View, Float[]>(Float[].class, "translation") {

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

  public static final Property<View, Float[]> SCALE =
    new Property<View, Float[]>(Float[].class, "scale") {

      private final Float[] array = new Float[2];

      @Override
      public void set(View object, Float[] value) {
        object.setScaleX(value[0]);
        object.setScaleY(value[1]);
      }

      @Override
      public Float[] get(View object) {
        array[0] = object.getScaleX();
        array[1] = object.getScaleY();
        return array;
      }
    };

  public static final Property<View, Float[]> PIVOT =
    new Property<View, Float[]>(Float[].class, "pivot") {

      private final Float[] array = new Float[2];

      @Override
      public void set(View object, Float[] value) {
        object.setPivotX(value[0]);
        object.setPivotY(value[1]);
      }

      @Override
      public Float[] get(View object) {
        array[0] = object.getPivotX();
        array[1] = object.getPivotY();
        return array;
      }
    };

  public static final Property<View, Float[]> ANCHOR_POINT_ADJUSTMENT =
    new TagProperty<Float[]>(
      Float[].class,
      "anchor_point_adjustment",
      R.id.gesture_anchor_point_adjustment_tag,
      new Float[]{0f, 0f}) {

      @Override
      public void set(View object, Float[] adjustment) {
        super.set(object, adjustment);

        object.setTranslationX(object.getTranslationX() + adjustment[0]);
        object.setTranslationY(object.getTranslationY() + adjustment[1]);
      }

      @Override
      public Float[] get(View object) {
        return super.get(object);
      }
    };

  public static final Property<View, Integer> BACKGROUND_COLOR =
    new Property<View, Integer>(Integer.class, "background_color") {

      @Override
      public void set(View object, Integer value) {
        object.setBackgroundColor(value);
      }

      @Override
      public Integer get(View object) {
        Drawable background = object.getBackground();
        if (background instanceof ColorDrawable) {
          return ((ColorDrawable) background).getColor();
        }
        return Color.TRANSPARENT;
      }
    };

  private static class TagProperty<T> extends Property<View, T> {

    private final int id;
    private final T initialValue;

    public TagProperty(Class<T> type, String name, @IdRes int id, T initialValue) {
      super(type, name);
      this.id = id;
      this.initialValue = initialValue;
    }

    @Override
    public void set(View object, T value) {
      object.setTag(id, value);
    }

    @Override
    public T get(View object) {
      Object value = object.getTag(id);
      if (value == null) {
        value = initialValue;
      }
      //noinspection unchecked
      return (T) value;
    }
  }
}
