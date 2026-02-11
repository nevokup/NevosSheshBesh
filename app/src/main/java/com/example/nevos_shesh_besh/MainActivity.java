package com.example.nevos_shesh_besh;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nevos_shesh_besh.UI.CustomSurfaceView;
import com.example.nevos_shesh_besh.model.Game;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. יצירת האובייקט של המשחק
        Game game = new Game();

        // 2. הגדרת המאזין לסוף המשחק
        game.setGameOverListener(new Game.GameOverListener() {
            @Override
            public void onGameOver(String winnerName, Game.WinType winType, int score) {
                // מריצים את הדיאלוג על ה-Main Thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWinnerDialog(winnerName, winType, score);
                    }
                });
            }
        });

        // 3. יצירת התצוגה והעברת המשחק אליה
        setContentView(new CustomSurfaceView(this, game));
    }

    private void showWinnerDialog(String winnerName, Game.WinType winType, int score) {
        String typeString = "";
        switch (winType) {
            case REGULAR:
                typeString = "רגיל";
                break;
            case MARS:
                typeString = "מארס!";
                break;
            case TURKISH_MARS:
                typeString = "מארס טורקי (כוכבים)!!!";
                break;
        }

        String message = "המנצח הוא: " + winnerName + "\n" +
                "סוג הניצחון: " + typeString + "\n" +
                "ניקוד: " + score;

        new AlertDialog.Builder(this)
                .setTitle("GAME OVER!")
                .setMessage(message)
                .setCancelable(false) // אי אפשר לסגור בלי ללחוץ על הכפתור
                .setPositiveButton("משחק חדש", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // מתחיל מחדש את האקטיביטי (משחק חדש)
                        recreate();
                    }
                })
                .show();
    }
}