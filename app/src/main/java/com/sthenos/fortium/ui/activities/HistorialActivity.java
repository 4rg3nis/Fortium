package com.sthenos.fortium.ui.activities;

import android.content.Intent;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.queries.HistorialSesion;
import com.sthenos.fortium.ui.adapters.HistorialAdapter;
import com.sthenos.fortium.ui.viewmodels.EntrenamientoViewModel;

/**
 * Actividad que muestra el historial completo de sesiones.
 * @author Argenis
 */
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

        HistorialAdapter adapter = new HistorialAdapter(true, new HistorialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistorialSesion sesion) {
                Intent intent = new android.content.Intent(HistorialActivity.this, DetallesSesionActivity.class);
                intent.putExtra("sesionId", sesion.sesionId);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(HistorialSesion sesion) {
                new MaterialAlertDialogBuilder(HistorialActivity.this)
                        .setTitle("¿Eliminar entrenamiento?")
                        .setMessage("Vas a borrar tu sesión '" + (sesion.nombreRutina != null ? sesion.nombreRutina : "Libre") + "'. Se borrarán todas las series y volumen de tu historial. Esta acción no se puede deshacer.")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            // Llamamos al ViewModel para que la fulmine
                            entrenamientoViewModel.eliminarSesionCompleta(sesion.sesionId);
                            android.widget.Toast.makeText(HistorialActivity.this, "Sesión eliminada", android.widget.Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
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