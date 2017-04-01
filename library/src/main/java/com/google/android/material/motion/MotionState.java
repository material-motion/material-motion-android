package com.google.android.material.motion;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The possible states that a stream can be in.
 * <p>
 * What "active" means is stream-dependant. The stream is active if you can answer yes to any of
 * the following questions: <ul> <li>Is my animation currently animating?</li> <li>Is my
 * physical simulation still moving?</li> <li>Is my gesture recognizer in the .began or .changed
 * state?</li> </ul> Momentary events such as taps may emit {@link #ACTIVE} immediately followed
 * by {@link #AT_REST}.
 */
@IntDef({MotionState.AT_REST, MotionState.ACTIVE})
@Retention(RetentionPolicy.SOURCE)
public @interface MotionState {

  /**
   * The stream is at rest.
   */
  int AT_REST = 0;

  /**
   * The stream is currently active.
   */
  int ACTIVE = 1;
}
