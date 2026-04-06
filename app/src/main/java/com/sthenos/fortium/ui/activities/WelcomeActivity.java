package com.sthenos.fortium.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.sthenos.fortium.R;

public class WelcomeActivity extends AppCompatActivity {
    private MaterialButton btnSetUp;
    private TextView tvRadiant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences("FortiumApp", MODE_PRIVATE);
        if(prefs.getBoolean("perfilCreado", false)){
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // Para que no ejecute lo de abajo.
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initComponents();
        setColorRadiant();
        setActionListeners();
    }

    private void setActionListeners() {
        btnSetUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, CreateUserActivity.class);
                startActivity(intent);
                finish(); // Para que no pueda volver atrás.
            }
        });
    }

    private void setColorRadiant() {
        tvRadiant.post(new Runnable() {
            @Override
            public void run() {
                int colorInicio = ContextCompat.getColor(WelcomeActivity.this, R.color.fortium_primary_dark);
                int colorFin = ContextCompat.getColor(WelcomeActivity.this, R.color.fortium_primary);
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