package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;

import com.google.android.material.motion.FilterOperation;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureRecognizerState;

public final class OnRecognitionState {

  @VisibleForTesting
  OnRecognitionState() {
    throw new UnsupportedOperationException();
  }

  /**
   * Only forwards the gesture recognizer if its state matches the provided value.
   */
  public static <T extends GestureRecognizer> Operation<T, T> onRecognitionState(
    @GestureRecognizerState
    final int state) {
    return new FilterOperation<T>() {
      @Override
      public boolean filter(T value) {
        return value.getState() == state;
      }
    };
  }

  /**
   * Only forwards the gesture recognizer if its state matches any of the provided values.
   */
  public static <T extends GestureRecognizer> Operation<T, T> onRecognitionState(
    @GestureRecognizerState
    final int... states) {
    return new FilterOperation<T>() {
      @Override
      public boolean filter(T value) {
        int s = value.getState();
        for (@GestureRecognizerState int state : states) {
          if (state == s) {
            return true;
          }
        }
        return false;
      }
    };
  }
}
