package com.sthenos.fortium.ui.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
import com.sthenos.fortium.ui.adapters.RutinaEjercicioAdapter;
import com.sthenos.fortium.ui.fragments.ExerciseSelectionBottomSheet;
import com.sthenos.fortium.ui.viewmodels.EjercicioViewModel;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad que muestra los detalles de una rutina.
 * @author Argenis
 */
public class RutinaDetalleActivity extends AppCompatActivity {

    private int rutinaId;
    private int ejercicioCount = 0;
    private TextView tvRoutineName, tvTotalExercises, tvTotalSets, tvRoutineDesc, tvRoutineDate;
    private RecyclerView rvEjerciciosRutina;
    private RutinaEjercicioAdapter rutinaEjercicioAdapter;
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

        // Aqui consigo el id de la rutina que se ha seleccionado en la lista de rutinas.
        rutinaId = getIntent().getIntExtra("rutinaId", -1);

        // En caso de que no se haya seleccionado ninguna rutina, salimos.
        if(rutinaId == -1){
            finish();
            return;
        }

        initComponents();
        setupViewModel();
        setListeners();
    }

    /**
     * Establece los listener de los botones.
     */
    private void setListeners() {
        // Botón de volver a la lista de rutinas.
        btnBack.setOnClickListener(v -> finish());
        // Botón para añadir un ejercicio a la rutina.
        btnAddEjercicio.setOnClickListener(v -> {
            // En caso de que los ejercicios disposibles esten vacios, mostramos un mensaje y sale.
            if(ejerciciosDisponibles.isEmpty()) {
                Toast.makeText(this, "Cargando ejercicios...", Toast.LENGTH_SHORT).show();
                return;
            }

            ExerciseSelectionBottomSheet bottomSheet = new ExerciseSelectionBottomSheet();

            // Pasamos la lista de ejercicios disponibles al BottomSheet.
            bottomSheet.setEjercicios(ejerciciosDisponibles);

            // Establecemos el listener para cuando se seleccione un ejercicio.
            bottomSheet.setListener(ejercicioSeleccionado -> {
                // Una vez selecionado un ejercicio mostramos un dialogo para establecer las series y repeticiones.
                showConfigureExerciseDialog(ejercicioSeleccionado);
            });

            // Mostramos el BottomSheet.
            bottomSheet.show(getSupportFragmentManager(), "ExerciseSheet");
        });

        // Establecemos el listener para cuando se elimine un ejercicio de la rutina.
        rutinaEjercicioAdapter.setOnDeleteListener(ejercicio -> {
            rutinaViewModel.deleteEjercioFromRutina(ejercicio);
            Toast.makeText(this, "Ejercicio eliminado", android.widget.Toast.LENGTH_SHORT).show();
            ejercicioCount--;
            // TODO: Hacer que el orden este bien
        });
    }

    /**
     * Muestra un dialogo para establecer las series y repeticiones de un ejercicio.
     * @param ejercicio El ejercicio seleccionado, para establecer las series y repeticiones.
     */
    private void showConfigureExerciseDialog(Ejercicio ejercicio) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_configure_exercise, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvConfigExerciseName);
        TextInputEditText etSets = dialogView.findViewById(R.id.etSets);
        TextInputEditText etReps = dialogView.findViewById(R.id.etReps);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelConfig);
        MaterialButton btnAdd = dialogView.findViewById(R.id.btnAddConfig);

        tvTitle.setText(ejercicio.getNombre());

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setBackground(new ColorDrawable(Color.TRANSPARENT))
                .setCancelable(false)
                .create();

        // Configuramos los listeners de los botones.
        // Botón de cancelar.
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Botón de añadir.
        btnAdd.setOnClickListener(v -> {
            String setsStr = etSets.getText().toString().trim();
            String repsStr = etReps.getText().toString().trim();

            if (setsStr.isEmpty() || repsStr.isEmpty()) {
                Toast.makeText(this, "Rellena ambos campos", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            int numSets = Integer.parseInt(setsStr);
            int numReps = Integer.parseInt(repsStr);

            guardarEjercicioEnRutina(ejercicio.getId(), numSets, numReps);

            dialog.dismiss();
            Toast.makeText(this, "Ejercicio añadido", android.widget.Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    /**
     * Guarda un ejercicio en una rutina.
     * @param ejercicioId El id del ejercio selecionado.
     * @param numSets El numero de series que se eligió.
     * @param numReps El numero de repeticiones que se eligió.
     */
    private void guardarEjercicioEnRutina(long ejercicioId, int numSets, int numReps) {
        int nuevoOrden = ejercicioCount + 1; // Orden del nuevo ejercicio
        int ejId = (int) ejercicioId;
        RutinaEjercicio rutinaEjercicio = new RutinaEjercicio(rutinaId, ejId, numSets, numReps, "", nuevoOrden);
        rutinaViewModel.insertRutinaEjercicio(rutinaEjercicio, () -> {
            // Aqui mandamos ese mensaje para cuando se inserte correctamente la rutinaEjercicio y se cree las series correspondientes
            Toast.makeText(this, "Ejercicio añadido con sus correspondientes series y repeticiones!", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Inicializa los ViewModels y configura los observadores de LiveData.
     * Cualquier cambio en la base de datos se reflejará automáticamente en la interfaz de usuario.
     */
    private void setupViewModel() {
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        ejercicioViewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);

        // Observa los detalles de la rutina seleccionada desde la base de datos.
        // Se actualiza automáticamente si los datos cambian.
        rutinaViewModel.getRutinaById(rutinaId).observe(this,rutina -> {
            if(rutina != null) {
                tvRoutineName.setText(rutina.getNombre());
                tvRoutineDesc.setText(rutina.getDescripcion());
                tvRoutineDate.setText("Creada: " + rutina.getFechaCreacion());

                // Aqui se implementaria la logica de obtener el total de ejercicios y series
            }
        });

        // Observa la lista de ejercicios disponibles.
        ejercicioViewModel.getAllEjercicios().observe(this, ejercicios -> {
            if (ejercicios != null) {
                ejerciciosDisponibles = ejercicios;
            }
        });

        // Observa la lista de ejercicios de la rutina seleccionada.
        // Actualiza el contador visual de ejercicios en la cabecera.
        rutinaViewModel.getEjerciciosDeRutina(rutinaId).observe(this, lista -> {
            if(lista != null) {
                ejercicioCount = lista.size();
                tvTotalExercises.setText(String.valueOf(ejercicioCount));
            }
        });

        // Observa la lista de ejercicios de la rutina seleccionada.
        rutinaViewModel.getEjerciciosDeRutina(rutinaId).observe(this, ejercicios -> {
            if (ejercicios != null) {
                rutinaEjercicioAdapter.setEjercicios(ejercicios);
            }
        });
    }

    /**
     * Inicializa los componentes de la interfaz de usuario.
     */
    private void initComponents() {
        tvRoutineName = findViewById(R.id.tvRoutineName);
        tvTotalExercises = findViewById(R.id.tvTotalExercises);
        tvTotalSets = findViewById(R.id.tvTotalSets);
        tvRoutineDesc = findViewById(R.id.tvRoutineDesc);
        tvRoutineDate = findViewById(R.id.tvRoutineDate);
        btnBack = findViewById(R.id.btnBack);
        btnAddEjercicio = findViewById(R.id.btnAddEjercicio);

        rvEjerciciosRutina = findViewById(R.id.rvEjerciciosRutina);
        rvEjerciciosRutina.setLayoutManager(new LinearLayoutManager(this));
        rutinaEjercicioAdapter = new RutinaEjercicioAdapter();
        rvEjerciciosRutina.setAdapter(rutinaEjercicioAdapter);
    }
}