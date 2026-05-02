package com.sthenos.fortium.ui.viewmodels;

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
import com.sthenos.fortium.model.queries.RutinaExportData;
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
}