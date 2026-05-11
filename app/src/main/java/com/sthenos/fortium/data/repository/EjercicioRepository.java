package com.sthenos.fortium.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.data.local.dao.EjerciciosDao;
import com.sthenos.fortium.model.entities.Ejercicio;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EjercicioRepository {

    private static volatile EjercicioRepository instance;

    private final EjerciciosDao ejerciciosDao;
    private final LiveData<List<Ejercicio>> allEjercicios;
    private final ExecutorService executorService;

    private EjercicioRepository(Application application) {
        FortiumDatabase db = FortiumDatabase.getInstance(application);
        ejerciciosDao = db.ejerciciosDao();
        allEjercicios = ejerciciosDao.getAll();
        executorService = Executors.newFixedThreadPool(2);
    }

    // Método de acceso estático (El Singleton)
    public static EjercicioRepository getInstance(Application application) {
        if (instance == null) {
            // Sincronizamos para evitar que dos hilos creen dos instancias al mismo tiempo
            synchronized (EjercicioRepository.class) {
                if (instance == null) {
                    instance = new EjercicioRepository(application);
                }
            }
        }
        return instance;
    }

    public LiveData<List<Ejercicio>> getAllEjercicios() {
        return allEjercicios;
    }

    public void insertEjercicio(Ejercicio ejercicio) {
        executorService.execute(() -> {
            ejerciciosDao.insert(ejercicio);
        });
    }

    public void updateEjercicio(Ejercicio ejercicio) {
        executorService.execute(() -> {
            ejerciciosDao.update(ejercicio);
        });
    }

    public void deleteEjercicio(Ejercicio ejercicio, Runnable onSuccess, Consumer<String> onError) {
        executorService.execute(() -> {
            try {
                ejerciciosDao.delete(ejercicio);
                new Handler(Looper.getMainLooper()).post(onSuccess);
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        onError.accept("No se puede borrar: Este ejercicio está en una rutina o ya tienes historial de entrenamiento guardado con él.")
                );
            }
        });
    }

    public LiveData<Ejercicio> getEjercicioById(int id) {
        return ejerciciosDao.getById(id);
    }
}
