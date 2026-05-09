package com.sthenos.fortium.ui.sesion;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.sthenos.fortium.R;
import com.sthenos.fortium.ui.settings.UsuarioViewModel;
import com.sthenos.fortium.ui.workout.EntrenamientoViewModel;
import com.sthenos.fortium.utils.Converters;

import java.util.Locale;

/**
 * Actividad para mostrar los detalles de una sesión de entrenamiento.
 * @author Argenis
 */
public class DetallesSesionActivity extends AppCompatActivity {

    private TextView tvDetalleFecha, tvDetalleTiempo, tvDetalleVolumen, tvDetalleSeries, tvDetalleNotas;
    private RecyclerView rvDetalleEjercicios;
    private MaterialCardView cardDetalleNotas;
    private MaterialToolbar toolbar;

    private int sesionId;
    private EntrenamientoViewModel entrenamientoViewModel;
    private DetallesSesionAdapter adapter;
    private UsuarioViewModel usuarioViewModel;
    private String unidad = "kg";
    private double volumenActual = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sesionId = getIntent().getIntExtra("sesionId", -1);
        if (sesionId == -1) {
            finish();
            return;
        }

        initComponents();
        setupToolbar();
        setObersevers();
    }

    /**
     * Configurar la toolbar.
     */
    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Establecer los observadores.
     */
    private void setObersevers() {
        entrenamientoViewModel.getSesionById(sesionId).observe(this, sesion -> {
            if (sesion != null) {
                String[] partesFecha = sesion.getFechaInicio().split(" ");
                if (partesFecha.length >= 2) {
                    tvDetalleFecha.setText(partesFecha[0].replace("-", "/"));

                    tvDetalleTiempo.setText(partesFecha[1].substring(0, 5));
                }
                volumenActual = sesion.getVolumenTotal();
                setUnits();
                tvDetalleSeries.setText(String.valueOf(sesion.getCantidadSeries()));


                if (sesion.getNotas() != null && !sesion.getNotas().trim().isEmpty()) {
                    cardDetalleNotas.setVisibility(View.VISIBLE);
                    tvDetalleNotas.setText(sesion.getNotas());
                } else {
                    cardDetalleNotas.setVisibility(View.GONE);
                }
            }
        });

        entrenamientoViewModel.getSeriesDeSesion(sesionId).observe(this, series -> {
            if (series != null && !series.isEmpty()) {
                adapter.setDatosBrutos(series);
            }
        });

        usuarioViewModel.getUsuarioActual().observe(this, usuario -> {
            if (usuario != null) {
                unidad = Converters.fromUnitMeasure(usuario.getUnidadmedida()).toLowerCase();
                adapter.setUnits(unidad);
                setUnits();
            }
        });
    }

    /**
     * Establecer las unidades de medida.
     */
    private void setUnits() {
        tvDetalleVolumen.setText(String.format(Locale.getDefault(), "%.1f %s", volumenActual, unidad));
    }

    /**
     * Inicializar los componentes.
     */
    private void initComponents() {
        toolbar = findViewById(R.id.toolbarDetalles);

        tvDetalleFecha = findViewById(R.id.tvDetalleFecha);
        tvDetalleTiempo = findViewById(R.id.tvDetalleTiempo);
        tvDetalleVolumen = findViewById(R.id.tvDetalleVolumen);
        tvDetalleSeries = findViewById(R.id.tvDetalleSeries);
        rvDetalleEjercicios = findViewById(R.id.rvDetalleEjercicios);
        tvDetalleNotas = findViewById(R.id.tvDetalleNotas);
        cardDetalleNotas = findViewById(R.id.cardDetalleNotas);

        entrenamientoViewModel = new ViewModelProvider(this).get(EntrenamientoViewModel.class);

        rvDetalleEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetallesSesionAdapter();
        rvDetalleEjercicios.setAdapter(adapter);
        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
    }
}