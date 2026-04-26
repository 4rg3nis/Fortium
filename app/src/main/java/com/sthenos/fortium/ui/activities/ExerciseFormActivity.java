package com.sthenos.fortium.ui.activities;

import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.ui.viewmodels.EjercicioViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
                    String rutaSegura = copiarImagenAInterno(uri);

                    if (rutaSegura != null) {
                        // Actualizamos nuestra variable global
                        currentImagePath = rutaSegura;

                        // Le decimos a Glide que nos muestre la foto que acabamos de copiar
                        Glide.with(this)
                                .load(currentImagePath)
                                .centerCrop()
                                .into(ivExercisePreview);
                    } else {
                        Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                    }
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

                Object fuenteDeImagen;

                if (currentImagePath != null && currentImagePath.startsWith("/")) {
                    // Es una ruta absoluta del almacenamiento interno
                    fuenteDeImagen = currentImagePath;
                } else {
                    // Es un nombre de archivo de la carpeta drawable
                    fuenteDeImagen = obtenerRecursoDesdeString(currentImagePath);
                }

                Glide.with(this)
                        .load(fuenteDeImagen)
                        .centerCrop()
                        .into(ivExercisePreview);

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
                    btnDelete.setVisibility(android.view.View.VISIBLE);
                }

                // Reseteamos los adaptadores para que no se bloqueen
                setupDropdowns();
            }
        });
    }

    /**
     * Traduce "archivo.gif" -> R.drawable.archivo
     * @param nombreArchivo Nombre del archivo.
     * @return Recurso de la imagen.
     */
    private int obtenerRecursoDesdeString(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return R.drawable.ic_launcher_foreground;
        }

        // Le quitamos la extensión
        String nombreLimpio = nombreArchivo.replaceFirst("[.][^.]+$", "");

        int recursoId = getResources().getIdentifier(nombreLimpio, "drawable", getPackageName());

        // Si recursoId es 0, significa que no lo encontró.
        return recursoId != 0 ? recursoId : R.drawable.ic_launcher_foreground;
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

            String imagenPathAGuardar = currentImagePath;

            if (nombre.isEmpty() || musculo.isEmpty()) {
                Toast.makeText(this, "Nombre y Músculo son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String equipoFormateado = equipo.replace(" ", "_").toUpperCase();
                Equipo equipoEnum = Equipo.valueOf(equipoFormateado);

                if (ejercicioIdActual != -1 && ejercicioAEditar != null) {
                    // Modo edición
                    ejercicioAEditar.setImagenPath(imagenPathAGuardar);
                    ejercicioAEditar.setNombre(nombre);
                    ejercicioAEditar.setGrupoMuscularPrincipal(musculo);
                    ejercicioAEditar.setDescripcionTecnica(descripcion);
                    ejercicioAEditar.setEquipo(equipoEnum);

                    viewModel.updateEjercicio(ejercicioAEditar);
                    Toast.makeText(this, "Ejercicio actualizado", Toast.LENGTH_SHORT).show();

                } else {
                    // Modo creación
                    Ejercicio nuevoEjercicio = new Ejercicio(nombre, musculo, false, descripcion, equipoEnum, imagenPathAGuardar);

                    viewModel.insertEjercicio(nuevoEjercicio);
                    Toast.makeText(this, "Ejercicio creado", Toast.LENGTH_SHORT).show();
                }

                finish(); // Cerramos la pantalla
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Equipo no válido", Toast.LENGTH_SHORT).show();
            }
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
     * Copia la imagen seleccionada a la carpeta interna del dispositivo.
     * @param uri Uri de la imagen.
     * @return Ruta de la imagen copiada.
     */
    private String copiarImagenAInterno(Uri uri) {
        try {
            // Copiamos la imagen a la carpeta interna del dispositivo
            InputStream inputStream = getContentResolver().openInputStream(uri);

            String extension = getContentResolver().getType(uri);
            extension = extension != null ? extension.split("/")[1] : "jpg";

            // Creamos un nombre único basado en la fecha/hora
            String fileName = "img_ejercicio_" + System.currentTimeMillis() + "." + extension;
            File archivoDestino = new File(getFilesDir(), fileName);

            FileOutputStream outputStream = new FileOutputStream(archivoDestino);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return archivoDestino.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        btnUploadMedia.setVisibility(android.view.View.GONE);
        btnSave.setVisibility(android.view.View.GONE);
        btnDelete.setVisibility(android.view.View.GONE);
    }
}