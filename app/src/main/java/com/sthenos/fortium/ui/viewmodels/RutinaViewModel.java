package com.sthenos.fortium.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.repository.RutinaRepository;
import com.sthenos.fortium.model.entities.Rutina;

import java.util.List;

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
}
