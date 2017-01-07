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
 * A meta spring is made of multiple rebound springs. The meta spring manages the aggregate state of
 * each individual spring, and reports aggregate state changes to its listeners.
 */
public final class MetaSpring {

  private final Spring[] springs;
  private final SpringTracker tracker;

  private final List<MetaSpringListener> listeners = new CopyOnWriteArrayList<>();

  /**
   * Create a new meta spring to track the given individual springs.
   */
  public MetaSpring(Spring[] springs) {
    this.springs = springs;
    this.tracker = new SpringTracker(this);
  }

  /**
   * Adds a listener to the meta spring. The first listener to be added will add a private
   * listener to each individual spring.
   */
  public void addListener(MetaSpringListener listener) {
    if (listeners.isEmpty()) {
      tracker.start();
    }

    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Removes a listener from the meta spring. The last listener to be removed will remove the
   * private listener to each individual spring.
   */
  public void removeListener(MetaSpringListener listener) {
    listeners.remove(listener);

    if (listeners.isEmpty()) {
      tracker.stop();
    }
  }

  /**
   * Returns whether the aggregate state of the meta spring is at rest.
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

  private void onMetaSpringActivate() {
    for (MetaSpringListener listener : listeners) {
      listener.onMetaSpringActivate();
    }
  }

  private void onMetaSpringUpdate() {
    for (MetaSpringListener listener : listeners) {
      listener.onMetaSpringUpdate(tracker.currentValues);
    }
  }

  private void onSpringAtRest() {
    for (MetaSpringListener listener : listeners) {
      listener.onMetaSpringAtRest();
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

    private final MetaSpring metaSpring;

    private boolean wasAtRest;

    private final float[] currentValues;
    private final SimpleArrayMap<Spring, Float> updatedValues = new SimpleArrayMap<>();

    private final boolean[] currentAtRestStates;
    private final SimpleArrayMap<Spring, Boolean> updatedAtRestStates = new SimpleArrayMap<>();

    private final Handler handler = new Handler();

    public SpringTracker(MetaSpring metaSpring) {
      this.metaSpring = metaSpring;

      this.currentValues = new float[metaSpring.springs.length];
      this.currentAtRestStates = new boolean[metaSpring.springs.length];
    }

    /**
     * Adds the listener to each individual spring.
     */
    private void start() {
      for (int i = 0, count = metaSpring.springs.length; i < count; i++) {
        Spring spring = metaSpring.springs[i];

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
      for (int i = 0, count = metaSpring.springs.length; i < count; i++) {
        Spring spring = metaSpring.springs[i];
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
          metaSpring.onMetaSpringActivate();
        }

        if (!updatedValues.isEmpty()) {
          metaSpring.onMetaSpringUpdate();
        }

        if (isAtRest && !wasAtRest) {
          metaSpring.onSpringAtRest();
        }

        wasAtRest = isAtRest;
        updatedValues.clear();
        updatedAtRestStates.clear();
      }
    };

    private void processUpdates() {
      for (int i = 0, count = metaSpring.springs.length; i < count; i++) {
        Spring spring = metaSpring.springs[i];

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
   * A listener for the meta spring.
   */
  public interface MetaSpringListener {

    /**
     * All individual springs were at rest, and now some are active.
     */
    void onMetaSpringActivate();

    /**
     * Some individual springs have updated.
     */
    void onMetaSpringUpdate(float[] values);

    /**
     * Some individual springs were active, and now all are at rest.
     */
    void onMetaSpringAtRest();
  }
}
