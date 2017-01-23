package com.google.android.material.motion.streams.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.interactions.Draggable;
import com.google.android.material.motion.streams.interactions.GestureInteraction;
import com.google.android.material.motion.streams.interactions.Pinchable;
import com.google.android.material.motion.streams.interactions.Rotatable;

public class GesturesActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gestures_activity);

    View target = findViewById(R.id.target);
    runtime.addInteraction(new Draggable(), target);
    runtime.addInteraction(new Pinchable(), target);
    runtime.addInteraction(new Rotatable(), target);
  }
}
