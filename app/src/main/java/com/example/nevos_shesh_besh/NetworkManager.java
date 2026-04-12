package com.example.nevos_shesh_besh;

import com.example.nevos_shesh_besh.model.Game;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

public class NetworkManager {
    private FirebaseFirestore db;
    private DocumentReference gameRef;

    public interface OnGameUpdateListener {
        void onUpdate(Map<String, Object> data);
    }

    public NetworkManager() {
        // כאן תהיה השגיאה עד לסינכרון ה-SDK
        try { db = FirebaseFirestore.getInstance(); } catch (Exception e) {}
    }

    public void createGame(String code, Game game, OnGameUpdateListener listener) {
        gameRef = db.collection("Games").document(code);
        gameRef.set(game.toMap());
        listenToUpdates(listener);
    }

    public void joinGame(String code, OnGameUpdateListener listener) {
        gameRef = db.collection("Games").document(code);
        listenToUpdates(listener);
    }

    public void updateGameState(Game game) {
        if (gameRef != null) gameRef.update(game.toMap());
    }

    private void listenToUpdates(OnGameUpdateListener listener) {
        gameRef.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) listener.onUpdate(snapshot.getData());
        });
    }
}
