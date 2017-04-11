package com.google.android.material.motion.operators;

import android.support.annotation.IntDef;
import android.support.annotation.VisibleForTesting;

import com.google.android.indefinite.observable.Observer;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Threshold {

  @IntDef({ThresholdSide.BELOW, ThresholdSide.WITHIN, ThresholdSide.ABOVE})
  @Retention(RetentionPolicy.SOURCE)
  public @interface ThresholdSide {

    int BELOW = 0;
    int WITHIN = 1;
    int ABOVE = 2;
  }

  @VisibleForTesting
  Threshold() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Comparable<T>> Operation<T, Integer> threshold(final T threshold) {
    return thresholdRange(threshold, threshold);
  }

  public static <T extends Comparable<T>> Operation<T, Integer> thresholdRange(
    final T min, final T max) {
    return new Operation<T, Integer>() {
      @Override
      public void next(MotionObserver<Integer> observer, T value) {
        if (min.compareTo(max) > 0) {
          return;
        }

        if (value.compareTo(min) < 0) {
          observer.next(ThresholdSide.BELOW);
        } else if (value.compareTo(max) > 0) {
          observer.next(ThresholdSide.ABOVE);
        } else {
          observer.next(ThresholdSide.WITHIN);
        }
      }
    };
  }
}
