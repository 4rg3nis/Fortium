package com.sthenos.fortium.ui.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.sthenos.fortium.ui.settings.UsuarioViewModel;
import com.sthenos.fortium.ui.workout.EntrenamientoViewModel;
import com.sthenos.fortium.utils.Converters;

/**
 * Actividad que muestra el historial completo de sesiones.
 * @author Argenis
 */
public class SesionHistorialActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout tvEmptyHistorial;
    private RecyclerView rvCompleto;
    private EntrenamientoViewModel entrenamientoViewModel;
    private UsuarioViewModel usuarioViewModel;
    private String unidadPeso = "kg";


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
        rvCompleto.setLayoutManager(new LinearLayoutManager(this));

        SesionHistorialAdapter adapter = new SesionHistorialAdapter(true, new SesionHistorialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistorialSesion sesion) {
                Intent intent = new Intent(SesionHistorialActivity.this, DetallesSesionActivity.class);
                intent.putExtra("sesionId", sesion.sesionId);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(HistorialSesion sesion) {
                new MaterialAlertDialogBuilder(SesionHistorialActivity.this)
                        .setTitle("¿Eliminar entrenamiento?")
                        .setMessage("Vas a borrar tu sesión '" + (sesion.nombreRutina != null ? sesion.nombreRutina : "Libre") + "'. Se borrarán todas las series y volumen de tu historial. Esta acción no se puede deshacer.")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            // Llamamos al ViewModel para que la fulmine
                            entrenamientoViewModel.eliminarSesionCompleta(sesion.sesionId);
                            Toast.makeText(SesionHistorialActivity.this, "Sesión eliminada", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
        rvCompleto.setAdapter(adapter);

        entrenamientoViewModel.getHistorialCompleto().observe(this, sesiones -> {
            if (sesiones != null && !sesiones.isEmpty()) {
                // Hay sesiones: Le pasamos los datos al adaptador
                adapter.setSesiones(sesiones);

                // Mostramos la lista y ocultamos el mensaje
                rvCompleto.setVisibility(View.VISIBLE);
                tvEmptyHistorial.setVisibility(View.GONE);
            } else {
                // No hay sesiones (lista vacía)
                // Ocultamos la lista y mostramos el mensaje
                rvCompleto.setVisibility(View.GONE);
                tvEmptyHistorial.setVisibility(View.VISIBLE);
            }
        });

        usuarioViewModel.getUsuarioActual().observe(this, usuario -> {
            if (usuario != null) {
                unidadPeso = Converters.fromUnitMeasure(usuario.getUnidadmedida());
                adapter.setUnidadPeso(unidadPeso);
            }
        });
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

    }

    private void initComponents() {
        btnBack = findViewById(R.id.btnBack);
        rvCompleto = findViewById(R.id.rvHistorialCompleto);
        tvEmptyHistorial = findViewById(R.id.viewEmptyState);

        entrenamientoViewModel = new ViewModelProvider(this).get(EntrenamientoViewModel.class);
        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
    }
}