/*
 * Copyright 2017-present The Material Motion Authors. All Rights Reserved.
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
package com.google.android.material.motion.streams.springs;

import android.graphics.Color;

/**
 * A vectorizer for RGB colors.
 */
public class RgbVectorizer extends TypeVectorizer<Integer> {

  public RgbVectorizer() {
    super(4);
  }

  @Override
  public void vectorize(Integer color, float[] vector) {
    vector[0] = Color.alpha(color);
    vector[1] = Color.red(color);
    vector[2] = Color.green(color);
    vector[3] = Color.blue(color);
  }

  @Override
  public Integer compose(float[] vector) {
    return Color.argb(
      constrain(vector[0], 0, 255),
      constrain(vector[1], 0, 255),
      constrain(vector[2], 0, 255),
      constrain(vector[3], 0, 255));
  }

  private static int constrain(float amount, float low, float high) {
    return (int) (amount < low ? low : (amount > high ? high : amount));
  }
}
