package com.google.android.material.motion.streams.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.ReactiveProperty;
import com.google.android.material.motion.streams.interactions.Tap;
import com.google.android.material.motion.streams.interactions.Tossable;
import com.google.android.material.motion.streams.properties.ViewProperties;

public class GesturesActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  private View container;
  private View target;
  private View destination;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gestures_activity);

    container = findViewById(android.R.id.content);
    target = findViewById(R.id.target);
    destination = findViewById(R.id.destination);

    destination.setBackgroundDrawable(new CheckerboardDrawable());

    target.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        v.removeOnLayoutChangeListener(this);
        springDemo();
      }
    });
  }

  private void springDemo() {
    ReactiveProperty<Float[]> anchor = ReactiveProperty.of(ViewProperties.POSITION.get(target));

    Tossable tossable = new Tossable(anchor);
    runtime.addInteraction(tossable, target);

    Tap tap = new Tap(container);
    runtime.addInteraction(tap, tossable.anchor);

    runtime.write(tossable.anchor.getStream(), ReactiveProperty.of(destination, ViewProperties.POSITION));
  }
}
