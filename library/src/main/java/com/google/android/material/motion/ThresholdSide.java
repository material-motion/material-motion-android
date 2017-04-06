package com.google.android.material.motion;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ThresholdSide.BELOW, ThresholdSide.WITHIN, ThresholdSide.ABOVE})
@Retention(RetentionPolicy.SOURCE)
public @interface ThresholdSide {

  int BELOW = 0;
  int WITHIN = 1;
  int ABOVE = 2;
}
