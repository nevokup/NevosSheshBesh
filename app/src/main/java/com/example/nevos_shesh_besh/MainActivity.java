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

        game.setGameOverListener((winnerName, winTypeDesc) ->
                runOnUiThread(() -> showWinnerDialog(winnerName, winTypeDesc))
        );

        showLobbyDialog();
    }

    private void showLobbyDialog() {
        String[] options = {"צור משחק", "הצטרף למשחק", "משחק יחיד (מקומי)"};
        new AlertDialog.Builder(this)
                .setTitle("שש-בש")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        isOnlineMode = true;
                        game.isOnlineMode_Internal = true;
                        startGameAsHost();
                    } else if (which == 1) {
                        isOnlineMode = true;
                        game.isOnlineMode_Internal = true;
                        showJoinDialog();
                    } else {
                        isOnlineMode = false;
                        game.isOnlineMode_Internal = false;
                        startLocalSinglePlayer();
                    }
                })
                .setCancelable(false).show();
    }

    private void startLocalSinglePlayer() {
        game = new Game();
        game.isOnlineMode_Internal = false;
        game.setGameOverListener((winnerName, winTypeDesc) ->
                runOnUiThread(() -> showWinnerDialog(winnerName, winTypeDesc))
        );
        startGameView();
        Toast.makeText(this, "משחק יחיד (מקומי) התחיל", Toast.LENGTH_SHORT).show();
    }

    private void startGameAsHost() {
        String code = String.valueOf((int)(Math.random() * 9000) + 1000);
        game.localPlayerIsP1 = true;
        networkManager.createGame(code, game, data -> {
            runOnUiThread(() -> {
                game.updateFromMap(data);
                handleRemoteUpdate();
                if (gameView != null) gameView.invalidate();
            });
        });
        Toast.makeText(this, "קוד משחק: " + code, Toast.LENGTH_LONG).show();
        startGameView();
    }

    private void showJoinDialog() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("הכנס קוד")
                .setView(input)
                .setPositiveButton("הצטרף", (dialog, which) -> {
                    game.localPlayerIsP1 = false;
                    networkManager.joinGame(input.getText().toString(), data -> {
                        runOnUiThread(() -> {
                            game.updateFromMap(data);
                            handleRemoteUpdate();
                            if (gameView != null) gameView.invalidate();
                        });
                    });
                    startGameView();
                }).show();
    }

    private void handleRemoteUpdate() {
        if (game.isGameOver) {
            showWinnerDialog(game.winnerName, game.winTypeString);
        }
    }

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

    private void showWinnerDialog(String winnerName, String winTypeDesc) {
        if (isFinishing()) return;
        new AlertDialog.Builder(this)
                .setTitle("המשחק נגמר!")
                .setMessage(winnerName + " ניצח!\nסוג ניצחון: " + winTypeDesc)
                .setCancelable(false)
                .setPositiveButton("חזרה לתפריט", (dialog, which) -> recreate())
                .show();
    }
}