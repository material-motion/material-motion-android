package com.google.android.material.motion;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({SlopEvent.EXIT, SlopEvent.RETURN})
@Retention(RetentionPolicy.SOURCE)
public @interface SlopEvent {

  int EXIT = 0;
  int RETURN = 1;
}
