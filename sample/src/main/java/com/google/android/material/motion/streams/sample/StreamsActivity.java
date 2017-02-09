/*
 * Copyright 2016-present The Material Motion Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.motion.streams.sample;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.motion.gestures.DragGestureRecognizer;
import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.FilterOperation;
import com.google.android.material.motion.streams.MotionObservable.MapOperation;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.MotionState;
import com.google.android.material.motion.streams.MotionRuntime;
import com.google.android.material.motion.streams.ReactiveWritable;
import com.google.android.material.motion.streams.properties.ViewProperties;
import com.google.android.material.motion.streams.sources.GestureSource;
import com.google.android.material.motion.streams.springs.LabVectorizer;
import com.google.android.material.motion.streams.springs.MaterialSpring;
import com.google.android.material.motion.streams.springs.RgbVectorizer;

import java.util.Locale;

import static com.google.android.material.motion.streams.MotionObservable.ACTIVE;
import static com.google.android.material.motion.streams.MotionObservable.AT_REST;
import static com.google.android.material.motion.streams.operators.GestureOperators.centroid;

/**
 * Streams for Android sample Activity.
 */
public class StreamsActivity extends AppCompatActivity {

  private static final String[] values = new String[]{
    "foo", "skip", "bar", "baz", "qux"
  };

  private final MotionRuntime runtime = new MotionRuntime();

  private TextView text;
  private Button nextButton;
  private Button unsubscribeButton;

  private View dragTarget;

  private View springTarget;

  private MotionObserver<String> callback;
  private int index = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.streams_activity);

    text = (TextView) findViewById(R.id.text);
    nextButton = (Button) findViewById(R.id.next_button);
    unsubscribeButton = (Button) findViewById(R.id.unsubscribe_button);

    dragTarget = findViewById(R.id.drag_target);

    springTarget = findViewById(R.id.spring_target);
    runDemo1();
    runDemo2();
    runDemo3();
  }

  private void runDemo1() {
    final MotionObservable<String> observable = new MotionObservable<>(
      new IndefiniteObservable.Connector<MotionObserver<String>>() {

        @NonNull
        @Override
        public IndefiniteObservable.Disconnector connect(MotionObserver<String> observer) {
          registerButtonCallback(observer);
          return new IndefiniteObservable.Disconnector() {

            @Override
            public void disconnect() {
              unregisterButtonCallback();
            }
          };
        }
      });

    MotionObservable<CharSequence> stream = observable
      .compose(new FilterOperation<String>() {

        @Override
        public boolean filter(String value) {
          return !"skip".equals(value);
        }
      })
      .compose(new MapOperation<String, CharSequence>() {

        @Override
        public CharSequence transform(String value) {
          return italicizeAndCapitalize(value);
        }
      });

//    runtime.write(stream, text, TEXT_PROPERTY);

    final Subscription subscription = stream.subscribe(
      new MotionObserver<CharSequence>() {

        @Override
        public void next(CharSequence value) {
          // No-op. Handled by write() above.
        }

        @Override
        public void state(@MotionState int state) {
          switch (state) {
            case AT_REST:
              text.setTextColor(Color.BLACK);
              break;
            case ACTIVE:
              text.setTextColor(Color.RED);
              break;
          }
        }
      });

    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (callback != null) {
          if (index < values.length) {
            callback.next(values[index]);
          }

          if (index + 1 < values.length) {
            callback.state(ACTIVE);
          } else {
            callback.state(AT_REST);
          }

          index++;
        }
      }
    });

    unsubscribeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        subscription.unsubscribe();
      }
    });
  }

  private void runDemo2() {
    DragGestureRecognizer gesture = new DragGestureRecognizer();
    dragTarget.setOnTouchListener(gesture);

    MotionObservable<PointF> observable =
      GestureSource
        .from(gesture)
        .compose(centroid());

//    runtime.write(observable, new ReactiveWritable<PointF>() {
//      @Override
//      public void write(PointF value) {
//        text.setText(String.format(Locale.getDefault(), "[%f, %f]", value.x, value.y));
//      }
//    });
  }

  private void runDemo3() {
    final MaterialSpring<View, Integer> rgbSpring = new MaterialSpring<>(
      ViewProperties.BACKGROUND_COLOR,
      new RgbVectorizer(),
      Color.RED,
      Color.RED,
      0,
      0.01f,
      1,
      10);
    final MaterialSpring<View, Integer> labSpring = new MaterialSpring<>(
      ViewProperties.BACKGROUND_COLOR,
      new LabVectorizer(),
      Color.RED,
      Color.RED,
      0,
      0.01f,
      1,
      10);

    springTarget.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = MotionEventCompat.getActionMasked(motionEvent);

        switch (action) {
          case MotionEvent.ACTION_DOWN:
            rgbSpring.destination.write(Color.GREEN);
            labSpring.destination.write(Color.GREEN);
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            rgbSpring.destination.write(Color.RED);
            labSpring.destination.write(Color.RED);
            break;
        }

        return true;
      }
    });

    runtime.addInteraction(rgbSpring, springTarget);
    runtime.addInteraction(labSpring, dragTarget);
  }

  private CharSequence italicizeAndCapitalize(String value) {
    Spannable spannable = new SpannableString(value.toUpperCase());
    spannable.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannable.length(), 0);
    return spannable;
  }

  private void registerButtonCallback(MotionObserver<String> observer) {
    callback = observer;
  }

  private void unregisterButtonCallback() {
    callback = null;
  }

  private static Property<TextView, CharSequence> TEXT_PROPERTY = new Property<TextView, CharSequence>(CharSequence.class, "text") {

    @Override
    public CharSequence get(TextView object) {
      return object.getText();
    }

    @Override
    public void set(TextView object, CharSequence value) {
      object.setText(value);
    }
  };
}
