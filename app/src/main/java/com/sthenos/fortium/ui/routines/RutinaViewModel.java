package com.sthenos.fortium.ui.routines;

import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.sthenos.fortium.data.repository.RutinaRepository;
import com.sthenos.fortium.model.queries.EjercicioConDetalles;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
import com.sthenos.fortium.model.dto.RutinaExportData;
import com.sthenos.fortium.model.queries.RutinaResumen;
import com.sthenos.fortium.utils.JsonExporter;
import com.sthenos.fortium.utils.JsonImporter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RutinaViewModel extends AndroidViewModel {
    private final RutinaRepository repository;

    private final ExecutorService executorService;

    private final Handler mainHandler;

    public RutinaViewModel(@NonNull Application application) {
        super(application);
        repository = RutinaRepository.getInstance(application);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void insert(Rutina rutina) {
        repository.insert(rutina);
    }

    public LiveData<Rutina> getRutinaById(int id) {
        return repository.getRutinaById(id);
    }

    public void insert(Rutina rutina, RutinaRepository.OnRutinaCreadaListener listener) {
        repository.insert(rutina, listener);
    }

    public void insertRutinaEjercicio(RutinaEjercicio rutinaEjercicio, Runnable onSuccess){
        repository.insertRutinaEjercicio(rutinaEjercicio, onSuccess);
    }

    public LiveData<List<EjercicioConDetalles>> getEjerciciosDeRutina(int rutinaId) {
        return repository.getEjerciciosDeRutina(rutinaId);
    }

    public void deleteEjercioFromRutina(RutinaEjercicio rutinaEjercicio){
        repository.deleteEjercioFromRutina(rutinaEjercicio);
    }

    public LiveData<List<RutinaResumen>> getRutinasConResumen() {
        return repository.getRutinasConResumen();
    }

    public void deleteRutina(int rutinaId) {
        repository.deleteRutina(rutinaId);
    }

    /**
     * Genera un JSON con los datos de una rutina. Se ejecuta en un hilo separado.
     * @param rutina La rutina a exportar.
     * @param onJsonReady Callback que se ejecuta cuando se haya generado el JSON.
     */
    public void generarJsonDeRutina(Rutina rutina, Consumer<String> onJsonReady) {
        new Thread(() -> {
            List<RutinaEjercicio> listaEjercicios = repository.getEjercicioRutinaExport(rutina.getId());

            RutinaExportData exportData = new RutinaExportData();
            exportData.rutina = rutina;
            exportData.ejercicios = listaEjercicios;

            String jsonFinal = new Gson().toJson(exportData);

            // Volvemos al hilo principal para avisar a la pantalla
            mainHandler.post(() -> {
                onJsonReady.accept(jsonFinal);
            });
        }).start();
    }

    /**
     * Importa una rutina desde un JSON. Se ejecuta en un hilo separado.
     * @param jsonString El contenido del archivo JSON.
     * @param onSuccess Callback que se ejecuta cuando se haya completado la importación.
     * @param onError Callback que se ejecuta en caso de error.
     */
    public void importarRutinaFromJson(String jsonString, Runnable onSuccess, Consumer<String> onError) {
        executorService.execute(() -> {
            try {
                RutinaExportData data = new Gson().fromJson(jsonString, RutinaExportData.class);

                if (data == null || data.rutina == null || data.ejercicios == null) {
                    throw new Exception("El archivo no tiene el formato correcto.");
                }

                // Reseteamos los IDs para no sobreescribir datos existentes
                data.rutina.setId(0);

                // Guardamos la rutina y guardamos el nuevo id que nos da Room
                long nuevoIdRutina = repository.insertarRutinaExport(data.rutina);

                // Asignamos ese nuevo id a todos los ejercicios y reseteamos sus propios ids
                for (RutinaEjercicio ejercicio : data.ejercicios) {
                    ejercicio.setId(0); // Para que Room cree una fila nueva
                    ejercicio.setRutinaId((int) nuevoIdRutina); // Lo enlazamos a la nueva rutina
                }

                // Guardamos todos los ejercicios de golpe
                repository.insertRutinaEjercicioExport(data.ejercicios);

                mainHandler.post(onSuccess);
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> onError.accept(e.getMessage()));
            }
        });
    }

    /**
     * Exporta una rutina a un archivo que elija el usuario con el explorador de archivos.
     * @param uri Ruta del archivo donde se exportará la rutina.
     * @param jsonExportar JSON a exportar.
     * @param callback Callback que se ejecuta cuando se haya completado la exportación.
     */
    public void exportarRutinaArchivo(Uri uri, String jsonExportar, Consumer<Boolean> callback) {
        executorService.execute(() -> {
            boolean exito = JsonExporter.exportarStringAJson(getApplication().getApplicationContext(), uri, jsonExportar);
            mainHandler.post(() -> callback.accept(exito));
        });
    }

    /**
     * Importa una rutina desde un archivo que elija el usuario con el explorador de archivos.
     * @param uri Ruta del archivo que contiene la rutina.
     * @param onSuccess Callback que se ejecuta cuando se haya completado la importación.
     * @param onError Callback que se ejecuta en caso de error.
     */
    public void importarRutinaUri(Uri uri, Runnable onSuccess, Consumer<String> onError) {
        executorService.execute(() -> {
            String jsonLeido = JsonImporter.leerArchivoComoString(getApplication(), uri);

            mainHandler.post(() -> {
                if (jsonLeido != null && !jsonLeido.isEmpty()) {
                    importarRutinaFromJson(jsonLeido, onSuccess, onError);
                } else {
                    onError.accept("No se pudo leer el archivo JSON o está vacío.");
                }
            });
        });
    }

    public void crearYGuardarRutina(String titulo, String descripcion, RutinaRepository.OnRutinaCreadaListener listener) {
        String fechaHoy = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Rutina nuevaRutina = new Rutina(titulo, descripcion, fechaHoy);
        repository.insert(nuevaRutina, listener);
    }

    public void addEjercicioARutina(int rutinaId, long ejercicioId, int numSets, int numReps, int ejercicioCountActual, Runnable onSuccess) {
        int nuevoOrden = ejercicioCountActual + 1;
        RutinaEjercicio rutinaEjercicio = new RutinaEjercicio(rutinaId, (int) ejercicioId, numSets, numReps, nuevoOrden);
        repository.insertRutinaEjercicio(rutinaEjercicio, onSuccess);
    }
}