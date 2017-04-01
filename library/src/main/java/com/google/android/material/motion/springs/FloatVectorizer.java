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

/**
 * A vectorizer for single floating point values.
 */
public class FloatVectorizer extends TypeVectorizer<Float> {

  public FloatVectorizer() {
    super(1);
  }

  @Override
  public void onVectorize(Float value, float[] vector) {
    vector[0] = value;
  }

  @Override
  public Float compose(float[] vector) {
    return vector[0];
  }
}
