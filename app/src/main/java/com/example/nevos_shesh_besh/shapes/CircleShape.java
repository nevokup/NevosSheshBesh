package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.util.Log;

public class CircleShape extends BaseShape {
    protected float radius;

    public boolean isActive = false;


    private static final String TAG = "CircleShape";

    public CircleShape(float radius, int color, float x, float y) {
        super(x, y, color);

        this.radius = radius;

    }


    public float getY() {
        return y;
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    @Override
    public boolean isTouched(float touchX, float touchY) {

        double dx = touchX - x;
        double dy = touchY - y;

        Log.d(TAG, String.format("isTouched: touchX=%f, touchY=%f", touchX, touchY));
        Log.d(TAG, String.format("isTouched: x=%f, y=%f", x, y));
        Log.d(TAG, String.format("isTouched: x=%f, y=%f", dx, dy));

        return (dx * dx + dy * dy) <= (radius * radius);
    }


}
