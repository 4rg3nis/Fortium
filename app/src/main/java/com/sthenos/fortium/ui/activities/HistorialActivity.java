package com.sthenos.fortium.ui.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sthenos.fortium.R;
import com.sthenos.fortium.ui.adapters.HistorialAdapter;
import com.sthenos.fortium.ui.viewmodels.EntrenamientoViewModel;

public class HistorialActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView rvCompleto;
    private EntrenamientoViewModel entrenamientoViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        setListeners();
        cargarHistorial();
    }

    /**
     * Carga el historial de sesiones desde la base de datos en un RecyclerView
     */
    private void cargarHistorial() {
        RecyclerView rvCompleto = findViewById(R.id.rvHistorialCompleto);
        rvCompleto.setLayoutManager(new LinearLayoutManager(this));

        HistorialAdapter adapter = new HistorialAdapter(true, sesion -> {
            Toast.makeText(HistorialActivity.this, "Clic en: " + sesion.nombreRutina, android.widget.Toast.LENGTH_SHORT).show();
        });
        rvCompleto.setAdapter(adapter);

        entrenamientoViewModel.getHistorialCompleto().observe(this, sesiones -> {
            if (sesiones != null) {
                adapter.setSesiones(sesiones);
            }
        });
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

    }

    private void initComponents() {
        btnBack = findViewById(R.id.btnBack);
        rvCompleto = findViewById(R.id.rvHistorialCompleto);

        entrenamientoViewModel = new ViewModelProvider(this).get(EntrenamientoViewModel.class);
    }
}