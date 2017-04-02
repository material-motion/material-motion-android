/*
 * Copyright 2016-present The Material Motion Authors. All Rights Reserved.
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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.android.indefinite.observable.IndefiniteObservable.Connector;
import com.google.android.indefinite.observable.IndefiniteObservable.Disconnector;
import com.google.android.material.motion.MotionObservable;
import com.google.android.material.motion.MotionObserver;
import com.google.android.material.motion.MotionState;
import com.google.android.material.motion.gestures.GestureInteraction;
import com.google.android.material.motion.gestures.GestureRecognizer;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureRecognizerState;
import com.google.android.material.motion.gestures.GestureRecognizer.GestureStateChangeListener;

/**
 * A source for gestures.
 */
public final class GestureSource {

  @VisibleForTesting
  public GestureSource() {
    throw new UnsupportedOperationException();
  }

  /**
   * Creates a gesture source that will connect to the provided gesture interaction.
   */
  public static <GR extends GestureRecognizer> MotionObservable<GR> from(
    final GestureInteraction<GR, ?> interaction) {
    return new MotionObservable<>(new Connector<MotionObserver<GR>>() {
      @NonNull
      @Override
      public Disconnector connect(MotionObserver<GR> observer) {
        final GestureConnection connection = new GestureConnection<>(interaction, observer);
        return new Disconnector() {
          @Override
          public void disconnect() {
            connection.disconnect();
          }
        };
      }
    });
  }

  private static class GestureConnection<GR extends GestureRecognizer> {

    private final GestureInteraction<GR, ?> interaction;
    private final MotionObserver<GR> observer;
    @Nullable
    @MotionState
    private Integer lastPropagatedState = null;

    private GestureConnection(GestureInteraction<GR, ?> interaction, MotionObserver<GR> observer) {
      this.interaction = interaction;
      this.observer = observer;

      interaction.gestureRecognizer.addStateChangeListener(gestureStateChangeListener);

      propagate();
    }

    private void disconnect() {
      interaction.gestureRecognizer.removeStateChangeListener(gestureStateChangeListener);
    }

    private void propagate() {
      @GestureRecognizerState int state = interaction.gestureRecognizer.getState();
      boolean isActive = state == GestureRecognizer.BEGAN || state == GestureRecognizer.CHANGED;
      boolean wasActive =
        lastPropagatedState != null && lastPropagatedState == MotionState.ACTIVE;
      boolean wasAtRest =
        lastPropagatedState != null && lastPropagatedState == MotionState.AT_REST;

      if (isActive && !wasActive) {
        interaction.state.write(MotionState.ACTIVE);
        lastPropagatedState = MotionState.ACTIVE;
      }

      observer.next(interaction.gestureRecognizer);

      if (!isActive && !wasAtRest) {
        interaction.state.write(MotionState.AT_REST);
        lastPropagatedState = MotionState.AT_REST;
      }
    }

    private final GestureStateChangeListener gestureStateChangeListener =
      new GestureStateChangeListener() {
        @Override
        public void onStateChanged(GestureRecognizer gesture) {
          propagate();
        }
      };
  }
}
