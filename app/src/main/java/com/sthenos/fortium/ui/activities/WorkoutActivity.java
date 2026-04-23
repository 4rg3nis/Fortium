package com.sthenos.fortium.ui.activities;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sthenos.fortium.R;
import com.sthenos.fortium.ui.adapters.ActiveWorkoutAdapter;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;

public class WorkoutActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private ImageButton btnDiscard;
    private MaterialButton btnFinish, btnAddExercise;
    private RecyclerView rvActiveExercises;
    private RutinaViewModel rutinaViewModel;
    private ActiveWorkoutAdapter adapter;
    private int rutinaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        setupChronometer();
        setupListeners();
        setupViewModel();
    }

    private void setupViewModel() {
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        rutinaId = getIntent().getIntExtra("rutinaId", -1);

        if (rutinaId != -1) {
            rutinaViewModel.getEjerciciosDeRutina(rutinaId).observe(this, listaPlantilla -> {
                if (listaPlantilla != null && !listaPlantilla.isEmpty()) {
                    // Pasamos los ejercicios reales al adaptador
                    adapter.setEjercicios(listaPlantilla);
                }
            });
        } else {
            Toast.makeText(this, "Error: No se encontró la rutina", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        btnDiscard.setOnClickListener(v -> mostrarDialogoDescartar());

        // Finalizar Sesión (Guardar en BBDD)
        btnFinish.setOnClickListener(v -> {
            chronometer.stop();
            long tiempoEntrenamientoMs = SystemClock.elapsedRealtime() - chronometer.getBase();
            // TODO: Guardar toda la sesión y las series marcadas en la BBDD
            Toast.makeText(this, "¡Entrenamiento guardado!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Añadir nuevo ejercicio "al vuelo" estos no estarán visible en la parte del detalle de la rutina
        btnAddExercise.setOnClickListener(v -> {
            // Aquí abriríamos el mismo BottomSheet de ejercicios que en detalle rutina
            Toast.makeText(this, "Abrir catálogo de ejercicios...", Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarDialogoDescartar() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("¿Descartar entrenamiento?")
                .setMessage("Se perderán todos los datos y series que hayas anotado hoy.")
                .setPositiveButton("Descartar", (dialog, which) -> {
                    chronometer.stop();
                    finish(); // Cierra la pantalla sin guardar
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupChronometer() {
        // Arrancamos el cronómetro desde cero en el momento que se abre la pantalla
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void initComponents() {
        chronometer = findViewById(R.id.chronometerWorkout);
        btnDiscard = findViewById(R.id.btnDiscardWorkout);
        btnFinish = findViewById(R.id.btnFinishWorkout);
        btnAddExercise = findViewById(R.id.btnAddExerciseToWorkout);


        rvActiveExercises = findViewById(R.id.rvWorkoutActive);
        rvActiveExercises.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ActiveWorkoutAdapter(this);
        rvActiveExercises.setAdapter(adapter);
    }
}