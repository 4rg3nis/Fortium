package com.sthenos.fortium.ui.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.ui.viewmodels.EjercicioViewModel;

public class ExerciseFormActivity extends AppCompatActivity {

    private TextInputEditText etName, etDescription;
    private AutoCompleteTextView dropMuscle, dropEquipment;
    private MaterialToolbar toolbar;
    private MaterialButton btnSave, btnUploadMedia;
    private ImageView ivExercisePreview;
    private EjercicioViewModel viewModel;
    private int ejercicioIdActual = -1;
    private Ejercicio ejercicioAEditar = null;
    private String currentImagePath = "";

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

    private void setupDropdowns() {
        String[] musculos = {"Pecho", "Espalda", "Piernas", "Hombros", "Brazos", "Core", "Cardio"};
        dropMuscle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, musculos));

        String[] equipo = {"Maquina", "Polea", "Peso Corporal", "Peso libre"};
        dropEquipment.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, equipo));
    }

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

    private void cargarDatosParaEditar(int id) {

        // Le pedimos a Room que nos busque este ejercicio
        viewModel.getEjercicioById(id).observe(this, ejercicio -> {
            if (ejercicio != null) {
                this.ejercicioAEditar = ejercicio;

                currentImagePath = ejercicio.getImagenPath();

                int imageResId = obtenerRecursoDesdeString(currentImagePath);

                Glide.with(this)
                        .load(imageResId)
                        .centerCrop()
                        .into(ivExercisePreview);

                // Rellenamos los campos automáticamente
                etName.setText(ejercicio.getNombre());
                dropMuscle.setText(ejercicio.getGrupoMuscularPrincipal());
                etDescription.setText(ejercicio.getDescripcionTecnica());
                String equipo = ejercicio.getEquipo().toString();
                String equipoFormateado = equipo.replace("_", " ").toLowerCase();
                dropEquipment.setText(equipoFormateado);

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

        // Le quitamos la extensión (".gif", ".png", etc) porque a Android no le gusta
        String nombreLimpio = nombreArchivo.replaceFirst("[.][^.]+$", "");

        // Buscamos su DNI en la carpeta drawable
        int recursoId = getResources().getIdentifier(nombreLimpio, "drawable", getPackageName());

        // Si recursoId es 0, significa que no lo encontró.
        return recursoId != 0 ? recursoId : R.drawable.ic_launcher_foreground;
    }


    private void setListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String nombre = etName.getText().toString().trim();
            String musculo = dropMuscle.getText().toString().trim();
            String descripcion = etDescription.getText().toString().trim();
            String equipo = dropEquipment.getText().toString().trim();


            // Usamos la variable global, NO el toString() del ImageView
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

                    Ejercicio nuevoEjercicio = new Ejercicio(nombre, musculo, false, descripcion, equipoEnum, imagenPathAGuardar);

                    viewModel.insertEjercicio(nuevoEjercicio);
                    Toast.makeText(this, "Ejercicio creado", Toast.LENGTH_SHORT).show();
                }

                finish(); // Cerramos la pantalla
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Equipo no válido", Toast.LENGTH_SHORT).show();
            }


        });


        // Aquí en el futuro abriremos la galería del móvil
        btnUploadMedia.setOnClickListener(v -> {
            Toast.makeText(this, "Próximamente: Abrir galería", Toast.LENGTH_SHORT).show();
        });
    }

    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        dropMuscle = findViewById(R.id.dropMuscle);
        dropEquipment = findViewById(R.id.dropEquipment);
        btnSave = findViewById(R.id.btnSave);
        btnUploadMedia = findViewById(R.id.btnUploadMedia);
        ivExercisePreview = findViewById(R.id.ivExercisePreview);

        viewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);

    }
}