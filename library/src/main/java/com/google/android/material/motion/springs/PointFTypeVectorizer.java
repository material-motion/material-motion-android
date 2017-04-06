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
package com.google.android.material.motion.springs;

import android.graphics.PointF;

public class PointFTypeVectorizer extends TypeVectorizer<PointF> {

  private final PointF pointF;

  public PointFTypeVectorizer() {
    super(2);
    pointF = new PointF();
  }

  @Override
  public void onVectorize(PointF value, float[] vector) {
    vector[0] = value.x;
    vector[1] = value.y;
  }

  @Override
  public PointF compose(float[] vector) {
    pointF.x = vector[0];
    pointF.y = vector[1];
    return pointF;
  }
}
