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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Property;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.motion.observable.IndefiniteObservable.Subscriber;
import com.google.android.material.motion.observable.IndefiniteObservable.Subscription;
import com.google.android.material.motion.observable.IndefiniteObservable.Unsubscriber;
import com.google.android.material.motion.streams.MotionObservable;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;
import com.google.android.material.motion.streams.MotionObservable.MotionState;

import static com.google.android.material.motion.streams.MotionObservable.ACTIVE;
import static com.google.android.material.motion.streams.MotionObservable.AT_REST;

/**
 * Streams for Android sample Activity.
 */
public class MainActivity extends AppCompatActivity {

  private static final String[] values = new String[]{
    "foo", "skip", "bar", "baz", "qux"
  };

  private MotionObserver<String> callback;
  private int index = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main_activity);

    final TextView text = (TextView) findViewById(R.id.text);
    Button nextButton = (Button) findViewById(R.id.next_button);
    Button unsubscribeButton = (Button) findViewById(R.id.unsubscribe_button);

    final MotionObservable<String> observable = new MotionObservable<>(
      new Subscriber<MotionObserver<String>>() {

        @Nullable
        @Override
        public Unsubscriber subscribe(MotionObserver<String> observer) {
          registerButtonCallback(observer);
          return new Unsubscriber() {

            @Override
            public void unsubscribe() {
              unregisterButtonCallback();
            }
          };
        }
      });

    MotionObservable<CharSequence> stream = observable
      .filter(new MotionObservable.Predicate<String>() {

        @Override
        public boolean evaluate(String value) {
          return value != "skip";
        }
      })
      .map(new MotionObservable.Transformation<String, CharSequence>() {

        @Override
        public CharSequence transform(String value) {
          return italicizeAndCapitalize(value);
        }
      })
      .write(text, TEXT_PROPERTY);

    final Subscription subscription = stream
      .subscribe(new MotionObserver<CharSequence>() {

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
