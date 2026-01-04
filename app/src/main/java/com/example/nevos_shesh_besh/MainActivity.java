package com.example.nevos_shesh_besh;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nevos_shesh_besh.UI.CustomSurfaceView;
import com.example.nevos_shesh_besh.model.Game;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new CustomSurfaceView(this, new Game()));
    }
}
