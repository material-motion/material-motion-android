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

import android.annotation.TargetApi;
import android.graphics.PointF;
import android.os.Build;
import android.view.View;

import com.google.android.material.motion.properties.ViewProperties;

/**
 * A ReactiveView is a view that wraps around an Android View class.
 * It contains Reactive Properties that you can subscribe to.
 */
public class ReactiveView {
  private final View view;

  public ReactiveView(View view) {
    this.view = view;
  }

  public ReactiveProperty<PointF> translation() {
    return ReactiveProperty.of(view, ViewProperties.TRANSLATION);
  }

  public ReactiveProperty<PointF> center() {
    return ReactiveProperty.of(view, ViewProperties.CENTER);
  }

  public ReactiveProperty<PointF> scale() {
    return ReactiveProperty.of(view, ViewProperties.SCALE);
  }

  public ReactiveProperty<PointF> pivot() {
    return ReactiveProperty.of(view, ViewProperties.PIVOT);
  }

  public ReactiveProperty<Integer> backgroundColor() {
    return ReactiveProperty.of(view, ViewProperties.BACKGROUND_COLOR);
  }

  public ReactiveProperty<Float> translationX() {
    return ReactiveProperty.of(view, View.TRANSLATION_X);
  }

  public ReactiveProperty<Float> translationY() {
    return ReactiveProperty.of(view, View.TRANSLATION_Y);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ReactiveProperty<Float> translationZ() {
    return ReactiveProperty.of(view, View.TRANSLATION_Z);
  }

  public ReactiveProperty<Float> rotationX() {
    return ReactiveProperty.of(view, View.ROTATION_X);
  }

  public ReactiveProperty<Float> rotationY() {
    return ReactiveProperty.of(view, View.ROTATION_Y);
  }

  public ReactiveProperty<Float> scaleX() {
    return ReactiveProperty.of(view, View.SCALE_X);
  }

  public ReactiveProperty<Float> scaleY() {
    return ReactiveProperty.of(view, View.SCALE_Y);
  }

  public ReactiveProperty<Float> x() {
    return ReactiveProperty.of(view, View.X);
  }

  public ReactiveProperty<Float> y() {
    return ReactiveProperty.of(view, View.Y);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ReactiveProperty<Float> z() {
    return ReactiveProperty.of(view, View.Z);
  }

  public ReactiveProperty<Float> alpha() {
    return ReactiveProperty.of(view, View.ALPHA);
  }
}
