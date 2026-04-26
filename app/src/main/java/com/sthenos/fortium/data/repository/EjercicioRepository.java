package com.sthenos.fortium.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.data.local.dao.EjercicioMusculosSecundariosDao;
import com.sthenos.fortium.data.local.dao.EjerciciosDao;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.EjercicioMusculosSecundario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EjercicioRepository {

    private static volatile EjercicioRepository instance;

    private final EjerciciosDao ejerciciosDao;
    private final EjercicioMusculosSecundariosDao ejercicioMusculosSecundariosDao;
    private final LiveData<List<Ejercicio>> allEjercicios;
    private final ExecutorService executorService;

    private EjercicioRepository(Application application) {
        FortiumDatabase db = FortiumDatabase.getInstance(application);
        ejerciciosDao = db.ejerciciosDao();
        ejercicioMusculosSecundariosDao = db.ejercicioMusculosSecundariosDao();
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

    public void updateEjercicio(Ejercicio ejercicio) throws UnsupportedOperationException {
        if (ejercicio.isEsPredefinido()) {
            throw new UnsupportedOperationException("Seguridad: No se pueden modificar los ejercicios predefinidos.");
        }
        executorService.execute(() -> {
            ejerciciosDao.update(ejercicio);
        });
    }

    public void deleteEjercicio(Ejercicio ejercicio) throws UnsupportedOperationException {
        executorService.execute(() -> {
            ejerciciosDao.delete(ejercicio);
        });
    }

    public LiveData<Ejercicio> getEjercicioById(int id) {
        return ejerciciosDao.getById(id);
    }
}
