package com.sthenos.fortium.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.repository.EjercicioRepository;
import com.sthenos.fortium.model.entities.Ejercicio;

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

}
