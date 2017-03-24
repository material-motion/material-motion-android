package com.google.android.reactive.motion.sample;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.reactive.motion.MotionRuntime;
import com.google.android.reactive.motion.interactions.Tween;

public class TweenActivity extends AppCompatActivity {

  private final MotionRuntime runtime = new MotionRuntime();

  private View target;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tossable_tap_activity);

    target = findViewById(R.id.target);

    target.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        v.removeOnLayoutChangeListener(this);
        runDemo();
      }
    });
  }

  private void runDemo() {
    final ValueAnimator animator = ValueAnimator.ofFloat(1f);
    animator.setDuration(1000);
    final Tween<View, Float> tween = new Tween<>(View.ALPHA, animator);

    target.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
          case MotionEvent.ACTION_DOWN:
            animator.setFloatValues(1f, 0f);
            tween.animator.write(animator);
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            animator.setFloatValues(0f, 1f);
            tween.animator.write(animator);
            break;
        }

        return true;
      }
    });

    runtime.addInteraction(tween, target);
  }
}
