package com.sthenos.fortium.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.model.dto.ExportData;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;
import com.sthenos.fortium.utils.Converters;
import com.sthenos.fortium.utils.JsonExporter;
import com.sthenos.fortium.utils.JsonImporter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etHeight, etAge;
    private AutoCompleteTextView dropGender;
    private MaterialButton btnSaveProfile, btnExport, btnImport;
    private MaterialToolbar toolbar;

    private UsuarioViewModel usuarioViewModel;
    private Usuario usuarioActual;

    // Lanzador para guardar el archivo (Exportar)
    private final ActivityResultLauncher<String> exportLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> {
                if (uri != null) {
                    ejecutarExportacionBackup(uri);
                }
            });

    // Lanzador para abrir un archivo (Importar)
    private final ActivityResultLauncher<String[]> importLauncher = registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.OpenDocument(), uri -> {
        if (uri != null) {
            Toast.makeText(this, "Importando datos, por favor espera...", Toast.LENGTH_LONG).show();

            JsonImporter.ejecutarImportacionCompleta(this, uri, new JsonImporter.ImportCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "¡Datos restaurados con éxito!", Toast.LENGTH_LONG).show();

                        // Reiniciamos la app para aplicar cambios
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String mensaje) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show());
                }
            });
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        setupDropdown();
        setListeners();

        cargarDatosUsuario();
    }

    /**
     * Carga los datos del usuario actual de la base de datos.
     */
    private void cargarDatosUsuario() {
        usuarioViewModel.getUsuarioActual().observe(this, usuario -> {
            if (usuario != null) {
                this.usuarioActual = usuario;
                etWeight.setText(String.valueOf(usuario.getPesoActual()));
                etHeight.setText(String.valueOf(usuario.getAltura()));
                int edad = usuarioViewModel.calcularEdad(usuario);
                etAge.setText(String.valueOf(edad));
                dropGender.setText(Converters.fromGenero(usuario.getGenero()), false);
                setupDropdown();
            }
        });
    }

    /**
     * Configura los listeners de la vista.
     */
    private void setListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());

        etAge.setOnClickListener(v -> {
            setDate();
        });

        btnSaveProfile.setOnClickListener(v -> {
            if (usuarioActual != null) {
                try {
                    double peso = Double.parseDouble(etWeight.getText().toString());
                    double altura = Double.parseDouble(etHeight.getText().toString());
                    String genero = dropGender.getText().toString();

                    usuarioActual.setPesoActual(peso);
                    usuarioActual.setAltura(altura);
                    usuarioActual.setGenero(Genero.valueOf(genero));
                    // TODO: Edad...

                    usuarioViewModel.updateUsuario(usuarioActual);
                    Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Por favor, introduce números válidos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // BOTÓN EXPORTAR
        btnExport.setOnClickListener(v -> {
            String nombreSugerido = "Fortium_Backup_" + System.currentTimeMillis() + ".json";
            exportLauncher.launch(nombreSugerido);
        });

        // BOTÓN IMPORTAR
        btnImport.setOnClickListener(v -> {
            // Mensaje para advertir al usuario.
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Restaurar copia de seguridad")
                    .setMessage("Al importar estos datos, se borrará todo tu progreso actual y se sustituirá por el del archivo. ¿Deseas continuar?")
                    .setPositiveButton("Continuar", (dialog, which) -> {
                        // Si confirma, lanzamos el selector de archivos
                        importLauncher.launch(new String[]{"application/json"});
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });


    }

    /**
     * Configura el calendario para seleccionar la fecha de nacimiento.
     */
    private void setDate() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Selecciona tu fecha");

        // Seleccionar la fecha de hoy por defecto
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        // Mostrar el calendario
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        // Escuchar cuando el usuario pulsa Aceptar
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(selection));

            usuarioActual.setFechaNacimiento(dateString);
        });
    }

    /**
     * Configura el menú desplegable para seleccionar el género.
     */
    private void setupDropdown() {
        String[] generos = {Converters.fromGenero(Genero.Masculino), Converters.fromGenero(Genero.Femenino), Converters.fromGenero(Genero.Otros)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, generos);
        dropGender.setAdapter(adapter);
    }

    /**
     * Inicializa los componentes.
     */
    private void initComponents() {
        toolbar = findViewById(R.id.toolbarSettings);
        etWeight = findViewById(R.id.etUserWeight);
        etHeight = findViewById(R.id.etUserHeight);
        etAge = findViewById(R.id.etUserAge);
        dropGender = findViewById(R.id.dropUserGender);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnExport = findViewById(R.id.btnExportJson);
        btnImport = findViewById(R.id.btnImportJson);

        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
    }

    /**
     * Ejecuta la exportación de la base de datos.
     *
     * @param uri Ruta del archivo donde se exportará la base de datos.
     */
    private void ejecutarExportacionBackup(Uri uri) {

        Toast.makeText(this, "Generando copia de seguridad...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                // Obtener la instancia de tu base de datos
                FortiumDatabase db = FortiumDatabase.getInstance(getApplicationContext());

                // Extraer todos los datos de forma síncrona
                Usuario usuario = db.usuariosDao().getUsuario();
                List<Ejercicio> ejercicios = db.ejerciciosDao().getAllEjerciciosSync();
                List<Sesion> sesiones = db.sesionesDao().getAllSesionesSync();
                List<Serie> series = db.seriesDao().getAllSeriesSync();
                List<Rutina> rutinas = db.rutinasDao().getAllExport();

                // Empaquetar todos los datos en el contenedor que creamos
                ExportData backupData = new ExportData(usuario, ejercicios, rutinas, sesiones, series);

                // Convertir a JSON y guardar en el archivo que eligió el usuario
                boolean exito = JsonExporter.exportarAJson(SettingsActivity.this, uri, backupData);

                // Volver al hilo principal para avisar al usuario
                runOnUiThread(() -> {
                    if (exito) {
                        Toast.makeText(SettingsActivity.this, "¡Copia de seguridad guardada con éxito!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Error al escribir el archivo JSON.", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(SettingsActivity.this, "Error crítico al exportar la base de datos.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

}