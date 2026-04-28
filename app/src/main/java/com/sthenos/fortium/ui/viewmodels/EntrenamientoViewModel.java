package com.sthenos.fortium.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.repository.EntrenamientoRepository;
import com.sthenos.fortium.model.queries.DistribucionMuscular;
import com.sthenos.fortium.model.queries.Progreso1RM;
import com.sthenos.fortium.model.queries.ProgresoVolumen;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;

import java.util.List;

/**
 * ViewModel para la actividad de entrenamiento.
 * @author Argenis
 */
public class EntrenamientoViewModel extends AndroidViewModel {

    private EntrenamientoRepository repository;

    public EntrenamientoViewModel(@NonNull Application application) {
        super(application);
        repository = EntrenamientoRepository.getInstance(application);
    }

    /**
     * Guarda un entrenamiento completo.
     * @param nuevaSesion La nueva sesión a guardar.
     * @param seriesRealizadas Las series realizadas en esta sesión.
     * @param onSuccess Callback para manejar el resultado de la inserción.
     */
    public void guardarEntrenamientoCompleto(Sesion nuevaSesion, List<Serie> seriesRealizadas, Runnable onSuccess) {
        repository.guardarEntrenamientoCompleto(nuevaSesion, seriesRealizadas, onSuccess);
    }

    public LiveData<List<Progreso1RM>> getProgresion1RM(int ejercicioId) {
        return repository.getProgresion1RM(ejercicioId);
    }

    public  LiveData<List<DistribucionMuscular>> getDistribucionMuscular30Dias(String fecha) {
        return repository.getDistribucionMuscular30Dias( fecha);
    }

    public LiveData<List<ProgresoVolumen>> getUltimas7SesionesVolumen() {
        return repository.getUltimas7SesionesVolumen();
    }
}
