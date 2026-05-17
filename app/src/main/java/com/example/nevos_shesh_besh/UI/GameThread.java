package com.example.nevos_shesh_besh.UI;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
// מחלקה היורשת מ-Thread ומנהלת את ה-Game Loop (לולאת המשחק הראשי הגרפית).

    private final SurfaceHolder surfaceHolder;
    // אובייקט מערכת המאפשר לנעול ולפתוח את הקנבס (Canvas) לצורך ציור ישיר על ה-SurfaceView.

    private final CustomSurfaceView gameView;
    // הפניה לרכיב התצוגה של המשחק המכיל את פונקציית ה-draw (הציור).

    private boolean isRunning = false;
    // משתנה בוליאני השולט על המשך ריצת הלולאה בשרשור (כל עוד הוא אמת, המשחק ממשיך לצייר את עצמו).

    public GameThread(SurfaceHolder surfaceHolder, CustomSurfaceView gameView) {
        // קונסטרקטור (בנאי) המקבל את ה-Holder וה-View ומקשר אותם לשרשור הנוכחי.
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean isRunning) {
        // פונקציה חיצונית המאפשרת להפעיל או לעצור את ריצת הלולאה של השרשור (למשל כשהאפליקציה עוברת לרקע).
        this.isRunning = isRunning;
    }

    @Override
    public void run() {
        // פונקציית הריצה הראשית של השרשור. מכילה לולאת 'while' שכל עוד היא פעילה, היא נועלת את הקנבס, קוראת לפונקציית הציור של לוח המשחק, ומציגה אותו על המסך באופן מיידי.
        while (isRunning) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(); // נעילת הקנבס לצורך ציור בטוח
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        gameView.draw(canvas); // קריאה לפונקציית הציור של הלוח והחיילים
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas); // שחרור הקנבס ועדכון המסך חזותית
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}