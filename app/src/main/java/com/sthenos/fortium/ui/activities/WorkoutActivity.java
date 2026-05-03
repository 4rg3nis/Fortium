package com.sthenos.fortium.ui.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Actividad de entrenamiento.
 * @author Argenis
 */
public class WorkoutActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private ImageButton btnDiscard, btnTimerClose, btnTimerPlus, btnTimerMinus;
    private MaterialButton btnFinish, btnAddExercise;
    private RecyclerView rvActiveExercises;
    private TextView tvLiveSeries, tvLiveVolumen, tvRestTimerPill;
    private TextInputEditText etWorkoutNotes;
    private RutinaViewModel rutinaViewModel;
    private EntrenamientoViewModel entrenamientoViewModel;
    private EjercicioViewModel ejercicioViewModel;
    private ActiveWorkoutAdapter adapter;
    private int rutinaId = -1;
    private List<Ejercicio> ejerciciosDisponibles;
    private List<Serie> seriesCompletadasHoy;
    private String fechaInicioString;
    private MaterialCardView cardRestTimer;

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
        setupTimeExercise();
    }

    /**
     * Configuramos el temporizador del ejercicio. Cuando esta activi se hace visible, cuando no, se desactiva.
     * Una vez finalizado el temporizador, se le manda el dispositivo que vibre.
     */
    private void setupTimeExercise() {
        entrenamientoViewModel.getTimerActivo().observe(this, activo -> {
            cardRestTimer.setVisibility(activo ? View.VISIBLE : View.GONE);
        });

        entrenamientoViewModel.getTiempoRestante().observe(this, segundos -> {
            if (segundos != null) {
                long min = segundos / 60;
                long seg = segundos % 60;
                tvRestTimerPill.setText(String.format(Locale.getDefault(), "%02d:%02d", min, seg));
            }
        });

        entrenamientoViewModel.getTimerFinalizado().observe(this, finalizado -> {
            if (finalizado) {
                hacerVibrar();
                entrenamientoViewModel.resetTimerFinalizado();
            }
        });
    }

    /**
     * Hacemos vibrar el dispositivo. Ponemos un log para ver que se hace.
     */
    private void hacerVibrar() {
        Log.d("Vibrar", "Vibrar");
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    private void setupViewModel() {
        rutinaId = getIntent().getIntExtra("rutinaId", -1);

        if (rutinaId != -1) {
            rutinaViewModel.getEjerciciosDeRutina(rutinaId).observe(this, listaPlantilla -> {
                if (listaPlantilla != null && !listaPlantilla.isEmpty()) {
                    adapter.setEjercicios(listaPlantilla);
                    cargarRecordsPersonales(listaPlantilla);
                }
            });
        } else {
            Toast.makeText(this, "Entrenamiento Libre iniciado", Toast.LENGTH_SHORT).show();
        }

        ejercicioViewModel.getAllEjercicios().observe(this, ejercicios -> {
            if (ejercicios != null) {
                ejerciciosDisponibles = ejercicios;
            }
        });
    }

    /**
     * Carga los records personales de los ejercicios.
     * @param listaPlantilla La lista de ejercicios de la plantilla
     */
    private void cargarRecordsPersonales(List<EjercicioConDetalles> listaPlantilla) {
        entrenamientoViewModel.getRecords(listaPlantilla).observe(this, records -> {
            adapter.setUltimosRecords(records);
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

            // Esto es para cuando se inicia una sesion sin rutina previa ( Entrenamiento rapido)
            Integer idRutinaParaBD = (rutinaId == -1) ? null : rutinaId;

            // Creamos la sesión con todos los datos integrados
            Sesion sesionHoy = new Sesion(
                    idRutinaParaBD,
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

                ejercicioExtra.rutinaEjercicio = new RutinaEjercicio(
                        rutinaId,
                        ejercicioSeleccionado.getId(),
                        1,
                        0,
                        nuevoOrden
                );

                adapter.addEjercicioEnVivo(ejercicioExtra);
                Toast.makeText(this, ejercicioSeleccionado.getNombre() + " añadido", Toast.LENGTH_SHORT).show();
            });

            bottomSheet.show(getSupportFragmentManager(), "ExerciseSheet");
        });

        btnTimerClose.setOnClickListener(v -> entrenamientoViewModel.cancelarTemporizador());
        btnTimerPlus.setOnClickListener(v -> entrenamientoViewModel.ajustarTemporizador(15));
        btnTimerMinus.setOnClickListener(v -> entrenamientoViewModel.ajustarTemporizador(-15));
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
        fechaInicioString = sdf.format(new Date());
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
        cardRestTimer = findViewById(R.id.cardRestTimer);
        tvRestTimerPill = findViewById(R.id.tvRestTimerPill);

        btnTimerClose = findViewById(R.id.btnTimerClose);
        btnTimerPlus = findViewById(R.id.btnTimerPlus);
        btnTimerMinus = findViewById(R.id.btnTimerMinus);


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
            public void onSetCompleted(int tiempoDescanso, int ejercicioId, float peso, int reps, float rpe, String nota) {
                int ordenActual = seriesCompletadasHoy.size() + 1;

                Serie nuevaSerie = new Serie(
                        0,
                        ejercicioId,
                        peso,
                        reps,
                        rpe,
                        nota,
                        tiempoDescanso,
                        ordenActual
                );

                seriesCompletadasHoy.add(nuevaSerie);

                actualizarStatsEnVivo();

                entrenamientoViewModel.iniciarTemporizador(tiempoDescanso);
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
}