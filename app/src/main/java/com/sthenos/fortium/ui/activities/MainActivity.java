package com.sthenos.fortium.ui.activities;

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

import com.google.android.material.button.MaterialButton;
import com.sthenos.fortium.R;
import com.sthenos.fortium.ui.adapters.RutinaAdapter;
import com.sthenos.fortium.data.repository.EntrenamientoRepository;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView tvSaludo, tvPeso, tvRm;
    private RecyclerView rvRutinas;
    private RutinaViewModel rutinaViewModel;
    private MaterialButton btnEmpezarEntrenamiento;
    private UsuarioViewModel usuarioViewModel;


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
        setRecyclerView();
        setObservers();
        setListeners();
        setExampleRm();
    }

    private void setExampleRm() {
        double pesoLevantado = 120.0;
        int repeticiones = 5;

        double e1rmCalculado = EntrenamientoRepository.getInstance(getApplication()).calcular1RM(pesoLevantado, repeticiones);

        tvRm.setText(String.format("%.1f", e1rmCalculado));
    }

    private void setObservers() {
        rutinaViewModel.getAllRutinas().observe(this, rutinas -> {
            ((RutinaAdapter) rvRutinas.getAdapter()).setRutinas(rutinas);
        });
        usuarioViewModel.getUsuarioActual().observe(this, usuario -> {
            setPeso(usuario.getPesoActual());
            setSaludo(usuario.getNombre());
        });
    }

    private void setListeners() {
        btnEmpezarEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numeroAleatorio = (int) (Math.random() * 100);
                Rutina nuevaRutina = new Rutina(
                        2, // usuarioId simulado
                        "Rutina " + numeroAleatorio,
                        "Creada para demostración de base de datos",
                        "2023-11-11"
                );
                rutinaViewModel.insert(nuevaRutina);
                android.widget.Toast.makeText(MainActivity.this,
                        "¡Rutina creada con éxito!", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerView() {
        rvRutinas.setLayoutManager(new LinearLayoutManager(this));
        rvRutinas.setHasFixedSize(true);

        final RutinaAdapter adapter = new RutinaAdapter();
        rvRutinas.setAdapter(adapter);
    }

    private void setPeso(Double peso) {
        tvPeso.setText(peso + " kg");
    }

    private void setSaludo(String nombre) {
        tvSaludo.setText("Hola, " + nombre);
    }

    private void initComponents() {
        tvSaludo = findViewById(R.id.tvSaludo);
        tvPeso = findViewById(R.id.tvPeso);
        rvRutinas = findViewById(R.id.rvRutinas);
        btnEmpezarEntrenamiento = findViewById(R.id.btnEmpezarEntrenamiento);
        tvRm = findViewById(R.id.tvRm);

        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
    }
}