package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Path;
import android.util.Log;

import com.example.nevos_shesh_besh.shapes.BaseShape;

public class TriangleShape extends BaseShape {
    private final float width;

    private final float height;

    private boolean isUpSideDown;

    private int circlesCount;

    private final Path path;

    private static final String TAG = "TriangleShape";
    
    public TriangleShape(float x, float y, float width, float height, boolean isUpSideDown, int color) {
        super(x, y, color);
        this.width = width;
        this.height = height;
        this.path = new Path();
        this.isUpSideDown = isUpSideDown;
        this.circlesCount = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int addCircle() {
        circlesCount++;

        return circlesCount;
    }

    public void removeCircle() {
        circlesCount--;
    }

    public boolean getIsUpSideDown()
    {
        return isUpSideDown;
    }

    @Override
    public void draw(Canvas canvas) {
        path.reset();
        if (isUpSideDown == false)
        {
            path.moveTo(x-width/2, y);           // Top
            path.lineTo(x, y-height);    // Bottom Left
            path.lineTo(x + width/2, y);    // Bottom Right
            path.lineTo(x-width/2, y);           // Close
        }
        else {
            path.moveTo(x - width / 2, y);           // Top
            path.lineTo(x, y + height);    // Bottom Left
            path.lineTo(x + width / 2, y);    // Bottom Right
            path.lineTo(x - width / 2, y);           // Close
        }
        path.close();
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean isTouched(float touchX, float touchY) {

        Log.d(TAG, String.format("isTouched: touchX=%f, touchY=%f", touchX, touchY));
        Log.d(TAG, String.format("isTouched: x=%f, y=%f", x, y));

        if(!(touchX >= x - width/2 && touchX <= x + width/2))
            return false;

        if (isUpSideDown)
        {
            if(touchY >= y && touchY <= y + height)
                return true;
        }
        else
        {
            if(touchY <= y && touchY >= y - height)
                return true;
        }

        return false;
    }


}
