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

import com.example.nevos_shesh_besh.model.Game;
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

    private int numberOfTriangles = 12;
    private Game game;

    private static final String TAG = "CustomSurfaceView";

    public CustomSurfaceView(Context context, Game game) {
        super(context);
        getHolder().addCallback(this);
        this.game = game;
    }

    private void initShapes() {
        initBoardTriangles();
        middleLine = new MiddleLineShape(screenWidth, screenHeight, numberOfTriangles);
        RectF middleRect = middleLine.getRect();

        float dieSize = middleRect.width() * 0.8f;
        float dieX = middleRect.centerX() - (dieSize / 2);

        float die1Y = screenHeight / 5f - dieSize / 2f;
        float die2Y = screenHeight * 4 / 5f - dieSize / 2f;

        die1 = new DieShape(game.dice[0], dieX, die1Y, dieSize);
        die2 = new DieShape(game.dice[1], dieX, die2Y, dieSize);
    }

    private void initBoardTriangles() {
        triangles.clear();
        float sectionWidth = (float) screenWidth / (numberOfTriangles + 1);
        float drawWidth = (float) 0.9 * sectionWidth;
        float drawHeight = (float) 0.45 * screenHeight;

        // חלק תחתון (משולשים 0-11)
        float drawY = screenHeight - 10;
        for (int i = 0; i < numberOfTriangles; i++) {
            float drawX = (i * sectionWidth) + (sectionWidth / 2);
            if (i >= numberOfTriangles / 2) drawX += sectionWidth;

            int color = (i % 2 == 0) ? Color.BLACK : Color.RED;
            triangles.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, false, color, game.board[i]));
        }

        // חלק עליון (משולשים 12-23)
        drawY = 0;
        for (int i = 0; i < numberOfTriangles; i++) {
            float drawX = screenWidth - ((i * sectionWidth) + (sectionWidth / 2));
            if (i >= numberOfTriangles / 2) drawX -= sectionWidth;

            int color = (i % 2 == 0) ? Color.BLACK : Color.RED;
            triangles.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, true, color, game.board[i + numberOfTriangles]));
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        initShapes();
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

        canvas.drawColor(Color.DKGRAY);

        // ציור המשולשים והחיילים שעליהם
        for (int i = 0; i < triangles.size(); i++) {
            // שים לב: אנחנו מעבירים לכל משולש את מספר החיילים העדכני מה-Game
            triangles.get(i).draw(canvas, game.board[i]);
        }

        if (middleLine != null) {
            middleLine.draw(canvas, game.p1EatenCount, game.p2EatenCount);
        }

        if (die1 != null && die2 != null) {
            die1.setNumber(game.dice[0]);
            die2.setNumber(game.dice[1]);
            die1.draw(canvas);
            die2.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (int i = 0; i < triangles.size(); i++) {
                if (triangles.get(i).isTouched(x, y)) {
                    // קריאה לפונקציית המהלך ב-Game
                    boolean moved = game.move(i);
                    Log.d(TAG, "Touched triangle " + i + ". Move success: " + moved);
                    return true;
                }
            }
        }
        return true;
    }
}