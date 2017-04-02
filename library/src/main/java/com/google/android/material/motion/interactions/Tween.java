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
package com.google.android.material.motion.interactions;

import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.util.Property;

import com.google.android.material.motion.ConstraintApplicator;
import com.google.android.material.motion.Interaction;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionRuntime;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.sources.TweenSource;

public class Tween<O, T> extends Interaction<O, T> {

  public final O target;
  public final Property<? super O, T> property;

  public final ReactiveProperty<TypeEvaluator<T>> evaluator;
  public final ReactiveProperty<T[]> values;
  public final ReactiveProperty<float[]> offsets;
  public final ReactiveProperty<TimeInterpolator[]> timingFunctions;

  public final ReactiveProperty<Long> duration;
  public final ReactiveProperty<Long> delay;
  public final ReactiveProperty<TimeInterpolator> timingFunction;

  private final MotionObservable<T> stream;


  public Tween(
    O target,
    Property<? super O, T> property,
    TypeEvaluator<T> evaluator,
    T[] values,
    float[] offsets,
    TimeInterpolator[] timingFunctions,
    Long duration,
    Long delay,
    TimeInterpolator timingFunction) {
    this(
      target,
      property,
      ReactiveProperty.of(evaluator),
      ReactiveProperty.of(values),
      ReactiveProperty.of(offsets),
      ReactiveProperty.of(timingFunctions),
      ReactiveProperty.of(duration),
      ReactiveProperty.of(delay),
      ReactiveProperty.of(timingFunction));
  }

  public Tween(
    O target,
    Property<? super O, T> property,
    ReactiveProperty<TypeEvaluator<T>> evaluator,
    ReactiveProperty<T[]> values,
    ReactiveProperty<float[]> offsets,
    ReactiveProperty<TimeInterpolator[]> timingFunctions,
    ReactiveProperty<Long> duration,
    ReactiveProperty<Long> delay,
    ReactiveProperty<TimeInterpolator> timingFunction) {
    this.target = target;
    this.property = property;
    this.evaluator = evaluator;
    this.values = values;
    this.offsets = offsets;
    this.timingFunctions = timingFunctions;
    this.duration = duration;
    this.delay = delay;
    this.timingFunction = timingFunction;

    this.stream = new TweenSource<>(this).getStream();
  }

  @Override
  public void apply(MotionRuntime runtime, O target, ConstraintApplicator<T> constraints) {
    runtime.write(constraints.apply(stream), target, property);
  }
}
