package com.example.nevos_shesh_besh.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
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

        // צבעים מעודכנים לפי הבקשה:
        // משולשים כהים הפכו לטיפה יותר בהירים (110 במקום 84)
        int colorDarkWood = Color.rgb(110, 50, 15); 
        int colorLightWood = Color.rgb(186, 92, 28);

        // חלק תחתון (0-11)
        float drawY = screenHeight - 10;
        for (int i = 0; i < numberOfTriangles; i++) {
            float drawX = (i * sectionWidth) + (sectionWidth / 2);
            if (i >= numberOfTriangles / 2) drawX += sectionWidth;
            int color = (i % 2 == 0) ? colorDarkWood : colorLightWood;
            triangles.add(new TriangleShape(drawX, drawY, drawWidth, drawHeight, false, color, game.board[i]));
        }

        // חלק עליון (12-23)
        drawY = 0;
        for (int i = 0; i < numberOfTriangles; i++) {
            float drawX = screenWidth - ((i * sectionWidth) + (sectionWidth / 2));
            if (i >= numberOfTriangles / 2) drawX -= sectionWidth;
            int color = (i % 2 == 0) ? colorLightWood : colorDarkWood;
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
            try { gameThread.join(); retry = false; } catch (InterruptedException e) {}
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;

        // רקע לוח עץ כהה יותר (Sienna)
        canvas.drawColor(Color.rgb(160, 100, 60)); 

        for (int i = 0; i < triangles.size(); i++) {
            triangles.get(i).draw(canvas, game.board[i]);
        }

        if (middleLine != null) middleLine.draw(canvas, game.p1EatenCount, game.p2EatenCount);

        if (die1 != null && die2 != null) {
            die1.setNumber(game.dice[0]);
            die2.setNumber(game.dice[1]);
            die1.draw(canvas);
            die2.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            for (int i = 0; i < triangles.size(); i++) {
                if (triangles.get(i).isTouched(x, y)) {
                    game.move(i);
                    return true;
                }
            }
        }
        return true;
    }
}
