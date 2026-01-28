package com.example.nevos_shesh_besh.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
// Import shapes
import com.example.nevos_shesh_besh.R;
import com.example.nevos_shesh_besh.model.Game;
import com.example.nevos_shesh_besh.shapes.CircleShape;
import com.example.nevos_shesh_besh.shapes.DieShape;
import com.example.nevos_shesh_besh.shapes.MiddleLineShape;
import com.example.nevos_shesh_besh.shapes.TriangleShape;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private final List<TriangleShape> triangles = new ArrayList<>();

    private MiddleLineShape middleLine;
    private DieShape die1;
    private DieShape die2;

    int screenWidth;
    int screenHeight;

    private int numberOfTriangles;

    int[] initPositionsP1 = {0,0,0,0,3,0,5,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,5};
    int[] initPositionsP2 = {5,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,5,0,3,0,0,0,0};

    Game game;

    private static final String TAG = "CustomSurfaceView";

    public CustomSurfaceView(Context context, Game game) {
        super(context);
        Log.d(TAG, "CustomSurfaceView: start");

        getHolder().addCallback(this);

        numberOfTriangles = 12;

        this.game = game;

        Log.d(TAG, "CustomSurfaceView: done");
    }

    private void initShapes() {

        Log.d(TAG, "initShapes: start");


        initBoardTriangles();
        middleLine = new MiddleLineShape(screenWidth, screenHeight, numberOfTriangles);
        RectF middleRect = middleLine.getRect();

        float dieSize = middleRect.width() * 0.8f; // Make die smaller than the line width
        float dieX = middleRect.centerX() - (dieSize / 2);

        // Position dice vertically, one top, one bottom on the middle line area
        float die1Y = screenHeight / 5f - dieSize / 2f;
        float die2Y = screenHeight * 4 / 5f - dieSize / 2f;


        int[] diceValues = game.getDice();
        die1 = new DieShape(diceValues[0], dieX, die1Y, dieSize);
        die2 = new DieShape(diceValues[1], dieX, die2Y, dieSize);


        Log.d(TAG, "initShapes: done");
    }

    private void initBoardTriangles()
    {
        Log.d(TAG, "initBoardTriangles: start");

        // + 1 for the middle line
        float sectionWidth = (float) screenWidth / (numberOfTriangles + 1);

        float drawWidth = (float) 0.9 * sectionWidth;

        float drawHeight = (float) 0.45 * screenHeight;

        float drawY = screenHeight - 10;

        int color = Color.BLUE;

        for (int i = 0; i < numberOfTriangles; i++) {

            // חישוב נקודת המרכז של המשבצת הנוכחית
            float drawX = (i * sectionWidth) + (sectionWidth / 2);

            if(i >= numberOfTriangles / 2)
                drawX += sectionWidth;

            Log.d(TAG, String.format("initBoardTriangles: drawing triangle at: (%f,%f). width: %f, height: %f", drawX, drawY, drawWidth, drawHeight));

            if(i %2 == 0)
                color = Color.BLACK;
            else
                color = Color.RED;

            triangles.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, false, color, game.getBoard()[i]));
        }
        drawY =0;
        for (int i = 0; i < numberOfTriangles; i++){

            float drawX = screenWidth - ((i * sectionWidth) + (sectionWidth / 2));
            if(i >= numberOfTriangles / 2)
                drawX -= sectionWidth;

            Log.d(TAG, String.format("initBoardTriangles: drawing triangle at: (%f,%f). width: %f, height: %f", drawX, drawY, drawWidth, drawHeight));

            if(i %2 == 0)
                 color = Color.BLACK;
            else
                color = Color.RED;
            triangles.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, true,color,game.getBoard()[i]+numberOfTriangles));

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

        int i=0;
        for (TriangleShape triangle : triangles) {
            triangle.draw(canvas, game.getBoard()[i]);
            i++;
        }

        if(middleLine != null){
            middleLine.draw(canvas, game.getP1EatenCount(), game.getP2EatenCount());
        }

        if(die1 != null && die2 != null) {
            int[] diceValues = game.getDice();
            die1.setNumber(diceValues[0]);
            die2.setNumber(diceValues[1]);
            die1.draw(canvas);
            die2.draw(canvas);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        Log.d(TAG, "onTouchEvent: x: " +x + ", y: " + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Logic 1: Teleportation if active
                // Logic 2: Circle Hit detection
                for (int i = 0; i <  triangles.size(); i++) {
                    TriangleShape triangle = triangles.get(i);
                    if (triangle.isTouched(x, y)) {
                        game.move(i);
                        Log.d(TAG, "onTouchEvent: touched triangle: " + i);

                        return true;
                    }
                }

                // Logic 2: Hit detection

                break;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }


}
