package com.sthenos.fortium.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.data.local.dao.RutinasDao;
import com.sthenos.fortium.data.local.dao.RutinasEjerciciosDao;
import com.sthenos.fortium.model.entities.Rutina;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RutinaRepository {
    private final RutinasDao rutinasDao;
    private final RutinasEjerciciosDao rutinasEjerciciosDao;
    private final ExecutorService executorService;

    private static volatile RutinaRepository instance;

    private RutinaRepository(Application application){
        FortiumDatabase db = FortiumDatabase.getInstance(application);
        rutinasDao = db.rutinasDao();
        rutinasEjerciciosDao = db.rutinasEjerciciosDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public static RutinaRepository getInstance(Application application){
        if(instance == null){
            synchronized (RutinaRepository.class){
                if(instance == null){
                    instance = new RutinaRepository(application);
                }
            }
        }
        return instance;
    }

    public LiveData<List<Rutina>> getRutinas() {
        return rutinasDao.getAll();
    }

    public void insert(Rutina rutina) {
        executorService.execute(() -> rutinasDao.insert(rutina));
    }
}
