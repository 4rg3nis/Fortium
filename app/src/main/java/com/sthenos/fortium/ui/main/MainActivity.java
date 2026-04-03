package com.sthenos.fortium.ui.main;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.sthenos.fortium.R;

public class MainActivity extends AppCompatActivity {


    private MaterialButton btnSetUp;
    private TextView tvRadiant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initComponents();
        setColorRadiant();
    }

    private void setColorRadiant() {
        tvRadiant.post(new Runnable() {
            @Override
            public void run() {
                int colorInicio = ContextCompat.getColor(MainActivity.this, R.color.fortium_primary_dark);
                int colorFin = ContextCompat.getColor(MainActivity.this, R.color.fortium_primary);
                Shader shader = new LinearGradient(
                        0,0,tvRadiant.getWidth(),0,
                        colorInicio, colorFin,
                        Shader.TileMode.CLAMP
                );
                tvRadiant.getPaint().setShader(shader);
                tvRadiant.invalidate();
            }
        });
    }

    private void initComponents() {
        btnSetUp = findViewById(R.id.btnSetUpProfile);
        tvRadiant = findViewById(R.id.tvRadiant);
    }
}