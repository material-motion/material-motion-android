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
package com.google.android.material.motion.streams.springs;

import android.os.Handler;
import android.support.v4.util.SimpleArrayMap;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A composite spring is made of multiple rebound springs. The composite spring manages the
 * aggregate state of each individual spring, and reports aggregate state changes to its listeners.
 */
public final class CompositeReboundSpring {

  private final Spring[] springs;
  private final SpringTracker tracker;

  private final List<CompositeSpringListener> listeners = new CopyOnWriteArrayList<>();

  /**
   * Create a new composite spring to track the given individual springs.
   */
  public CompositeReboundSpring(Spring[] springs) {
    this.springs = springs;
    this.tracker = new SpringTracker(this);
  }

  /**
   * Adds a listener to the composite spring. The first listener to be added will add a private
   * listener to each individual spring.
   */
  public void addListener(CompositeSpringListener listener) {
    if (listeners.isEmpty()) {
      tracker.start();
    }

    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Removes a listener from the composite spring. The last listener to be removed will remove the
   * private listener to each individual spring.
   */
  public void removeListener(CompositeSpringListener listener) {
    listeners.remove(listener);

    if (listeners.isEmpty()) {
      tracker.stop();
    }
  }

  /**
   * Returns whether the aggregate state of the composite spring is at rest.
   */
  public boolean isAtRest() {
    return tracker.isAtRest();
  }

  /**
   * Returns the current values of each individual spring. The ordering of the values is
   * consistent with the ordering of springs provided in the constructor.
   */
  public float[] getCurrentValues() {
    return tracker.currentValues;
  }

  private void onCompositeSpringActivate() {
    for (CompositeSpringListener listener : listeners) {
      listener.onCompositeSpringActivate();
    }
  }

  private void onCompositeSpringUpdate() {
    for (CompositeSpringListener listener : listeners) {
      listener.onCompositeSpringUpdate(tracker.currentValues);
    }
  }

  private void onCompositeSpringAtRest() {
    for (CompositeSpringListener listener : listeners) {
      listener.onCompositeSpringAtRest();
    }
  }

  /**
   * A class that observes each individual spring and combines them into the respective aggregate
   * state changes.
   * <p>
   * Because we do not know which individual springs are expected to change state at any given
   * time, we use a {@link Handler} to aggregate all the individual state changes in a frame. At
   * the end of each frame, the individual state changes are processed and an aggregate state
   * change is published.
   * <p>
   * {@link #currentValues} and {@link #currentAtRestStates} should be queried to get the current
   * values and states. They may disagree with the individual springs themselves. That is expected
   * and is caused by the frame-by-frame aggregation strategy.
   */
  private static class SpringTracker {

    private final CompositeReboundSpring compositeReboundSpring;

    private boolean wasAtRest;

    private final float[] currentValues;
    private final SimpleArrayMap<Spring, Float> updatedValues = new SimpleArrayMap<>();

    private final boolean[] currentAtRestStates;
    private final SimpleArrayMap<Spring, Boolean> updatedAtRestStates = new SimpleArrayMap<>();

    private final Handler handler = new Handler();

    public SpringTracker(CompositeReboundSpring compositeReboundSpring) {
      this.compositeReboundSpring = compositeReboundSpring;

      this.currentValues = new float[compositeReboundSpring.springs.length];
      this.currentAtRestStates = new boolean[compositeReboundSpring.springs.length];
    }

    /**
     * Adds the listener to each individual spring.
     */
    private void start() {
      for (int i = 0, count = compositeReboundSpring.springs.length; i < count; i++) {
        Spring spring = compositeReboundSpring.springs[i];

        currentValues[i] = (float) spring.getCurrentValue();
        currentAtRestStates[i] = spring.isAtRest();
        spring.addListener(listener);
      }

      this.updatedValues.clear();
      this.updatedAtRestStates.clear();
      this.wasAtRest = isAtRest();
    }

    /**
     * Removes the listener from each individual spring.
     */
    private void stop() {
      for (int i = 0, count = compositeReboundSpring.springs.length; i < count; i++) {
        Spring spring = compositeReboundSpring.springs[i];
        spring.removeListener(listener);
      }
      handler.removeCallbacks(processBatch);
    }

    private boolean isAtRest() {
      for (boolean atRest : currentAtRestStates) {
        if (!atRest) {
          return false;
        }
      }
      return true;
    }

    private void schedule() {
      handler.removeCallbacks(processBatch);
      handler.post(processBatch);
    }

    private final SpringListener listener = new SimpleSpringListener() {

      @Override
      public void onSpringUpdate(Spring spring) {
        updatedValues.put(spring, (float) spring.getCurrentValue());
        schedule();
      }

      @Override
      public void onSpringAtRest(Spring spring) {
        updatedAtRestStates.put(spring, true);
        schedule();
      }

      @Override
      public void onSpringActivate(Spring spring) {
        updatedAtRestStates.put(spring, false);
        schedule();
      }
    };

    private final Runnable processBatch = new Runnable() {
      @Override
      public void run() {
        processUpdates();

        boolean isAtRest = isAtRest();

        if (!isAtRest && wasAtRest) {
          compositeReboundSpring.onCompositeSpringActivate();
        }

        if (!updatedValues.isEmpty()) {
          compositeReboundSpring.onCompositeSpringUpdate();
        }

        if (isAtRest && !wasAtRest) {
          compositeReboundSpring.onCompositeSpringAtRest();
        }

        wasAtRest = isAtRest;
        updatedValues.clear();
        updatedAtRestStates.clear();
      }
    };

    private void processUpdates() {
      for (int i = 0, count = compositeReboundSpring.springs.length; i < count; i++) {
        Spring spring = compositeReboundSpring.springs[i];

        if (updatedValues.containsKey(spring)) {
          currentValues[i] = updatedValues.get(spring);
        }

        if (updatedAtRestStates.containsKey(spring)) {
          currentAtRestStates[i] = updatedAtRestStates.get(spring);
        }
      }
    }
  }

  /**
   * A listener for the composite spring.
   */
  public interface CompositeSpringListener {

    /**
     * All individual springs were at rest, and now some are active.
     */
    void onCompositeSpringActivate();

    /**
     * Some individual springs have updated.
     */
    void onCompositeSpringUpdate(float[] values);

    /**
     * Some individual springs were active, and now all are at rest.
     */
    void onCompositeSpringAtRest();
  }
}
