package com.sthenos.fortium.ui.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.ui.fragments.ExerciseSelectionBottomSheet;
import com.sthenos.fortium.ui.viewmodels.EjercicioViewModel;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;

import java.util.ArrayList;
import java.util.List;

public class RutinaDetalleActivity extends AppCompatActivity {

    private int rutinaId;
    private TextView tvRoutineName, tvTotalExercises, tvTotalSets, tvRoutineDesc, tvRoutineDate;
    private RutinaViewModel rutinaViewModel;
    private EjercicioViewModel ejercicioViewModel;
    private List<Ejercicio> ejerciciosDisponibles = new ArrayList<>();
    private ImageButton btnBack;
    private MaterialButton btnAddEjercicio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rutina_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rutinaId = getIntent().getIntExtra("rutinaId", -1);

        if(rutinaId == -1){
            finish();
            return;
        }

        initComponents();
        setupViewModel();
        setListeners();
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnAddEjercicio.setOnClickListener(v -> {
            if(ejerciciosDisponibles.isEmpty()) {
                Toast.makeText(this, "Cargando ejercicios...", Toast.LENGTH_SHORT).show();
                return;
            }
            ExerciseSelectionBottomSheet bottomSheet = new ExerciseSelectionBottomSheet();

            bottomSheet.setEjercicios(ejerciciosDisponibles);

            bottomSheet.setListener(ejercicioSeleccionado -> {

                Toast.makeText(this, "Elegiste: " + ejercicioSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();

                // Aquí es donde en el siguiente paso enlazaremos el 'rutinaId' con este 'ejercicio.getId()'
                // en la tabla RutinaEjercicio.
            });

            bottomSheet.show(getSupportFragmentManager(), "ExerciseSheet");
        });
    }

    private void setupViewModel() {
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        ejercicioViewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);

        rutinaViewModel.getRutinaById(rutinaId).observe(this,rutina -> {
            if(rutina != null) {
                tvRoutineName.setText(rutina.getNombre());
                tvRoutineDesc.setText(rutina.getDescripcion());
                tvRoutineDate.setText("Creada: " + rutina.getFechaCreacion());

                // Aqui se implementaria la logica de obtener el total de ejercicios y series
            }
        });

        ejercicioViewModel.getAllEjercicios().observe(this, ejercicios -> {
            if (ejercicios != null) {
                ejerciciosDisponibles = ejercicios;
            }
        });
    }

    private void initComponents() {
        tvRoutineName = findViewById(R.id.tvRoutineName);
        tvTotalExercises = findViewById(R.id.tvTotalExercises);
        tvTotalSets = findViewById(R.id.tvTotalSets);
        tvRoutineDesc = findViewById(R.id.tvRoutineDesc);
        tvRoutineDate = findViewById(R.id.tvRoutineDate);
        btnBack = findViewById(R.id.btnBack);
        btnAddEjercicio = findViewById(R.id.btnAddEjercicio);
    }
}