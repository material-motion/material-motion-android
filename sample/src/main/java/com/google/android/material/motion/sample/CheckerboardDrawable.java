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
package com.google.android.material.motion.sample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Draws a checkerboard pattern.
 */
public class CheckerboardDrawable extends Drawable {
  public static final int COLS = 10;
  public static final int ROWS = 10;
  private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
  private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

  public CheckerboardDrawable() {
    gridPaint.setColor(Color.BLACK);
    backgroundPaint.setColor(Color.RED);
  }

  @Override
  public void draw(@NonNull Canvas canvas) {
    Rect bounds = getBounds();

    canvas.drawRect(bounds, backgroundPaint);

    float cellWidth = (float) bounds.width() / COLS;
    float cellHeight = (float) bounds.height() / ROWS;

    for (int i = 0, x = bounds.left; i < COLS + 1; i++, x += cellWidth) {
      if (i == 0 || i == COLS) {
        gridPaint.setStrokeWidth(10);
      } else {
        gridPaint.setStrokeWidth(1);
      }
      canvas.drawLine(x, bounds.top, x, bounds.bottom, gridPaint);
    }
    for (int i = 0, y = bounds.top; i < ROWS + 1; i++, y += cellHeight) {
      if (i == 0 || i == COLS) {
        gridPaint.setStrokeWidth(10);
      } else {
        gridPaint.setStrokeWidth(1);
      }
      canvas.drawLine(bounds.left, y, bounds.right, y, gridPaint);
    }
  }

  @Override
  public void setAlpha(int alpha) {
    gridPaint.setAlpha(alpha);
    invalidateSelf();
  }

  @Override
  public void setColorFilter(@Nullable ColorFilter colorFilter) {
    gridPaint.setColorFilter(colorFilter);
    invalidateSelf();
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }
}
