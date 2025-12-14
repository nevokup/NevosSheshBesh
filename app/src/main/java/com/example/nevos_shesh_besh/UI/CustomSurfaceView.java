package com.example.nevos_shesh_besh.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
// Import shapes
import com.example.nevos_shesh_besh.shapes.*;
import com.example.nevos_shesh_besh.shapes.BaseShape;
import com.example.nevos_shesh_besh.shapes.CircleShape;
import com.example.nevos_shesh_besh.shapes.RectShape;
import com.example.nevos_shesh_besh.shapes.TeleportCircle;
import com.example.nevos_shesh_besh.shapes.TriangleShape;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private final List<BaseShape> shapes = new ArrayList<>();
    private BaseShape selectedShape = null;
    private TeleportCircle specialCircle;

    int screenWidth;
    int screenHeight;

    private static final String TAG = "CustomSurfaceView";

    public CustomSurfaceView(Context context) {
        super(context);
        Log.d(TAG, "CustomSurfaceView: start");
        
        getHolder().addCallback(this);

        Log.d(TAG, "CustomSurfaceView: done");
    }

    private void initShapes() {

        Log.d(TAG, "initShapes: start");


        initBoardTriangles();

        // 3 Rectangles
        shapes.add(new RectShape(200, 200, 200, 150, Color.RED));
        shapes.add(new RectShape(500, 200, 150, 150, Color.BLUE));
        shapes.add(new RectShape(800, 200, 100, 250, Color.GREEN));

        // 1 Normal Circle
        shapes.add(new CircleShape(300, 600, 80, Color.MAGENTA));

        // 1 Special Teleport Circle
        specialCircle = new TeleportCircle(700, 600, 100, Color.CYAN, Color.WHITE);
        shapes.add(specialCircle);


        Log.d(TAG, "initShapes: done");
    }

    private void initBoardTriangles()
    {
        Log.d(TAG, "initBoardTriangles: start");

        int numberOfTriangles = 12;

        float sectionWidth = (float) screenWidth / numberOfTriangles;

        float drawWidth = (float) 0.9 * sectionWidth;

        float drawHeight = (float) 0.45 * screenHeight;

        float drawY = screenHeight - 10;

        for (int i = 0; i < numberOfTriangles; i++) {

            // חישוב נקודת המרכז של המשבצת הנוכחית
            float drawX = (i * sectionWidth) + (sectionWidth / 2);

            Log.d(TAG, String.format("initBoardTriangles: drawing triangle at: (%f,%f). width: %f, height: %f", drawX, drawY, drawWidth, drawHeight));

            if (i%2 == 0)
            {
                int color = Color.RED;
            }
            else
            {
                int color = Color.BLACK ;
            }

            shapes.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, false, color));
        }

        for (int i = 0; i < numberOfTriangles; i++){

            float drawX = (i * sectionWidth) + (sectionWidth / 2);

            Log.d(TAG, String.format("initBoardTriangles: drawing triangle at: (%f,%f). width: %f, height: %f", drawX, drawY, drawWidth, drawHeight));

            if (i%2 == 0)
            {
                int color = Color.BLACK;
            }
            else
            {
                int color = Color.RED;
            }

            shapes.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, false, color));

        }



    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: start");
        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
        gameThread.start();

        Log.d(TAG, "surfaceCreated: done");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
    {
        Log.d(TAG, "surfaceChanged: start");
        screenWidth = width;
        screenHeight = height;

        //TODO - move to the right place
        initShapes();

        Log.d(TAG, "surfaceChanged: done");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        gameThread.setRunning(false);
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;

        // Background color
        canvas.drawColor(Color.DKGRAY);

        for (BaseShape shape : shapes) {
            shape.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Logic 1: Teleportation if active
                if (specialCircle.isActive) {
                    specialCircle.setPosition(x, y);
                    specialCircle.setActive(false);
                    return true;
                }

                // Logic 2: Hit detection
                for (int i = shapes.size() - 1; i >= 0; i--) {
                    BaseShape shape = shapes.get(i);
                    if (shape.isTouched(x, y)) {
                        if (shape == specialCircle) {
                            specialCircle.setActive(true);
                        } else {
                            selectedShape = shape;
                            // Bring to front
                            shapes.remove(i);
                            shapes.add(shape);
                        }
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (selectedShape != null) {
                    selectedShape.setPosition(x, y);
                }
                break;

            case MotionEvent.ACTION_UP:
                selectedShape = null;
                break;
        }
        return true;
    }


}

