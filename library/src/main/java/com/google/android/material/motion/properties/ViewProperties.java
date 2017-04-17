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
package com.google.android.material.motion.properties;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.VisibleForTesting;
import android.util.Property;
import android.view.View;

import com.google.android.material.motion.R;

public final class ViewProperties {

  @VisibleForTesting
  public ViewProperties() {
    throw new UnsupportedOperationException();
  }

  public static final Property<View, PointF> TRANSLATION =
    new Property<View, PointF>(PointF.class, "translation") {

      @Override
      public void set(View object, PointF value) {
        object.setTranslationX(value.x);
        object.setTranslationY(value.y);
      }

      @Override
      public PointF get(View object) {
        PointF pointF = new PointF();
        pointF.x = object.getTranslationX();
        pointF.y = object.getTranslationY();
        return pointF;
      }
    };

  public static final Property<View, PointF> CENTER =
    new Property<View, PointF>(PointF.class, "center") {

      @Override
      public void set(View object, PointF value) {
        object.setX(value.x - object.getWidth() / 2f);
        object.setY(value.y - object.getHeight() / 2f);
      }

      @Override
      public PointF get(View object) {
        PointF pointF = new PointF();
        pointF.x = object.getX() + object.getWidth() / 2f;
        pointF.y = object.getY() + object.getHeight() / 2f;
        return pointF;
      }
    };

  public static final Property<View, PointF> SCALE =
    new Property<View, PointF>(PointF.class, "scale") {

      @Override
      public void set(View object, PointF value) {
        object.setScaleX(value.x);
        object.setScaleY(value.y);
      }

      @Override
      public PointF get(View object) {
        PointF pointF = new PointF();
        pointF.x = object.getScaleX();
        pointF.y = object.getScaleY();
        return pointF;
      }
    };

  public static final Property<View, PointF> PIVOT =
    new Property<View, PointF>(PointF.class, "pivot") {

      @Override
      public void set(View object, PointF value) {
        object.setPivotX(value.x);
        object.setPivotY(value.y);
      }

      @Override
      public PointF get(View object) {
        PointF pointF = new PointF();
        pointF.x = object.getPivotX();
        pointF.y = object.getPivotY();
        return pointF;
      }
    };

  public static final Property<View, PointF> ANCHOR_POINT_ADJUSTMENT =
    new TagProperty<PointF>(
      PointF.class,
      "anchor_point_adjustment",
      R.id.gesture_anchor_point_adjustment_tag,
      new PointF(0f, 0f)) {

      @Override
      public void set(View object, PointF adjustment) {
        super.set(object, adjustment);

        object.setTranslationX(object.getTranslationX() + adjustment.x);
        object.setTranslationY(object.getTranslationY() + adjustment.y);
      }

      @Override
      public PointF get(View object) {
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
