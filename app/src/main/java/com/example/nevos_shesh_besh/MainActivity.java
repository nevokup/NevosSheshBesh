package com.example.nevos_shesh_besh;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nevos_shesh_besh.UI.CustomSurfaceView;
import com.example.nevos_shesh_besh.model.Game;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private NetworkManager networkManager;
    private CustomSurfaceView gameView;
    private boolean isOnlineMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = new Game();
        networkManager = new NetworkManager();

        // מאזין לשחקן שמבצע את המהלך המנצח מקומית
        game.setGameOverListener((winnerName, winType, score) ->
                runOnUiThread(() -> showWinnerDialog(winnerName, winType, score))
        );

        showLobbyDialog();
    }

    private void showLobbyDialog() {
        String[] options = {"צור משחק", "הצטרף למשחק", "משחק מקומי"};
        new AlertDialog.Builder(this)
                .setTitle("שש-בש")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) { isOnlineMode = true; startGameAsHost(); }
                    else if (which == 1) { isOnlineMode = true; showJoinDialog(); }
                    else { isOnlineMode = false; startLocalGame(); }
                })
                .setCancelable(false).show();
    }

    private void startGameAsHost() {
        String code = String.valueOf((int)(Math.random() * 9000) + 1000);
        game.localPlayerIsP1 = true;
        networkManager.createGame(code, game, data -> {
            runOnUiThread(() -> {
                game.updateFromMap(data);
                handleRemoteUpdate(); // בדיקה בכל עדכון מהענן
                if (gameView != null) gameView.invalidate();
            });
        });
        Toast.makeText(this, "קוד משחק: " + code, Toast.LENGTH_LONG).show();
        startGameView();
    }

    private void showJoinDialog() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("הכנס קוד משחק")
                .setView(input)
                .setPositiveButton("הצטרף", (dialog, which) -> {
                    game.localPlayerIsP1 = false;
                    networkManager.joinGame(input.getText().toString(), data -> {
                        runOnUiThread(() -> {
                            game.updateFromMap(data);
                            handleRemoteUpdate(); // מאפשר למצטרף לראות הודעת סיום
                            if (gameView != null) gameView.invalidate();
                        });
                    });
                    startGameView();
                }).show();
    }

    private void handleRemoteUpdate() {
        // אם הנתונים מהענן אומרים שהמשחק נגמר, מקפיצים את הדיאלוג
        if (game.isGameOver) {
            showWinnerDialog(game.winnerName, Game.WinType.REGULAR, 1);
        }
    }

    private void startLocalGame() { game.localPlayerIsP1 = true; startGameView(); }

    private void startGameView() {
        gameView = new CustomSurfaceView(this, game);
        setContentView(gameView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP && isOnlineMode) {
            networkManager.updateGameState(game);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showWinnerDialog(String winnerName, Game.WinType winType, int score) {
        if (isFinishing()) return;
        new AlertDialog.Builder(this)
                .setTitle("המשחק נגמר!")
                .setMessage("המנצח: " + winnerName)
                .setCancelable(false)
                .setPositiveButton("חזרה לתפריט", (dialog, which) -> recreate())
                .show();
    }
}