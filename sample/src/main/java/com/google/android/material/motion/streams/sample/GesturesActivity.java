package com.google.android.material.motion.streams.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.interactions.Draggable;
import com.google.android.material.motion.streams.interactions.Pinchable;
import com.google.android.material.motion.streams.interactions.Rotatable;

import static com.google.android.material.motion.streams.operators.FloatArrayOperators.lockToYAxis;

public class GesturesActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gestures_activity);

    View target = findViewById(R.id.target);

    Draggable draggable = new Draggable();
    draggable.compose(lockToYAxis(0f));

    runtime.addInteraction(draggable, target);
    runtime.addInteraction(new Pinchable(), target);
    runtime.addInteraction(new Rotatable(), target);
  }
}
