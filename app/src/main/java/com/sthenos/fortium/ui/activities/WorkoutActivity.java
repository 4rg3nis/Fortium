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
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.EjercicioConDetalles;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
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
    private RutinaViewModel rutinaViewModel;
    private EntrenamientoViewModel entrenamientoViewModel;
    private ActiveWorkoutAdapter adapter;
    private int rutinaId = -1;

    private CountDownTimer countDownTimer;
    private BottomSheetDialog restDialog;

    private List<Ejercicio> ejerciciosDisponibles;

    private List<Serie> seriesCompletadasHoy;

    private String fechaInicioString;

    private EjercicioViewModel ejercicioViewModel;
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

    /**
     * Configura los view model's y sus observadores.
     */
    private void setupViewModel() {

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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String fechaFinString = sdf.format(new java.util.Date());

            long fechaHoy = System.currentTimeMillis();
            Sesion sesionHoy = new Sesion(
                    rutinaId,
                    fechaInicioString,
                    fechaFinString,
                    seriesCompletadasHoy.size(),
                    false,
                    ""
            );

            // Mandamos a guardar todo
            entrenamientoViewModel.guardarEntrenamientoCompleto(sesionHoy, seriesCompletadasHoy, () -> {
                Toast.makeText(this, "¡Entrenamiento guardado con éxito!", Toast.LENGTH_LONG).show();
                finish(); // Volvemos al menú principal
            });
        });

        // Añadir nuevo ejercicio "al vuelo" estos no estarán visible en la parte del detalle de la rutina
        btnAddExercise.setOnClickListener(v -> {
            // Aquí abriríamos el mismo BottomSheet de ejercicios que en detalle rutina

            if (ejerciciosDisponibles.isEmpty()) {
                Toast.makeText(this, "Cargando catálogo de ejercicios...", Toast.LENGTH_SHORT).show();
                return;
            }

            // Creamos el menú desplegable
            ExerciseSelectionBottomSheet bottomSheet = new ExerciseSelectionBottomSheet();

            // Le pasamos todos los ejercicios
            bottomSheet.setEjercicios(ejerciciosDisponibles);

            // Escuchamos cuál elige el usuario
            bottomSheet.setListener(ejercicioSeleccionado -> {

                EjercicioConDetalles ejercicioExtra = new EjercicioConDetalles();
                ejercicioExtra.ejercicio = ejercicioSeleccionado;

                int nuevoOrden = adapter.getItemCount() + 1;

                // Le creamos un enlace ficticio: 1 serie y 0 reps por defecto
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

            // Lo mostramos
            bottomSheet.show(getSupportFragmentManager(), "ExerciseSheet");
        });
    }

    /**
     * Muestra un diálogo de confirmación para descartar la sesión.
     */
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

    /**
     * Configura el cronómetro y tambien guarda la fecha de inicio de la sesión.
     */
    private void setupChronometer() {
        // Arrancamos el cronómetro desde cero en el momento que se abre la pantalla
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        // Uso el formato yyyy-MM-dd sobre todo para a la hora de hacer una query en Room lo haga bien al indicar la fecha.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        fechaInicioString = sdf.format(new java.util.Date());
    }

    /**
     * Inicializa los componentes de la pantalla.
     */
    private void initComponents() {
        chronometer = findViewById(R.id.chronometerWorkout);
        btnDiscard = findViewById(R.id.btnDiscardWorkout);
        btnFinish = findViewById(R.id.btnFinishWorkout);
        btnAddExercise = findViewById(R.id.btnAddExerciseToWorkout);
        rvActiveExercises = findViewById(R.id.rvWorkoutActive);

        initAdapter();

        rvActiveExercises.setLayoutManager(new LinearLayoutManager(this));
        rvActiveExercises.setAdapter(adapter);

        ejerciciosDisponibles = new ArrayList<>();
        seriesCompletadasHoy = new ArrayList<>();

        ejercicioViewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);
        entrenamientoViewModel = new ViewModelProvider(this).get(EntrenamientoViewModel.class);
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
    }

    /**
     * Inicializa el adaptador de ejercicios activos.
     */
    private void initAdapter() {
        adapter = new ActiveWorkoutAdapter(this, new ActiveWorkoutAdapter.OnSetActionListener() {
            @Override
            public void onSetCompleted(int tiempoDescansoSegundos) {
                iniciarTemporizadorDescanso(tiempoDescansoSegundos);
            }

            @Override
            public void onSetCompleted(int tiempoDescanso, int ejercicioId, float peso, int reps, float rpe) {
                // Iniciar el cronómetro de descanso
                iniciarTemporizadorDescanso(tiempoDescanso);
                int ordenActual = seriesCompletadasHoy.size() + 1;

                // TODO: Comprobar el tipo de serie que esta para actualizarlo.
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
            }

            @Override
            public void onSetUnchecked(int ejercicioId, float peso, int reps) {
                // Buscamos esta serie en la lista y la borramos
                for (int i = 0; i < seriesCompletadasHoy.size(); i++) {
                    Serie s = seriesCompletadasHoy.get(i);
                    if (s.getEjercicioId() == ejercicioId && s.getPeso() == peso && s.getRepeticiones() == reps) {
                        seriesCompletadasHoy.remove(i);
                        break;
                    }
                }
            }
        });
    }

    /**
     * Inicia el temporizador de descanso.
     * @param tiempoDescansoSegundos
     */
    private void iniciarTemporizadorDescanso(int tiempoDescansoSegundos) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Preparamos el panel
        if (restDialog == null) {
            restDialog = new BottomSheetDialog(this);
            restDialog.setContentView(R.layout.bottom_sheet_rest_timer);
        }

        TextView tvTime = restDialog.findViewById(R.id.tvRestTimerBig);
        MaterialButton btnSkip = restDialog.findViewById(R.id.btnSkipRest);
        MaterialButton btnAdd = restDialog.findViewById(R.id.btnAdd15s);
        MaterialButton btnLess = restDialog.findViewById(R.id.btnLess15s);


        // Lógica de los botones del panel
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

        // Arrancamos el cronómetro de Android. Multiplicamos por 1000 porque funciona en milisegundos
        countDownTimer = new CountDownTimer(tiempoDescansoSegundos * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Esto se ejecuta CADA SEGUNDO
                long segundosRestantes = millisUntilFinished / 1000;
                long minutos = segundosRestantes / 60;
                long segundos = segundosRestantes % 60;

                // Formateamos para que se vea como "01:30"
                String tiempoFormateado = String.format("%02d:%02d", minutos, segundos);
                if (tvTime != null) {
                    tvTime.setText(tiempoFormateado);
                }
            }

            @Override
            public void onFinish() {
                // Cuando el tiempo se acaba
                if (restDialog != null && restDialog.isShowing()) {
                    restDialog.dismiss();
                }
                // Opcional: Hacer vibrar el móvil
                // android.os.Vibrator v = (android.os.Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // if (v != null) v.vibrate(500);
            }
        }.start();
    }

    // Es vital cancelar el cronómetro si el usuario cierra la app de golpe
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}