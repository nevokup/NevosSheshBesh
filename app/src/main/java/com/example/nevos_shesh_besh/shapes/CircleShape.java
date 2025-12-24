package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
public class CircleShape extends BaseShape {
    protected float radius;

    private TriangleShape triangle;

    public CircleShape(float radius, int color, TriangleShape triangle) {
        super(triangle.getX(), triangle.getY(), color);

        float drawY;
        int circlesCount = triangle.addCircle();
        if(triangle.getIsUpSideDown())
        {
            drawY = y + radius + 2*radius*(circlesCount-1);
        }
        else
        {
            drawY = y - radius - 2*radius*(circlesCount-1);
        }

        this.y = drawY;

        this.radius = radius;
        this.triangle = triangle;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    @Override
    public boolean isTouched(float touchX, float touchY) {
        double dx = touchX - x;
        double dy = touchY - y;
        return (dx * dx + dy * dy) <= (radius * radius);
    }


}

