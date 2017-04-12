package com.google.android.material.motion.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.material.motion.MotionRuntime;
import com.google.android.material.motion.interactions.DirectlyManipulable;

public class GestureActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  private View target;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tossable_tap_activity);

    target = findViewById(R.id.target);

    target.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(
        View v,
        int left,
        int top,
        int right,
        int bottom,
        int oldLeft,
        int oldTop,
        int oldRight,
        int oldBottom) {
        v.removeOnLayoutChangeListener(this);
        runDemo();
      }
    });
  }

  private void runDemo() {
    runtime.addInteraction(new DirectlyManipulable(), target);
  }
}
