package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class MiddleLineShape {
    private float left, top, right, bottom;
    private Paint paint;

    public MiddleLineShape(int screenWidth, int screenHeight, int numberOfTriangles) {
        float sectionWidth = (float) screenWidth / (numberOfTriangles + 1);
        float middleLineWidth = sectionWidth;
        this.left = (screenWidth / 2f) - (middleLineWidth / 2f);
        this.top = 0;
        this.right = (screenWidth / 2f) + (middleLineWidth / 2f);
        this.bottom = screenHeight;

        this.paint = new Paint();
        this.paint.setColor(Color.GREEN);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(left, top, right, bottom, paint);
    }
}
