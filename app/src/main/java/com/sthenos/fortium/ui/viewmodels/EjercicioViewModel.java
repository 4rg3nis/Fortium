package com.sthenos.fortium.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.repository.EjercicioRepository;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.Usuario;

import java.util.List;

public class EjercicioViewModel extends AndroidViewModel {

    private final EjercicioRepository repository;

    public EjercicioViewModel(@NonNull Application application) {
        super(application);
        repository = EjercicioRepository.getInstance(application);
    }

    public LiveData<List<Ejercicio>> getAllEjercicios() {
        return repository.getAllEjercicios();
    }

    public void insertEjercicio(Ejercicio ejercicio) {
        repository.insertEjercicio(ejercicio);
    }

    public void updateEjercicio(Ejercicio ejercicioAEditar) {
        repository.updateEjercicio(ejercicioAEditar);
    }

    public LiveData<Ejercicio> getEjercicioById(int id) {
        return repository.getEjercicioById(id);
    }

    public void deleteEjercicio(Ejercicio ejercicio) {
        repository.deleteEjercicio(ejercicio);
    }
}
