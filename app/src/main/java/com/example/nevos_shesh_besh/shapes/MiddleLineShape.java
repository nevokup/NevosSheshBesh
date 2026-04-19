package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class MiddleLineShape {
    private RectF rect;
    private Paint paint;
    private Paint p1Paint;
    private Paint p2Paint;
    private float screenWidth;

    public MiddleLineShape(int screenWidth, int screenHeight, int numberOfTriangles) {
        this.screenWidth = screenWidth;
        float sectionWidth = (float) screenWidth / (numberOfTriangles + 1);
        float middleLineWidth = sectionWidth;
        float left = (screenWidth / 2f) - (middleLineWidth / 2f);
        float top = 0;
        float right = (screenWidth / 2f) + (middleLineWidth / 2f);
        float bottom = screenHeight;
        rect = new RectF(left, top, right, bottom);

        paint = new Paint();
        paint.setColor(Color.rgb(60, 30, 10));

        p1Paint = new Paint();
        p1Paint.setColor(Color.WHITE);

        p2Paint = new Paint();
        p2Paint.setColor(Color.rgb(40, 40, 40));
    }

    public void draw(Canvas canvas, int p1EatenCount, int p2EatenCount) {
        canvas.drawRect(rect, paint);

        // חישוב רדיוס מדויק שתואם למשולשים (0.45 * 0.9 * רוחב_סקציה / 2)
        float triangleSectionWidth = screenWidth / 13f;
        float triangleWidth = 0.9f * triangleSectionWidth;
        float checkerRadius = 0.42f * triangleWidth / 2f; // הקטנה קלה לשיפור המראה

        for (int i = 0; i < p1EatenCount; i++) {
            float cx = rect.centerX();
            float cy = rect.top + checkerRadius + (i * 2.1f * checkerRadius) + 15;
            canvas.drawCircle(cx, cy, checkerRadius, p1Paint);
        }

        for (int i = 0; i < p2EatenCount; i++) {
            float cx = rect.centerX();
            float cy = rect.bottom - checkerRadius - (i * 2.1f * checkerRadius) - 15;
            canvas.drawCircle(cx, cy, checkerRadius, p2Paint);
        }
    }

    public RectF getRect() {
        return rect;
    }
}
