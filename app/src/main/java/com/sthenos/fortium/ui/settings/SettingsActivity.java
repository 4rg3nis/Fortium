package com.sthenos.fortium.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.ui.MainActivity;
import com.sthenos.fortium.data.workers.AutoBackupWorker;
import com.sthenos.fortium.utils.Converters;
import com.sthenos.fortium.utils.JsonImporter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etHeight, etAge;
    private AutoCompleteTextView dropGender;
    private MaterialButton btnSaveProfile, btnExport, btnImport, btnLegalNotice;
    private MaterialToolbar toolbar;

    private UsuarioViewModel usuarioViewModel;
    private Usuario usuarioActual;

    private MaterialSwitch switchAutoBackup;
    private ActivityResultLauncher<Intent> exploradorArchivosLauncher;

    // Lanzador para guardar el archivo (Exportar)
    private ActivityResultLauncher<String> exportLauncher;

    // Lanzador para abrir un archivo (Importar)
    private ActivityResultLauncher<String[]> importLauncher;

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
        setupBackups();
    }

    /**
     * Configura la lógica de respaldos: exportación manual, importación
     * y programación de copias automáticas semanales.
     */
    private void setupBackups() {
        SharedPreferences prefs = getSharedPreferences("FortiumApp", MODE_PRIVATE);

        // Lanzador para crear un nuevo archivo JSON (Exportar)
        exportLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> {
            if (uri != null) {
                Toast.makeText(this, "Generando copia de seguridad...", Toast.LENGTH_SHORT).show();
                usuarioViewModel.ejecutarExportacionBackupCompleto(uri, exito -> {
                    if (isDestroyed()) return;
                    if (exito) {
                        Toast.makeText(SettingsActivity.this, "¡Copia de seguridad guardada con éxito!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Error al escribir el archivo JSON.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Lanzador para abrir un archivo JSON y restaurar la base de datos (Importar)
        importLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                Toast.makeText(this, "Importando datos", Toast.LENGTH_LONG).show();

                JsonImporter.ejecutarImportacionCompleta(this, uri, new JsonImporter.ImportCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Datos restaurados", Toast.LENGTH_LONG).show();

                            // Reiniciamos la app para aplicar cambios
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        switchAutoBackup.setChecked(prefs.getBoolean("autoBackupEnabled", false));

        // Lógica del interruptor de Backup Automático
        switchAutoBackup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si es la primera vez, pedimos al usuario elegir carpeta
                if (!prefs.contains("backupFolderUri")) {
                    // Abre el selector del sistema para que el usuario elija la carpeta de destino del backup
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    exploradorArchivosLauncher.launch(intent);
                } else {
                    // Si ya tenía carpeta de antes, solo reactivamos el trabajo
                    prefs.edit().putBoolean("autoBackupEnabled", true).apply();
                    programarTrabajadorBackup();
                    Toast.makeText(this, "Copias reactivadas", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Si lo apaga, cancelamos
                WorkManager.getInstance(this).cancelUniqueWork("FortiumWeeklyBackup");
                prefs.edit().putBoolean("autoBackupEnabled", false).apply();
                Toast.makeText(this, "Copias desactivadas", Toast.LENGTH_SHORT).show();
            }
        });

        // Manejador del explorador para obtener permisos persistentes sobre una carpeta
        exploradorArchivosLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(), result -> {
                    // Si el usuario selecciona una carpeta, guardamos su URI
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uriCarpeta = result.getData().getData();
                        if (uriCarpeta != null) {
                            // Solicita permiso permanente de lectura/escritura en la carpeta elegida
                            getContentResolver().takePersistableUriPermission(uriCarpeta,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                            // Guardamos la URI en nuestros ajustes
                            prefs.edit()
                                    .putString("backupFolderUri", uriCarpeta.toString())
                                    .putBoolean("autoBackupEnabled", true)
                                    .apply();

                            // Programamos el trabajo de fondo
                            programarTrabajadorBackup();
                            Toast.makeText(this, "Copias semanales activadas", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Si el usuario cancela y no elige carpeta, apagamos el interruptor
                        switchAutoBackup.setChecked(false);
                    }
                }
        );
    }

    /**
     * Registra una tarea programada en WorkManager para ejecutarse
     * cada 7 días, siempre que haya batería suficiente.
     */
    private void programarTrabajadorBackup() {
        PeriodicWorkRequest backupRequest = new PeriodicWorkRequest.Builder(
                AutoBackupWorker.class, 7, TimeUnit.DAYS)
                .setConstraints(new Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()) // Restricción de batería no baja
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "FortiumWeeklyBackup", // Nombre
                ExistingPeriodicWorkPolicy.KEEP, // KEEP para no resetear el ciclo de 7 días cada vez
                backupRequest // La tarea programada que queremos ejecutar en segundo plano
        );
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
            String pesoStr = etWeight.getText().toString();
            String alturaStr = etHeight.getText().toString();
            String generoTexto = dropGender.getText().toString();

            usuarioViewModel.actualizarPerfilUsuario(
                    usuarioActual,
                    pesoStr,
                    alturaStr,
                    generoTexto,
                    () -> Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show(),
                    mensajeError -> Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show()
            );
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

        btnLegalNotice.setOnClickListener(v -> mostrarAvisoLegal());
    }

    /**
     * Muestra un diálogo emergente con el aviso legal de la aplicación.
     */
    private void mostrarAvisoLegal() {
        // Usamos Html.fromHtml para que las etiquetas <b> (negritas) se pinten correctamente
        CharSequence mensajeFormateado = Html.fromHtml(
                getString(R.string.legal_notice_body),
                Html.FROM_HTML_MODE_LEGACY
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.legal_notice_title)
                .setMessage(mensajeFormateado)
                .setPositiveButton("Entendido", null)
                .show();
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

            int nuevaEdad = usuarioViewModel.calcularEdadDesdeString(dateString);
            etAge.setText(String.valueOf(nuevaEdad));
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
        switchAutoBackup = findViewById(R.id.switchAutoBackup);

        btnLegalNotice = findViewById(R.id.btnLegalNotice);

        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
    }
}
