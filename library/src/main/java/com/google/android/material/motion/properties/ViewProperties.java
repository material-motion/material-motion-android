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
import com.google.android.material.motion.springs.PointFTypeVectorizer;
import com.google.android.material.motion.springs.TypeVectorizer;

public final class ViewProperties {

  @VisibleForTesting
  public ViewProperties() {
    throw new UnsupportedOperationException();
  }

  public static final Property<View, PointF> TRANSLATION =
    new DerivativeProperty<>(
      PointF.class,
      "translation",
      new PointFTypeVectorizer(),
      View.TRANSLATION_X,
      View.TRANSLATION_Y);

  public static final Property<View, PointF> CENTER =
    new DerivativeProperty<View, PointF>(
      PointF.class,
      "center",
      new PointFTypeVectorizer(),
      View.X,
      View.Y) {

      @Override
      public float setterTransformation(View object, float value) {
        return value - object.getWidth() / 2f;
      }

      @Override
      public float getterTransformation(View object, float value) {
        return value + object.getWidth() / 2f;
      }
    };

  public static final Property<View, PointF> SCALE =
    new DerivativeProperty<>(
      PointF.class,
      "scale",
      new PointFTypeVectorizer(),
      View.SCALE_X,
      View.SCALE_Y);

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

  public static final Property<View, Float> SCROLL_X =
    new Property<View, Float>(Float.class, "scrollX") {
      @Override
      public Float get(View object) {
        return (float) object.getScrollX();
      }

      @Override
      public void set(View object, Float value) {
        object.setScrollX((int) (float) value);
      }
    };

  public static final Property<View, Float> SCROLL_Y =
    new Property<View, Float>(Float.class, "scrollY") {
      @Override
      public Float get(View object) {
        return (float) object.getScrollY();
      }

      @Override
      public void set(View object, Float value) {
        object.setScrollY((int) (float) value);
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

  public static class DerivativeProperty<O, T> extends Property<O, T> {

    public final TypeVectorizer<T> vectorizer;
    public final Property<O, Float>[] properties;
    private final float[] vector;

    @SafeVarargs
    public DerivativeProperty(
      Class<T> type,
      String name,
      TypeVectorizer<T> vectorizer,
      Property<O, Float>... properties) {
      super(type, name);
      this.vectorizer = vectorizer;
      this.properties = properties;
      this.vector = new float[vectorizer.getVectorLength()];
    }

    @Override
    public final void set(O object, T value) {
      vectorizer.vectorize(value, vector);
      for (int i = 0; i < properties.length; i++) {
        properties[i].set(object, setterTransformation(object, vector[i]));
      }
    }

    @Override
    public final T get(O object) {
      for (int i = 0; i < properties.length; i++) {
        vector[i] = getterTransformation(object, properties[i].get(object));
      }
      return vectorizer.compose(vector);
    }

    public float setterTransformation(O object, float value) {
      return value;
    }

    public float getterTransformation(O object, float value) {
      return value;
    }
  }
}
