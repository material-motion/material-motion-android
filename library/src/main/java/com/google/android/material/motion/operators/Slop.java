package com.google.android.material.motion.operators;

import android.support.annotation.IntDef;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;

import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.RawOperation;
import com.google.android.material.motion.operators.Threshold.ThresholdSide;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.google.android.material.motion.operators.Dedupe.dedupe;
import static com.google.android.material.motion.operators.IgnoreUntil.ignoreUntil;
import static com.google.android.material.motion.operators.Rewrite.rewrite;
import static com.google.android.material.motion.operators.Threshold.thresholdRange;

public final class Slop {

  @IntDef({SlopEvent.EXIT, SlopEvent.RETURN})
  @Retention(RetentionPolicy.SOURCE)
  public @interface SlopEvent {

    int EXIT = 0;
    int RETURN = 1;
  }

  private static final SimpleArrayMap<Integer, Integer> THRESHOLD_SIDE_TO_SLOP_EVENT_MAP =
    new SimpleArrayMap<>();

  static {
    Slop.THRESHOLD_SIDE_TO_SLOP_EVENT_MAP.put(ThresholdSide.BELOW, SlopEvent.EXIT);
    Slop.THRESHOLD_SIDE_TO_SLOP_EVENT_MAP.put(ThresholdSide.WITHIN, SlopEvent.RETURN);
    Slop.THRESHOLD_SIDE_TO_SLOP_EVENT_MAP.put(ThresholdSide.ABOVE, SlopEvent.EXIT);
  }

  @VisibleForTesting
  Slop() {
    throw new UnsupportedOperationException();
  }

  public static <T extends Comparable<T>> RawOperation<T, Integer> slop(final T min, final T max) {
    return new RawOperation<T, Integer>() {
      @Override
      public MotionObservable<Integer> compose(MotionObservable<T> stream) {
        return stream
          .compose(thresholdRange(min, max))
          .compose(rewrite(THRESHOLD_SIDE_TO_SLOP_EVENT_MAP))
          .compose(dedupe())
          .compose(ignoreUntil(SlopEvent.EXIT));
      }
    };
  }
}
