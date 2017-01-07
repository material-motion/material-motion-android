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

/**
 * A vectorizer transforms a T typed value to and from an equivalent float[] representation.
 */
public abstract class TypeVectorizer<T> {

  private final int vectorLength;
  private final float[] vector;

  /**
   * @param vectorLength The length of the vector that can represent a T typed value.
   */
  public TypeVectorizer(int vectorLength) {
    this.vectorLength = vectorLength;
    this.vector = new float[vectorLength];
  }

  public final int getVectorLength() {
    return vectorLength;
  }

  /**
   * Transforms a T typed value to an equivalent float[].
   */
  public final float[] vectorize(T value) {
    vectorize(value, vector);
    return vector;
  }

  /**
   * Transforms a T typed value to an equivalent float[].
   *
   * @param vector the vector should be written to this parameter.
   */
  public abstract void vectorize(T value, float[] vector);

  /**
   * Transforms a T typed value from the equivalent float[].
   */
  public abstract T compose(float[] vector);
}
