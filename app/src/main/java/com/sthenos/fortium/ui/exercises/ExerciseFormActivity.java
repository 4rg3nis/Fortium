package com.sthenos.fortium.ui.exercises;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.utils.Converters;
import com.sthenos.fortium.utils.ImageUtils;

public class ExerciseFormActivity extends AppCompatActivity {

    private TextInputEditText etName, etDescription;
    private AutoCompleteTextView dropMuscle, dropEquipment;
    private MaterialToolbar toolbar;
    private MaterialButton btnSave, btnUploadMedia, btnDelete;
    private ImageView ivExercisePreview;
    private EjercicioViewModel viewModel;
    private int ejercicioIdActual = -1;
    private Ejercicio ejercicioAEditar = null;
    private String currentImagePath = "";

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    // El usuario ha elegido una foto. Hacemos la copia de seguridad.
                    ImageUtils.copiarImagenAInternoAsync(this, uri, rutaSegura -> {
                        if (rutaSegura != null) {
                            currentImagePath = rutaSegura;
                            ImageUtils.cargarImagenEjercicio(this, currentImagePath, ivExercisePreview, false);
                        } else {
                            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        setListeners();
        setupDropdowns();
        getExcersiceId();
    }

    /**
     * Configuramos los dropdowns.
     */
    private void setupDropdowns() {
        String[] musculos = {"Pecho", "Espalda", "Cuadriceps","Isquiotibiales", "Glúteos", "Gemelos", "Bíceps", "Tríceps", "Hombros", "Abdominales"};
        dropMuscle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, musculos));

        String[] equipo = {"Maquina", "Polea", "Peso Corporal", "Peso libre"};
        dropEquipment.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, equipo));
    }

    /**
     * Obtenemos el id del ejercicio que queremos editar. En caso de que sea -1 significa que estamos creando un nuevo ejercicio.
     * En caso de que sea distinto de -1 significa que estamos editando un ejercicio existente.
     */
    private void getExcersiceId() {
        ejercicioIdActual = getIntent().getIntExtra("ejercicioId", -1);

        if (ejercicioIdActual != -1) {
            // Modo edición
            toolbar.setTitle("Editar Ejercicio");
            btnSave.setText("Actualizar Cambios");
            cargarDatosParaEditar(ejercicioIdActual);
        } else {
            // Modo creación
            toolbar.setTitle("Nuevo Ejercicio");
            btnSave.setText("Crear Ejercicio");
        }
    }

    /**
     * Cargamos los datos del ejercicio que queremos editar. En caso de que sea un ejercicio del sistema no se permitirá ni el borrado ni la edición.
     * @param id
     */
    private void cargarDatosParaEditar(int id) {
        // Le pedimos a Room que nos busque este ejercicio
        viewModel.getEjercicioById(id).observe(this, ejercicio -> {
            if (ejercicio != null) {
                this.ejercicioAEditar = ejercicio;

                currentImagePath = ejercicio.getImagenPath();

                ImageUtils.cargarImagenEjercicio(this, ejercicio.getImagenPath(), ivExercisePreview, false);

                // Rellenamos los campos automáticamente
                etName.setText(ejercicio.getNombre());
                dropMuscle.setText(ejercicio.getGrupoMuscularPrincipal());
                etDescription.setText(ejercicio.getDescripcionTecnica());

                String equipo = ejercicio.getEquipo().toString();
                String equipoFormateado = equipo.replace("_", " ").toLowerCase();
                dropEquipment.setText(equipoFormateado);

                if (ejercicio.isEsPredefinido()) {
                    activarModoSoloLectura();
                } else {
                    // Si es un ejercicio creado por el usuario, mostramos el botón de borrar
                    btnDelete.setVisibility(View.VISIBLE);
                }

                // Reseteamos los adaptadores para que no se bloqueen
                setupDropdowns();
            }
        });
    }

    /**
     * Configura los listeners.
     */
    private void setListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String nombre = etName.getText().toString().trim();
            String musculo = dropMuscle.getText().toString().trim();
            String descripcion = etDescription.getText().toString().trim();
            String equipo = dropEquipment.getText().toString().trim();

            viewModel.guardarEjercicio(
                    ejercicioIdActual,
                    ejercicioAEditar,
                    nombre,
                    musculo,
                    descripcion,
                    equipo,
                    currentImagePath,
                    () -> {
                        Toast.makeText(this, "Ejercicio guardado con éxito", Toast.LENGTH_SHORT).show();
                        finish(); // Cerramos la pantalla
                    },
                    (mensajeError) -> {
                        Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
                    }
            );
        });

        btnUploadMedia.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        btnDelete.setOnClickListener(v -> {
            // Creamos un diálogo de advertencia nativo de Material Design
            new MaterialAlertDialogBuilder(this)
                    .setTitle("¿Eliminar " + ejercicioAEditar.getNombre() + "?")
                    .setMessage("Esta acción no se puede deshacer. ¿Estás seguro?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {

                        viewModel.deleteEjercicio(ejercicioAEditar);

                        Toast.makeText(this, "Ejercicio eliminado", Toast.LENGTH_SHORT).show();
                        finish(); // Cerramos la pantalla
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    /**
     * Inicializa los componentes.
     */
    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        dropMuscle = findViewById(R.id.dropMuscle);
        dropEquipment = findViewById(R.id.dropEquipment);
        btnSave = findViewById(R.id.btnSave);
        btnUploadMedia = findViewById(R.id.btnUploadMedia);
        ivExercisePreview = findViewById(R.id.ivExercisePreview);
        btnDelete = findViewById(R.id.btnDelete);

        viewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);
    }

    /**
     * Activa el modo solo lectura. Esto solo es para los ejercicios que son del sistema. No se podrán ni modificar ni borrar.
     */
    private void activarModoSoloLectura() {
        // Cambiamos el título para que el usuario entienda qué pasa
        toolbar.setTitle("Detalle del Ejercicio");
        toolbar.setSubtitle("Ejercicio del sistema (No modificable)");

        // Bloqueamos los inputs
        etName.setEnabled(false);
        dropMuscle.setEnabled(false);
        dropEquipment.setEnabled(false);
        etDescription.setEnabled(false);

        // Escondemos los botones de acción
        btnUploadMedia.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
    }
}