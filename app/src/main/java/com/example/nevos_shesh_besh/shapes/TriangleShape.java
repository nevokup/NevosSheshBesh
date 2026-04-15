package com.example.nevos_shesh_besh.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
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
    
    public TriangleShape(float x, float y, float width, float height, boolean isUpSideDown, int color, int circlesCount) {
        super(x, y, color);
        this.width = width;
        this.height = height;
        this.path = new Path();
        this.isUpSideDown = isUpSideDown;
        this.circlesCount = circlesCount;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public boolean getIsUpSideDown() { return isUpSideDown; }

    @Override
    public void draw(Canvas canvas) {
        path.reset();
        if (!isUpSideDown) {
            path.moveTo(x - width / 2, y);
            path.lineTo(x, y - height);
            path.lineTo(x + width / 2, y);
            path.lineTo(x - width / 2, y);
        } else {
            path.moveTo(x - width / 2, y);
            path.lineTo(x, y + height);
            path.lineTo(x + width / 2, y);
            path.lineTo(x - width / 2, y);
        }
        path.close();
        canvas.drawPath(path, paint);

        if (circlesCount > 0) {
            int count = circlesCount;
            int color = Color.WHITE; // שחקן 1 - לבן
            int activeColor = Color.CYAN; // צבע סימון חייל נבחר

            boolean drawActiveCircle = false;
            if (count >= 1000) {
                count -= 1000;
                drawActiveCircle = true;
            }

            if (count >= 100) {
                count -= 100;
                color = Color.rgb(40, 40, 40); // שחקן 2 - שחור/אפור כהה מאוד
                activeColor = Color.YELLOW;
            }

            float drawRadius = (float) 0.45 * width / 2;
            float drawX = this.getX();
            float drawY;

            for (int i = 0; i < count; i++) {
                if (this.getIsUpSideDown()) {
                    drawY = this.getY() + drawRadius + 2 * drawRadius * i + 5;
                } else {
                    drawY = this.getY() - drawRadius - 2 * drawRadius * i - 5;
                }

                int finalColor = (drawActiveCircle && i == count - 1) ? activeColor : color;
                CircleShape circle = new CircleShape(drawRadius, finalColor, drawX, drawY);
                circle.draw(canvas);
            }
        }
    }

    public void draw(Canvas canvas, int circlesCount) {
        this.circlesCount = circlesCount;
        draw(canvas);
    }

    @Override
    public boolean isTouched(float touchX, float touchY) {
        if (!(touchX >= x - width / 2 && touchX <= x + width / 2)) return false;
        if (isUpSideDown) {
            return touchY >= y && touchY <= y + height;
        } else {
            return touchY <= y && touchY >= y - height;
        }
    }
}
