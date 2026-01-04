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
import com.example.nevos_shesh_besh.R;
import com.example.nevos_shesh_besh.model.Game;
import com.example.nevos_shesh_besh.shapes.CircleShape;
import com.example.nevos_shesh_besh.shapes.TriangleShape;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private final List<TriangleShape> triangles = new ArrayList<>();

    private  final  List<CircleShape> circles = new ArrayList<>();
    private CircleShape selectedCircle;

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

        selectedCircle = null;

        this.game = game;

        Log.d(TAG, "CustomSurfaceView: done");
    }

    private void initShapes() {

        Log.d(TAG, "initShapes: start");


        initBoardTriangles();

        //initBoardCircle();


        // 3 Rectangles
        //shapes.add(new RectShape(200, 200, 200, 150, Color.RED));
        //shapes.add(new RectShape(500, 200, 150, 150, Color.BLUE));
        //shapes.add(new RectShape(800, 200, 100, 250, Color.GREEN));

        // 1 Normal Circle
        //shapes.add(new CircleShape(300, 600, 80, Color.MAGENTA));

        // 1 Special Teleport Circle
        //specialCircle = new TeleportCircle(700, 600, 100, Color.CYAN, Color.WHITE);
        //shapes.add(specialCircle);


        Log.d(TAG, "initShapes: done");
    }

    private void initBoardTriangles()
    {
        Log.d(TAG, "initBoardTriangles: start");

        float sectionWidth = (float) screenWidth / numberOfTriangles;

        float drawWidth = (float) 0.9 * sectionWidth;

        float drawHeight = (float) 0.45 * screenHeight;

        float drawY = screenHeight - 10;

        int color = Color.BLUE;

        for (int i = 0; i < numberOfTriangles; i++) {

            // חישוב נקודת המרכז של המשבצת הנוכחית
            float drawX = (i * sectionWidth) + (sectionWidth / 2);

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

            Log.d(TAG, String.format("initBoardTriangles: drawing triangle at: (%f,%f). width: %f, height: %f", drawX, drawY, drawWidth, drawHeight));

            if(i %2 == 0)
                 color = Color.BLACK;
            else
                color = Color.RED;
            triangles.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, true,color,game.getBoard()[i]+numberOfTriangles));

        }

    }


    private void initBoardCircle(){

        float sectionWidth = (float) screenWidth / numberOfTriangles;

        float drawRadius = (float) 0.4 * sectionWidth / 2;


        int color = Color.BLUE;

        for (int i = 0; i < initPositionsP1.length; i++) {

            for (int j = 0; j < initPositionsP1[i]; j++) {
                // חישוב נקודת המרכז של המשבצת הנוכחית

                Log.d(TAG, String.format("initBoardCircle: drawing circle at triangle: %d", i));

                //circles.add(new CircleShape(drawRadius, color, triangles.get(i), R.color.Aqua));
            }
        }


         color = Color.WHITE;

        for (int i = 0; i < initPositionsP2 .length; i++) {

            for (int j = 0; j < initPositionsP2[i]; j++) {
                // חישוב נקודת המרכז של המשבצת הנוכחית

                Log.d(TAG, String.format("initBoardCircle: drawing circle at triangle: %d", i));

                //circles.add(new CircleShape(drawRadius, color, triangles.get(i), R.color.LightGrey));
            }
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


        //for (CircleShape circle : circles) {
        //    circle.draw(canvas);
        //}


    }

    private CircleShape getTopCircle(TriangleShape triangle) {
        CircleShape topCircle = null;
        for (CircleShape c : circles) {
            if (c.getTriangle() == triangle) {
                if (topCircle == null) {
                    topCircle = c;
                } else {
                    if (triangle.getIsUpSideDown()) {
                        if (c.getY() > topCircle.getY()) {
                            topCircle = c;
                        }
                    } else {
                        if (c.getY() < topCircle.getY()) {
                            topCircle = c;
                        }
                    }
                }
            }
        }
        return topCircle;
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
