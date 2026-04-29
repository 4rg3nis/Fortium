package com.sthenos.fortium.ui.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.queries.EjercicioConDetalles;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.enums.TipoSerie;
import com.sthenos.fortium.ui.adapters.ActiveWorkoutAdapter;
import com.sthenos.fortium.ui.fragments.ExerciseSelectionBottomSheet;
import com.sthenos.fortium.ui.viewmodels.EjercicioViewModel;
import com.sthenos.fortium.ui.viewmodels.EntrenamientoViewModel;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Actividad de entrenamiento.
 * @author Argenis
 */
public class WorkoutActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private ImageButton btnDiscard;
    private MaterialButton btnFinish, btnAddExercise;
    private RecyclerView rvActiveExercises;
    private TextView tvLiveSeries, tvLiveVolumen;
    private TextInputEditText etWorkoutNotes;

    private RutinaViewModel rutinaViewModel;
    private EntrenamientoViewModel entrenamientoViewModel;
    private EjercicioViewModel ejercicioViewModel;
    private ActiveWorkoutAdapter adapter;

    private int rutinaId = -1;
    private CountDownTimer countDownTimer;
    private BottomSheetDialog restDialog;
    private List<Ejercicio> ejerciciosDisponibles;
    private List<Serie> seriesCompletadasHoy;
    private String fechaInicioString;

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
        rutinaId = getIntent().getIntExtra("rutinaId", -1);

        if (rutinaId != -1) {
            rutinaViewModel.getEjerciciosDeRutina(rutinaId).observe(this, listaPlantilla -> {
                if (listaPlantilla != null && !listaPlantilla.isEmpty()) {
                    adapter.setEjercicios(listaPlantilla);
                }
            });
        } else {
            Toast.makeText(this, "Error: No se encontró la rutina", Toast.LENGTH_SHORT).show();
        }

        ejercicioViewModel.getAllEjercicios().observe(this, ejercicios -> {
            if (ejercicios != null) {
                ejerciciosDisponibles = ejercicios;
            }
        });
    }

    private void setupListeners() {
        btnDiscard.setOnClickListener(v -> mostrarDialogoDescartar());

        // Finalizar Sesión
        btnFinish.setOnClickListener(v -> {
            if (seriesCompletadasHoy.isEmpty()) {
                Toast.makeText(this, "No has completado ninguna serie", Toast.LENGTH_SHORT).show();
                return;
            }

            chronometer.stop();

            // Recogemos las notas escritas por el usuario
            String notas = "";
            if (etWorkoutNotes.getText() != null) {
                notas = etWorkoutNotes.getText().toString().trim();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String fechaFinString = sdf.format(new java.util.Date());

            // Recalculamos el volumen total sumando todas las series completadas.
            // Se hace aquí para garantizar la integridad de los datos
            // antes de guardar la sesión en la base de datos.
            double volumenFinal = 0.0;
            for (Serie s : seriesCompletadasHoy) {
                volumenFinal += (s.getPeso() * s.getRepeticiones());
            }

            // Creamos la sesión con todos los datos integrados
            Sesion sesionHoy = new Sesion(
                    rutinaId,
                    fechaInicioString,
                    fechaFinString,
                    seriesCompletadasHoy.size(),
                    volumenFinal,
                    notas
            );

            // Mandamos a guardar todo
            entrenamientoViewModel.guardarEntrenamientoCompleto(sesionHoy, seriesCompletadasHoy, () -> {
                Toast.makeText(this, "¡Entrenamiento guardado con éxito!", Toast.LENGTH_LONG).show();
                finish(); // Volvemos al menú principal
            });
        });

        // Añadir nuevo ejercicio
        btnAddExercise.setOnClickListener(v -> {
            if (ejerciciosDisponibles.isEmpty()) {
                Toast.makeText(this, "Cargando catálogo de ejercicios...", Toast.LENGTH_SHORT).show();
                return;
            }

            ExerciseSelectionBottomSheet bottomSheet = new ExerciseSelectionBottomSheet();
            bottomSheet.setEjercicios(ejerciciosDisponibles);

            bottomSheet.setListener(ejercicioSeleccionado -> {
                EjercicioConDetalles ejercicioExtra = new EjercicioConDetalles();
                ejercicioExtra.ejercicio = ejercicioSeleccionado;

                int nuevoOrden = adapter.getItemCount() + 1;

                ejercicioExtra.rutinaEjercicio = new com.sthenos.fortium.model.entities.RutinaEjercicio(
                        rutinaId,
                        ejercicioSeleccionado.getId(),
                        1,
                        0,
                        "",
                        nuevoOrden
                );

                adapter.addEjercicioEnVivo(ejercicioExtra);
                Toast.makeText(this, ejercicioSeleccionado.getNombre() + " añadido", Toast.LENGTH_SHORT).show();
            });

            bottomSheet.show(getSupportFragmentManager(), "ExerciseSheet");
        });
    }

    private void mostrarDialogoDescartar() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("¿Descartar entrenamiento?")
                .setMessage("Se perderán todos los datos y series que hayas anotado hoy.")
                .setPositiveButton("Descartar", (dialog, which) -> {
                    chronometer.stop();
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        fechaInicioString = sdf.format(new java.util.Date());
    }

    private void initComponents() {
        chronometer = findViewById(R.id.chronometerWorkout);
        btnDiscard = findViewById(R.id.btnDiscardWorkout);
        btnFinish = findViewById(R.id.btnFinishWorkout);
        btnAddExercise = findViewById(R.id.btnAddExerciseToWorkout);
        rvActiveExercises = findViewById(R.id.rvWorkoutActive);

        tvLiveSeries = findViewById(R.id.tvLiveSeries);
        tvLiveVolumen = findViewById(R.id.tvLiveVolumen);
        etWorkoutNotes = findViewById(R.id.etWorkoutNotes);

        initAdapter();

        rvActiveExercises.setLayoutManager(new LinearLayoutManager(this));
        rvActiveExercises.setAdapter(adapter);

        ejerciciosDisponibles = new ArrayList<>();
        seriesCompletadasHoy = new ArrayList<>();

        ejercicioViewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);
        entrenamientoViewModel = new ViewModelProvider(this).get(EntrenamientoViewModel.class);
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
    }

    private void initAdapter() {
        adapter = new ActiveWorkoutAdapter(this, new ActiveWorkoutAdapter.OnSetActionListener() {
            @Override
            public void onSetCompleted(int tiempoDescansoSegundos) {
                iniciarTemporizadorDescanso(tiempoDescansoSegundos);
            }

            @Override
            public void onSetCompleted(int tiempoDescanso, int ejercicioId, float peso, int reps, float rpe) {
                iniciarTemporizadorDescanso(tiempoDescanso);
                int ordenActual = seriesCompletadasHoy.size() + 1;

                Serie nuevaSerie = new Serie(
                        0,
                        ejercicioId,
                        peso,
                        reps,
                        rpe,
                        TipoSerie.NORMAL,
                        tiempoDescanso,
                        ordenActual
                );

                seriesCompletadasHoy.add(nuevaSerie);

                actualizarStatsEnVivo();
            }

            @Override
            public void onSetUnchecked(int ejercicioId, float peso, int reps) {
                seriesCompletadasHoy.removeIf(s ->
                        s.getEjercicioId() == ejercicioId && s.getPeso() == peso && s.getRepeticiones() == reps);

                actualizarStatsEnVivo();
            }
        });
    }

    private void actualizarStatsEnVivo() {
        double volumenTotal = 0;
        for (Serie s : seriesCompletadasHoy) {
            volumenTotal += (s.getPeso() * s.getRepeticiones());
        }

        tvLiveSeries.setText(String.valueOf(seriesCompletadasHoy.size()));
        tvLiveVolumen.setText(String.format(Locale.getDefault(), "%.1f kg", volumenTotal));
    }

    private void iniciarTemporizadorDescanso(int tiempoDescansoSegundos) {
        if (tiempoDescansoSegundos <= 0) return;
        if (countDownTimer != null) countDownTimer.cancel();

        if (restDialog == null) {
            restDialog = new BottomSheetDialog(this);
            restDialog.setContentView(R.layout.bottom_sheet_rest_timer);
        }

        TextView tvTime = restDialog.findViewById(R.id.tvRestTimerBig);
        MaterialButton btnSkip = restDialog.findViewById(R.id.btnSkipRest);
        MaterialButton btnAdd = restDialog.findViewById(R.id.btnAdd15s);
        MaterialButton btnLess = restDialog.findViewById(R.id.btnLess15s);

        btnSkip.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            restDialog.dismiss();
        });

        btnAdd.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            restDialog.dismiss();
            iniciarTemporizadorDescanso(tiempoDescansoSegundos + 15);
        });

        btnLess.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            restDialog.dismiss();
            iniciarTemporizadorDescanso(tiempoDescansoSegundos - 15);
        });

        restDialog.show();

        countDownTimer = new CountDownTimer(tiempoDescansoSegundos * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long segundosRestantes = millisUntilFinished / 1000;
                long minutos = segundosRestantes / 60;
                long segundos = segundosRestantes % 60;

                String tiempoFormateado = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
                if (tvTime != null) {
                    tvTime.setText(tiempoFormateado);
                }
            }

            @Override
            public void onFinish() {
                if (restDialog != null && restDialog.isShowing()) {
                    restDialog.dismiss();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}