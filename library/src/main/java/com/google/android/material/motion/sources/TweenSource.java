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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.support.v4.util.SimpleArrayMap;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.indefinite.observable.IndefiniteObservable.Subscription;
import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionObserver.SimpleMotionObserver;
import com.google.android.material.motion.MotionState;
import com.google.android.material.motion.Source;
import com.google.android.material.motion.interactions.Tween;

public class TweenSource<O, T> extends Source<T> {

  private static final TimeInterpolator defaultInterpolator =
    new AccelerateDecelerateInterpolator();
  private static final Object[] lengthTwoArray = new Object[2];

  private final Tween<O, T> interaction;
  private final ValueAnimator animator;
  private final SimpleArrayMap<Observer<T>, AnimatorUpdateListener> updateListeners =
    new SimpleArrayMap<>();

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

  public TweenSource(Tween<O, T> interaction) {
    super(interaction);
    this.interaction = interaction;
    animator = new ValueAnimator();
    animator.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        for (int i = 0, count = updateListeners.size(); i < count; i++) {
          updateListeners.valueAt(i).onAnimationUpdate(animation);
        }
      }
    });
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        interaction.state.write(MotionState.ACTIVE);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        interaction.state.write(MotionState.AT_REST);
      }
    });
  }

  @Override
  protected void onConnect(final MotionObserver<T> observer) {
    updateListeners.put(observer, new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        //noinspection unchecked
        observer.next((T) animation.getAnimatedValue());
      }
    });
  }

  @Override
  protected void onEnable() {
    initialized = false;

    evaluatorSubscription = interaction.evaluator.subscribe(new
                                                              SimpleMotionObserver<TypeEvaluator<T>>() {
      @Override
      public void next(TypeEvaluator<T> value) {
        lastEvaluator = value;
        startAnimator();
      }
    });
    valuesSubscription = interaction.values.subscribe(new SimpleMotionObserver<T[]>() {
      @Override
      public void next(T[] value) {
        lastValues = value;
        startAnimator();
      }
    });
    offsetsSubscription = interaction.offsets.subscribe(new SimpleMotionObserver<float[]>() {
      @Override
      public void next(float[] value) {
        lastOffsets = value;
        startAnimator();
      }
    });
    timingFunctionsSubscription = interaction.timingFunctions.subscribe(
      new SimpleMotionObserver<TimeInterpolator[]>() {

        @Override
        public void next(TimeInterpolator[] value) {
          lastTimingFunctions = value;
          startAnimator();
        }
      });
    durationSubscription = interaction.duration.subscribe(new SimpleMotionObserver<Long>() {
      @Override
      public void next(Long value) {
        lastDuration = value;
        startAnimator();
      }
    });
    delaySubscription = interaction.delay.subscribe(new SimpleMotionObserver<Long>() {
      @Override
      public void next(Long value) {
        lastDelay = value;
        startAnimator();
      }
    });
    timingFunctionSubscription =
      interaction.timingFunction.subscribe(new SimpleMotionObserver<TimeInterpolator>() {
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
      values[0] = interaction.property.get(interaction.target);
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
  protected void onDisable() {
    animator.cancel();

    evaluatorSubscription.unsubscribe();
    valuesSubscription.unsubscribe();
    offsetsSubscription.unsubscribe();
    timingFunctionsSubscription.unsubscribe();
    durationSubscription.unsubscribe();
    delaySubscription.unsubscribe();
    timingFunctionSubscription.unsubscribe();
  }

  @Override
  protected void onDisconnect(MotionObserver<T> observer) {
    updateListeners.remove(observer);
  }
}
