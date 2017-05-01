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
 * A vectorizer for single integer values.
 */
public class IntegerVectorizer extends TypeVectorizer<Integer> {

  public IntegerVectorizer() {
    super(1);
  }

  @Override
  protected void onVectorize(Integer value, float[] vector) {
    vector[0] = value;
  }

  @Override
  public Integer compose(float[] vector) {
    return (int) vector[0];
  }
}
