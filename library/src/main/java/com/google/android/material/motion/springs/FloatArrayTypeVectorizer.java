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

public class FloatArrayTypeVectorizer extends TypeVectorizer<Float[]> {

  private final Float[] array;

  public FloatArrayTypeVectorizer(int arrayLength) {
    super(arrayLength);
    array = new Float[arrayLength];
  }

  @Override
  protected void onVectorize(Float[] value, float[] vector) {
    for (int i = 0; i < value.length; i++) {
      vector[i] = value[i];
    }
  }

  @Override
  public Float[] compose(float[] vector) {
    for (int i = 0; i < vector.length; i++) {
      array[i] = vector[i];
    }
    return array;
  }
}
