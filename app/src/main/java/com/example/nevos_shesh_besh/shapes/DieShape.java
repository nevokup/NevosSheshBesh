package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class DieShape {
    private int number;
    private float x, y, size;
    private Paint paint;
    private Paint textPaint;

    public DieShape(int number, float x, float y, float size) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.size = size;

        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);

        this.textPaint = new Paint();
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextSize(size / 2);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas canvas) {
        RectF rect = new RectF(x, y, x + size, y + size);
        canvas.drawRect(rect, paint);

        float textX = x + size / 2;
        float textY = y + size / 2 - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(String.valueOf(number), textX, textY, textPaint);
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
