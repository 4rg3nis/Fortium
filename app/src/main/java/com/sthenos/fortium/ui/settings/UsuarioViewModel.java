package com.sthenos.fortium.ui.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.data.repository.UsuarioRepository;
import com.sthenos.fortium.model.dto.ExportData;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.model.enums.UnidadMedida;
import com.sthenos.fortium.utils.JsonExporter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UsuarioViewModel extends AndroidViewModel {

    private final UsuarioRepository usuarioRepository;

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public UsuarioViewModel(@NonNull Application application) {
        super(application);
        usuarioRepository = UsuarioRepository.getInstance(application);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void guardarUsuario(Usuario usuario){
        usuarioRepository.insert(usuario);
    }

    public LiveData<Usuario> getUsuarioActual(){
        return usuarioRepository.getUsuarioActual();
    }

    public void updateUsuario(Usuario usuarioActual) {
        usuarioRepository.update(usuarioActual);
    }

    /**
     * Calcula la edad a partir de la fecha de nacimiento. Obtiene la fecha de hoy y la fecha de nacimiento se resta a la de hoy.
     * @param usuario
     * @return Edad en años de la persona.
     */
    public int calcularEdad(Usuario usuario) {
        if (usuario == null || usuario.getFechaNacimiento() == null) {
            return 0; // Evita el crash si el objeto no ha cargado
        }
        String fechaNacimiento = usuario.getFechaNacimiento();
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaNac = LocalDate.parse(fechaNacimiento, formato);
        return Period.between(fechaNac, fechaActual).getYears();
    }

    public void marcarPerfilComoCreado() {
        SharedPreferences prefs = getApplication().getSharedPreferences("FortiumApp", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("perfilCreado", true).apply();
    }

    /**
     * Ejecuta la exportación de la base de datos.
     *
     * @param uri Ruta del archivo donde se exportará la base de datos.
     */
    public void ejecutarExportacionBackupCompleto(Uri uri, Consumer<Boolean> callback) {
        executorService.execute(() -> {
            boolean exito = false;
            try {
                // Obtener la instancia de tu base de datos
                FortiumDatabase db = FortiumDatabase.getInstance(getApplication());

                // Extraer todos los datos de forma síncrona

                ExportData backupData = new ExportData(
                        db.usuariosDao().getUsuario(),
                        db.ejerciciosDao().getAllEjerciciosSync(),
                        db.rutinasDao().getAllExport(),
                        db.sesionesDao().getAllSesionesSync(),
                        db.seriesDao().getAllSeriesSync(),
                        db.rutinasEjerciciosDao().getAllSync()
                );

                exito = JsonExporter.exportarAJson(getApplication(), uri, backupData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Devolver respuesta al hilo principal
            final boolean resultado = exito;
            mainHandler.post(() -> callback.accept(resultado));
        });
    }

    /**
     * Cerramos el ExecutorService para evitar fugas de memoria (Memory Leaks).
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Registra un nuevo usuario.
     * @param nombre Nombre del usuario.
     * @param apellidos Apellidos del usuario.
     * @param fechaNacimiento Fecha de nacimiento.
     * @param peso Peso del usuario.
     * @param altura Altura del usuario.
     * @param genero Género del usuario.
     * @param unidad Unidad de medida.
     * @param onSuccess Callback que se ejecuta cuando se haya registrado el usuario.
     */
    public void registrarNuevoUsuario(String nombre, String apellidos, String fechaNacimiento,
                                      double peso, double altura, Genero genero, UnidadMedida unidad,
                                      Runnable onSuccess) {
        Usuario nuevoUsuario = new Usuario(nombre, apellidos, fechaNacimiento, peso, altura, genero, unidad);
        guardarUsuario(nuevoUsuario);

        marcarPerfilComoCreado();

        onSuccess.run();
    }

    /**
     * Actualiza el perfil del usuario.
     * @param usuarioExistente Usuario actual.
     * @param pesoStr Peso en String.
     * @param alturaStr Altura en String.
     * @param generoTexto Género en String.
     * @param onSuccess Callback que se ejecuta cuando se haya actualizado el perfil.
     * @param onError Callback que se ejecuta cuando haya un error.
     */
    public void actualizarPerfilUsuario(Usuario usuarioExistente, String pesoStr, String alturaStr, String generoTexto, Runnable onSuccess, Consumer<String> onError) {
        if (usuarioExistente == null) {
            onError.accept("Error: Usuario no cargado");
            return;
        }

        try {
            double peso = Double.parseDouble(pesoStr);
            double altura = Double.parseDouble(alturaStr);

            Genero generoSeleccionado = Genero.Otros;
            if (generoTexto.equals("Masculino")) generoSeleccionado = Genero.Masculino;
            else if (generoTexto.equals("Femenino")) generoSeleccionado = Genero.Femenino;

            usuarioExistente.setPesoActual(peso);
            usuarioExistente.setAltura(altura);
            usuarioExistente.setGenero(generoSeleccionado);

            updateUsuario(usuarioExistente);
            onSuccess.run();

        } catch (NumberFormatException e) {
            onError.accept("Por favor, introduce números válidos para peso y altura");
        }
    }

    /**
     * Calcula la edad a partir de una cadena de fecha.
     * @param fechaString Fecha en formato dd/MM/yyyy
     * @return Edad en años
     */
    public int calcularEdadDesdeString(String fechaString) {
        try {
            LocalDate fechaActual = LocalDate.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaNac = LocalDate.parse(fechaString, formato);
            return Period.between(fechaNac, fechaActual).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
}