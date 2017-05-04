package com.google.android.material.motion.operators;

import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;

import com.google.android.material.motion.MapOperation;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.Operation;
import com.google.android.material.motion.SameTypedMapOperation;

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

  public static <T, U> Operation<T, U> rewrite(T from, U to) {
    return new Operation<T, U>() {
      @Override
      public void next(MotionObserver<U> observer, T value) {
        if (value == from) {
          observer.next(to);
        }
      }
    };
  }

  public static <T, U> Operation<T, U> rewrite(T from1, U to1, T from2, U to2) {
    return new Operation<T, U>() {
      @Override
      public void next(MotionObserver<U> observer, T value) {
        if (value == from1) {
          observer.next(to1);
        } else if (value == from2) {
          observer.next(to2);
        }
      }
    };
  }

  public static <T, U> Operation<T, U> rewrite(T from1, U to1, T from2, U to2, T from3, U to3) {
    return new Operation<T, U>() {
      @Override
      public void next(MotionObserver<U> observer, T value) {
        if (value == from1) {
          observer.next(to1);
        } else if (value == from2) {
          observer.next(to2);
        } else if (value == from3) {
          observer.next(to3);
        }
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
    return new SameTypedMapOperation<Float>() {
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
