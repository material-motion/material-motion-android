package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;

import java.util.Map;

public final class Rewrite {

  @VisibleForTesting
  Rewrite() {
    throw new UnsupportedOperationException();
  }

  public static <T, U> Operation<T, U> rewriteTo(final U value) {
    return new MapOperation<T, U>() {
      @Override
      public U transform(T ignored) {
        return value;
      }
    };
  }

  public static <T, U> Operation<T, U> rewrite(final Map<T, U> map) {
    return new Operation<T, U>() {
      @Override
      public void next(MotionObserver<U> observer, T value) {
        if (map.containsKey(value)) {
          observer.next(map.get(value));
        }
      }
    };
  }

  public static <T, U> Operation<T, U> rewrite(final SimpleArrayMap<T, U> map) {
    return new Operation<T, U>() {
      @Override
      public void next(MotionObserver<U> observer, T value) {
        if (map.containsKey(value)) {
          observer.next(map.get(value));
        }
      }
    };
  }

  public static Operation<Float, Float> rewriteRange(
    final float start, final float end, final float destinationStart, final float destinationEnd) {
    return new MapOperation<Float, Float>() {
      @Override
      public Float transform(Float value) {
        float position = value - start;

        float vector = end - start;
        if (vector == 0) {
          return destinationStart;
        }
        float progress = position / vector;

        float destinationVector = destinationEnd - destinationStart;
        float destinationPosition = destinationVector * progress;

        return destinationStart + destinationPosition;
      }
    };
  }
}
