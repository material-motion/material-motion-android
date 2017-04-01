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
package com.google.android.material.motion.sources;

import android.animation.Keyframe;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.interactions.Tween;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionObserver.SimpleMotionObserver;
import com.google.android.material.motion.Source;

public class TweenSource<O, T> extends Source<T> {

  private static final TimeInterpolator defaultInterpolator =
    new AccelerateDecelerateInterpolator();
  private static final Object[] lengthTwoArray = new Object[2];

  private final Tween<O, T> tween;
  private final ValueAnimator animator;

  private Subscription evaluatorSubscription;
  private Subscription valuesSubscription;
  private Subscription offsetsSubscription;
  private Subscription timingFunctionsSubscription;
  private Subscription durationSubscription;
  private Subscription delaySubscription;
  private Subscription timingFunctionSubscription;

  private TypeEvaluator<T> lastEvaluator;
  private Object[] lastValues;
  private float[] lastOffsets;
  private TimeInterpolator[] lastTimingFunctions;
  private Long lastDuration;
  private Long lastDelay;
  private TimeInterpolator lastTimingFunction;

  private boolean initialized;

  public TweenSource(Tween<O, T> tween) {
    super(tween);
    this.tween = tween;
    this.animator = new ValueAnimator();
  }

  @Override
  protected void onConnect(final MotionObserver<T> observer) {
    animator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        //noinspection unchecked
        observer.next((T) animation.getAnimatedValue());
      }
    });
  }

  @Override
  protected void onEnable(MotionObserver<T> observer) {
    initialized = false;

    evaluatorSubscription = tween.evaluator.subscribe(new SimpleMotionObserver<TypeEvaluator<T>>() {
      @Override
      public void next(TypeEvaluator<T> value) {
        lastEvaluator = value;
        startAnimator();
      }
    });
    valuesSubscription = tween.values.subscribe(new SimpleMotionObserver<T[]>() {
      @Override
      public void next(T[] value) {
        lastValues = value;
        startAnimator();
      }
    });
    offsetsSubscription = tween.offsets.subscribe(new SimpleMotionObserver<float[]>() {
      @Override
      public void next(float[] value) {
        lastOffsets = value;
        startAnimator();
      }
    });
    timingFunctionsSubscription = tween.timingFunctions.subscribe(
      new SimpleMotionObserver<TimeInterpolator[]>() {

        @Override
        public void next(TimeInterpolator[] value) {
          lastTimingFunctions = value;
          startAnimator();
        }
      });
    durationSubscription = tween.duration.subscribe(new SimpleMotionObserver<Long>() {
      @Override
      public void next(Long value) {
        lastDuration = value;
        startAnimator();
      }
    });
    delaySubscription = tween.delay.subscribe(new SimpleMotionObserver<Long>() {
      @Override
      public void next(Long value) {
        lastDelay = value;
        startAnimator();
      }
    });
    timingFunctionSubscription =
      tween.timingFunction.subscribe(new SimpleMotionObserver<TimeInterpolator>() {
        @Override
        public void next(TimeInterpolator value) {
          lastTimingFunction = value;
          startAnimator();
        }
      });

    initialized = true;
    startAnimator();
  }

  private void startAnimator() {
    if (!initialized) {
      return;
    }

    animator.cancel();

    if (lastValues == null || lastValues.length == 0) {
      return;
    }

    Object[] values;
    if (lastValues.length == 1) {
      values = lengthTwoArray;
      values[0] = tween.property.get(tween.target);
      values[1] = lastValues[0];
    } else {
      values = lastValues;
    }

    if (lastOffsets != null && lastOffsets.length != values.length) {
      return;
    }
    if (lastTimingFunctions != null && lastTimingFunctions.length != values.length - 1) {
      return;
    }

    if (lastOffsets == null && lastTimingFunctions == null) {
      animator.setObjectValues(values);
    } else {
      Keyframe[] keyframes = new Keyframe[values.length];
      for (int i = 0; i < values.length; i++) {
        float offset;
        if (lastOffsets != null) {
          offset = lastOffsets[i];
        } else {
          offset = (float) i / (values.length - 1);
        }

        keyframes[i] = Keyframe.ofObject(offset, values[i]);

        if (lastTimingFunctions != null && i >= 1) {
          keyframes[i].setInterpolator(lastTimingFunctions[i - 1]);
        }
      }
      animator.getValues()[0].setKeyframes(keyframes);
    }
    animator.setEvaluator(lastEvaluator);

    if (lastDuration != null) {
      animator.setDuration(lastDuration);
    } else {
      animator.setDuration(0);
    }

    if (lastDelay != null) {
      animator.setStartDelay(lastDelay);
    } else {
      animator.setStartDelay(0);
    }

    if (lastTimingFunction != null) {
      animator.setInterpolator(lastTimingFunction);
    } else {
      animator.setInterpolator(defaultInterpolator);
    }

    animator.start();
  }

  @Override
  protected void onDisable(MotionObserver<T> observer) {
    animator.cancel();

    evaluatorSubscription.unsubscribe();
    valuesSubscription.unsubscribe();
    offsetsSubscription.unsubscribe();
    timingFunctionsSubscription.unsubscribe();
    durationSubscription.unsubscribe();
    delaySubscription.unsubscribe();
    timingFunctionSubscription.unsubscribe();
  }
}
