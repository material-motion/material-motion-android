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
package com.google.android.material.motion.streams;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.material.motion.observable.IndefiniteObservable;
import com.google.android.material.motion.streams.MotionObservable.MotionObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MotionObservableTests {

  private static final float E = 0.0001f;

  private MotionObservable<Float> observable;

  @Before
  public void setUp() {
    observable = new MotionObservable<>(
      new IndefiniteObservable.Subscriber<MotionObserver<Float>>() {
        @Nullable
        @Override
        public IndefiniteObservable.Unsubscriber subscribe(MotionObserver<Float> observer) {
          observer.next(5f);
          observer.state(MotionObservable.ACTIVE);
          return null;
        }
      });
  }

  @Test
  public void mapByHalf() {
    observable.map(new MotionObservable.Transformation<Float, Float>() {
      @Override
      public Float transform(Float value) {
        return value / 2f; // Half.
      }
    }).subscribe(new MotionObserver<Float>() {
      @Override
      public void next(Float value) {
        assertThat(value).isWithin(E).of(2.5f);
      }

      @Override
      public void state(@MotionObservable.MotionState int state) {
        assertThat(state).isEqualTo(MotionObservable.ACTIVE);
      }
    });
  }

  @Test
  public void filterAll() {
    observable.filter(new MotionObservable.Predicate<Float>() {
      @Override
      public boolean evaluate(Float value) {
        return false; // All are filtered.
      }
    }).subscribe(new MotionObserver<Float>() {
      @Override
      public void next(Float value) {
        throw new AssertionError("Should never arrive here.");
      }

      @Override
      public void state(@MotionObservable.MotionState int state) {
        assertThat(state).isEqualTo(MotionObservable.ACTIVE);
      }
    }).unsubscribe();
  }

  @Test
  public void filterNone() {
    observable.filter(new MotionObservable.Predicate<Float>() {
      @Override
      public boolean evaluate(Float value) {
        return true; // None are filtered.
      }
    }).subscribe(new MotionObserver<Float>() {
      @Override
      public void next(Float value) {
        assertThat(value).isWithin(E).of(5f);
      }

      @Override
      public void state(@MotionObservable.MotionState int state) {
        assertThat(state).isEqualTo(MotionObservable.ACTIVE);
      }
    }).unsubscribe();
    ;
  }

  @Test
  public void writeUnscopedProperty() {
    View target = new View(Robolectric.setupActivity(Activity.class));
    target.setTranslationX(0);

    observable.write(target, View.TRANSLATION_X).subscribe(new MotionObserver<Float>() {
      @Override
      public void next(Float value) {
      }

      @Override
      public void state(@MotionObservable.MotionState int state) {
      }
    }).unsubscribe();
    ;

    assertThat(target.getTranslationX()).isWithin(E).of(5f);
  }

  @Test
  public void writeInlineProperty() {
    observable.write(new MotionObservable.ScopedWritable<Float>() {
      @Override
      public void write(Float value) {
        assertThat(value).isWithin(E).of(5f);
      }
    }).subscribe(new MotionObserver<Float>() {
      @Override
      public void next(Float value) {
      }

      @Override
      public void state(@MotionObservable.MotionState int state) {
      }
    }).unsubscribe();
  }

  @Test
  public void readInlineProperty() {
    MotionObservable.ScopedReadable<Float> reader = new MotionObservable.ScopedReadable<Float>() {
      @Override
      public Float read() {
        return 6f;
      }
    };

    assertThat(reader.read()).isWithin(E).of(6f);
  }
}
