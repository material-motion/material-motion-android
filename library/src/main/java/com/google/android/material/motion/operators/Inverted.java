package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.Operation;

public final class Inverted {

  @VisibleForTesting
  Inverted() {
    throw new UnsupportedOperationException();
  }

  public static Operation<Boolean, Boolean> inverted() {
    return new MapOperation<Boolean, Boolean>() {
      @Override
      public Boolean transform(Boolean value) {
        return !value;
      }
    };
  }
}
