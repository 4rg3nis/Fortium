package com.sthenos.fortium.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.sthenos.fortium.R;
import com.sthenos.fortium.utils.JsonImporter;

public class WelcomeActivity extends AppCompatActivity {
    private MaterialButton btnSetUp, btnImportData;
    private TextView tvRadiant;

    private final ActivityResultLauncher<String[]> importLauncher = registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.OpenDocument(), uri -> {
        if (uri != null) {
            Toast.makeText(this, "Importando datos, por favor espera...", Toast.LENGTH_LONG).show();

            JsonImporter.ejecutarImportacionCompleta(this, uri, new JsonImporter.ImportCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "¡Datos restaurados con éxito!", Toast.LENGTH_LONG).show();

                        // Reiniciamos la app para aplicar cambios
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String mensaje) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show());
                }
            });
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("FortiumApp", MODE_PRIVATE);
        if(prefs.getBoolean("perfilCreado", false)){
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // Para que no ejecute lo de abajo.
        }

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

        btnImportData.setOnClickListener(v -> {
            importLauncher.launch(new String[]{"application/json"});
        });
    }

    private void setColorRadiant() {
        tvRadiant.post(new Runnable() {
            @Override
            public void run() {
                int colorInicio = ContextCompat.getColor(WelcomeActivity.this, R.color.blue_800);
                int colorFin = ContextCompat.getColor(WelcomeActivity.this, R.color.blue_400);
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
        btnImportData = findViewById(R.id.btnImportData);
    }
}