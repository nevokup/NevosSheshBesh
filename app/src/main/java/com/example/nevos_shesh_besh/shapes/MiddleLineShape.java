package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class MiddleLineShape {
    private RectF rect;
    private Paint paint;

    public MiddleLineShape(int screenWidth, int screenHeight, int numberOfTriangles) {
        float sectionWidth = (float) screenWidth / (numberOfTriangles + 1);
        float middleLineWidth = sectionWidth;
        float left = (screenWidth / 2f) - (middleLineWidth / 2f);
        float top = 0;
        float right = (screenWidth / 2f) + (middleLineWidth / 2f);
        float bottom = screenHeight;
        rect = new RectF(left, top, right, bottom);

        paint = new Paint();
        paint.setColor(Color.GREEN);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }

    public RectF getRect() {
        return rect;
    }
}
