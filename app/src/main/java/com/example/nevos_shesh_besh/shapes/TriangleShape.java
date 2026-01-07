package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.Log;

import com.example.nevos_shesh_besh.R;
import com.example.nevos_shesh_besh.shapes.BaseShape;

public class TriangleShape extends BaseShape {
    private final float width;

    private final float height;

    private boolean isUpSideDown;

    private int circlesCount;

    private final Path path;

    private static final String TAG = "TriangleShape";
    
    public TriangleShape(float x, float y, float width, float height, boolean isUpSideDown, int color, int circlesCount) {
        super(x, y, color);
        this.width = width;
        this.height = height;
        this.path = new Path();
        this.isUpSideDown = isUpSideDown;
        this.circlesCount = circlesCount;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
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

        if(circlesCount > 0)
        {
            int count = circlesCount;
            int color = Color.BLUE;
            int activeColor =  Color.MAGENTA;

            boolean drawActiveCircle = false;

            if(count >= 1000)
            {
                count -= 1000;
                drawActiveCircle = true;
            }

            if(count > 100)
            {
                count-=100;
                color = Color.WHITE;
                activeColor = Color.YELLOW;
            }


            float drawRadius = (float) 0.4 * width / 2;

            float drawX = this.getX();
            float drawY;



            Log.d(TAG, "moveToTriangle: drawing circle. count: " + circlesCount + ", x: " + x + ", y: " + y);

            for (int i = 0; i < count; i++)
            {
                if(this.getIsUpSideDown())
                {
                    drawY = this.getY() + drawRadius + 2*drawRadius*i;
                }
                else
                {
                    drawY = this.getY() - drawRadius - 2*drawRadius*i;
                }

                if(drawActiveCircle && i == count - 1)
                    color = activeColor;

                CircleShape circle = new CircleShape(drawRadius, color, drawX, drawY);

                circle.draw(canvas);
            }
        }

    }

    public void draw(Canvas canvas, int circlesCount)
    {
        this.circlesCount = circlesCount;
        draw(canvas);
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
