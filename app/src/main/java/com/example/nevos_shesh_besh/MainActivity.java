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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = new Game();
        networkManager = new NetworkManager();

        game.setGameOverListener((winnerName, winType, score) ->
                runOnUiThread(() -> showWinnerDialog(winnerName, winType, score))
        );

        showLobbyDialog();
    }

    private void showLobbyDialog() {
        String[] options = {"צור משחק", "הצטרף למשחק"};
        new AlertDialog.Builder(this)
                .setTitle("שש-בש אונליין")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) startGameAsHost();
                    else showJoinDialog();
                })
                .setCancelable(false)
                .show();
    }

    private void startGameAsHost() {
        String code = String.valueOf((int)(Math.random() * 9000) + 1000);
        game.localPlayerIsP1 = true;
        networkManager.createGame(code, game, data -> game.updateFromMap(data));
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
                    networkManager.joinGame(input.getText().toString(), data -> game.updateFromMap(data));
                    startGameView();
                }).show();
    }

    private void startGameView() {
        gameView = new CustomSurfaceView(this, game);
        setContentView(gameView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            networkManager.updateGameState(game);
        }
        return handled;
    }

    private void showWinnerDialog(String winnerName, Game.WinType winType, int score) {
        new AlertDialog.Builder(this).setTitle("סוף משחק").setMessage("המנצח: " + winnerName)
                .setPositiveButton("חדש", (d, w) -> recreate()).show();
    }
}