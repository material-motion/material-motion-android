package com.google.android.material.motion.sample;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.material.motion.MotionRuntime;
import com.google.android.material.motion.ReactiveProperty;
import com.google.android.material.motion.interactions.MaterialSpring;
import com.google.android.material.motion.interactions.SetPositionOnTap;
import com.google.android.material.motion.interactions.Tossable;
import com.google.android.material.motion.properties.ViewProperties;
import com.google.android.material.motion.sources.DynamicSpringSource;
import com.google.android.material.motion.springs.FloatVectorizer;

public class TossableTapActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  private View container;
  private View target;
  private View destination;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tossable_tap_activity);

    container = findViewById(android.R.id.content);
    target = findViewById(R.id.target);
    destination = findViewById(R.id.destination);

    destination.setBackgroundDrawable(new CheckerboardDrawable());

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
    PointF anchor = runtime.get(target).center().read();
    ReactiveProperty<PointF> reactiveAnchor = ReactiveProperty.of(anchor);

    Tossable tossable = new Tossable(ViewProperties.CENTER, reactiveAnchor);
    runtime.addInteraction(tossable, target);

    runtime.addInteraction(new SetPositionOnTap(container), reactiveAnchor);

    runtime.write(reactiveAnchor.getStream(), runtime.get(destination).center());
  }
}
