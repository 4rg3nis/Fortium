package com.sthenos.fortium.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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

    public void insert(Rutina rutina, OnRutinaCreadaListener listener) {
        executorService.execute(() -> {
            // Guardamos en Room.
            long idLong = rutinasDao.insert(rutina);
            int idGenerado = (int) idLong;

            // Volvemos al Hilo Principal (Main Thread) para que la UI pueda viajar a la nueva Activity
            new Handler(Looper.getMainLooper()).post(() -> {
                if (listener != null && idGenerado > 0) {
                    Log.d("RutinaRepository", "Guardado correctamente");
                    listener.onSuccess(idGenerado);
                } else {
                    Log.e("RutinaRepository", "Error al insertar la rutina en la base de datos");
                }
            });

        });
    }

    public void insert(Rutina rutina) {
        executorService.execute(() -> rutinasDao.insert(rutina));
    }

    public LiveData<Rutina> getRutinaById(int id) {
        return rutinasDao.getById(id);
    }

    public interface OnRutinaCreadaListener {
        void onSuccess(int rutinaId);
    }


}
