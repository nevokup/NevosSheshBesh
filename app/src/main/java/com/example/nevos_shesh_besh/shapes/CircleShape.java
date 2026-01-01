package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.util.Log;

public class CircleShape extends BaseShape {
    protected float radius;

    public boolean isActive = false;

    private final int originalColor;
    private final int activeColor;

    private TriangleShape triangle;

    private static final String TAG = "CircleShape";

    public CircleShape(float radius, int color, TriangleShape triangle, int activeColor) {
        super(triangle.getX(), triangle.getY(), color);

        this.radius = radius;

        this.triangle = null; // Initialize to null so moveToTriangle works correctly
        moveToTriangle(triangle);


        this.originalColor = color;
        this.activeColor = activeColor;
    }

    public void moveToTriangle(TriangleShape triangle)
    {
        if(this.triangle != null)
            this.triangle.removeCircle();

        this.triangle = triangle;

        if (triangle == null) {
            return;
        }

        int circlesCount = triangle.addCircle();

        float drawX = triangle.getX();
        float drawY;

        if(triangle.getIsUpSideDown())
        {
            drawY = triangle.getY() + radius + 2*radius*(circlesCount-1);
        }
        else
        {
            drawY = triangle.getY() - radius - 2*radius*(circlesCount-1);
        }

        Log.d(TAG, "moveToTriangle: drawing circle. count: " + circlesCount + ", x: " + x + ", y: " + y);

        this.x = drawX;
        this.y = drawY;

    }

    public TriangleShape getTriangle() {
        return triangle;
    }

    public float getY() {
        return y;
    }

    public int getColor() {
        return originalColor;
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

    public void setActive(boolean active) {
        this.isActive = active;
        this.paint.setColor(active ? activeColor : originalColor);
    }


}
