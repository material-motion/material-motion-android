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
import android.support.v4.graphics.ColorUtils;

/**
 * A vectorizer for LAB colors.
 */
public class LabVectorizer extends TypeVectorizer<Integer> {

  private final double[] lab = new double[3];

  public LabVectorizer() {
    super(4);
  }

  @Override
  public void vectorize(Integer color, float[] vector) {
    vector[0] = Color.alpha(color);

    ColorUtils.colorToLAB(color, lab);
    vector[1] = (float) lab[0];
    vector[2] = (float) lab[1];
    vector[3] = (float) lab[2];
  }

  @Override
  public Integer compose(float[] vector) {
    int alpha = (int) constrain(vector[0], 0, 255);
    int color = ColorUtils.LABToColor(
      constrain(vector[1], 0, 100),
      constrain(vector[2], -128, 127),
      constrain(vector[3], -128, 127));
    return ColorUtils.setAlphaComponent(color, alpha);
  }

  private static double constrain(float amount, float low, float high) {
    return amount < low ? low : (amount > high ? high : amount);
  }
}
