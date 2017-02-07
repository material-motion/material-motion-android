package com.google.android.material.motion.streams.sample;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.ReactiveProperties;
import com.google.android.material.motion.streams.ReactiveProperty;
import com.google.android.material.motion.streams.interactions.DirectlyManipulable;
import com.google.android.material.motion.streams.properties.ViewProperties;

import static com.google.android.material.motion.streams.operators.FloatArrayOperators.lockToYAxis;
import static com.google.android.material.motion.streams.operators.FloatArrayOperators.rubberBanded;

public class GesturesActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gestures_activity);

    final View target = findViewById(R.id.target);
    target.setBackgroundDrawable(new CheckerboardDrawable());

    DirectlyManipulable directlyManipulable = new DirectlyManipulable();

    directlyManipulable.draggable
      .constrain(lockToYAxis(0f))
      .constrain(rubberBanded(new RectF(-500f, -500f, 500f, 500f), 200f));

    runtime.addInteraction(directlyManipulable, target);

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ReactiveProperty<Float[]> initialTranslation =
          ReactiveProperties.of(target, ViewProperties.TRANSLATION);
        Float[] translation = initialTranslation.read();

        translation[0] /= 2f;
        translation[1] /= 2f;

        initialTranslation.write(translation);
      }
    });
  }
}
