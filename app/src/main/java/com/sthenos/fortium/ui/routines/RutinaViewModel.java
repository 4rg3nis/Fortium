package com.sthenos.fortium.ui.routines;

import android.app.Application;
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

import java.util.List;
import java.util.function.Consumer;

public class RutinaViewModel extends AndroidViewModel {
    private final RutinaRepository repository;
    private final LiveData<List<Rutina>> allRutinas;

    public RutinaViewModel(@NonNull Application application) {
        super(application);
        repository = RutinaRepository.getInstance(application);
        allRutinas = repository.getRutinas();
    }

    public void insert(Rutina rutina) {
        repository.insert(rutina);
    }

    public LiveData<List<Rutina>> getAllRutinas() {
        return allRutinas;
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

            // Convertimos a JSON
            String jsonFinal = new Gson().toJson(exportData);

            // Volvemos al hilo principal para avisar a la pantalla
            new Handler(Looper.getMainLooper()).post(() -> {
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
        new Thread(() -> {
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
                for (com.sthenos.fortium.model.entities.RutinaEjercicio ejercicio : data.ejercicios) {
                    ejercicio.setId(0); // Para que Room cree una fila nueva
                    ejercicio.setRutinaId((int) nuevoIdRutina); // Lo enlazamos a la nueva rutina
                }

                // Guardamos todos los ejercicios de golpe
                repository.insertRutinaEjercicioExport(data.ejercicios);

                new Handler(Looper.getMainLooper()).post(onSuccess);
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> onError.accept(e.getMessage()));
            }
        }).start();
    }

}