package com.sthenos.fortium.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.sthenos.fortium.data.repository.EntrenamientoRepository;
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
}
