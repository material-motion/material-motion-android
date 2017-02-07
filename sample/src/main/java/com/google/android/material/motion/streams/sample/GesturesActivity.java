package com.google.android.material.motion.streams.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.ReactiveProperty;
import com.google.android.material.motion.streams.interactions.DirectlyManipulable;
import com.google.android.material.motion.streams.properties.ViewProperties;

public class GesturesActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gestures_activity);

    final View target = findViewById(R.id.target);
    target.setBackgroundDrawable(new CheckerboardDrawable());

    DirectlyManipulable directlyManipulable = new DirectlyManipulable();
    runtime.addInteraction(directlyManipulable, target);

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ReactiveProperty<Float[]> initialTranslation =
          ReactiveProperty.of(target, ViewProperties.TRANSLATION);
        Float[] translation = initialTranslation.read();

        translation[0] /= 2f;
        translation[1] /= 2f;

        initialTranslation.write(translation);
      }
    });
  }
}
