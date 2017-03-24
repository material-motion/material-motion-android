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
package com.google.android.reactive.motion.interactions;

import android.animation.ValueAnimator;
import android.util.Property;

import com.google.android.reactive.motion.Interaction;
import com.google.android.reactive.motion.MotionObservable;
import com.google.android.reactive.motion.MotionRuntime;
import com.google.android.reactive.motion.ReactiveProperty;
import com.google.android.reactive.motion.sources.TweenSource;

public class Tween<O, T> extends Interaction<O, T> {

  public final ReactiveProperty<ValueAnimator> animator;

  private final Property<? super O, T> property;
  private final MotionObservable<T> stream;

  public Tween(Property<? super O, T> property, ValueAnimator animator) {
    this(property, ReactiveProperty.of(animator));
  }

  public Tween(Property<? super O, T> property, ReactiveProperty<ValueAnimator> animator) {
    this.property = property;
    this.animator = animator;

    this.stream = new TweenSource<>(this).getStream();
  }

  @Override
  public void apply(MotionRuntime runtime, O target) {
    runtime.write(flatten(stream), target, property);
  }
}
