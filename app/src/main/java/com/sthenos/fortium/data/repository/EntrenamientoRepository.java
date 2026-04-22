package com.sthenos.fortium.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.data.local.dao.SeriesDao;
import com.sthenos.fortium.data.local.dao.SesionesDao;
import com.sthenos.fortium.model.Resource;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntrenamientoRepository {
    private final SeriesDao seriesDao;
    private final SesionesDao sesionesDao;
    private final ExecutorService executorService;

    private static volatile EntrenamientoRepository instance;

    private EntrenamientoRepository(Application application){
        FortiumDatabase db = FortiumDatabase.getInstance(application);
        seriesDao = db.seriesDao();
        sesionesDao = db.sesionesDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public static EntrenamientoRepository getInstance(Application application){
        if(instance == null){
            synchronized (EntrenamientoRepository.class){
                if(instance == null)
                    instance = new EntrenamientoRepository(application);
            }
        }
        return instance;
    }

    /**
     * Calcula el 1RM actual de un ejercicio. Utiliza la fórmula de Epley.
     * @param peso Peso del ejercicio.
     * @param reps Número de repeticiones del ejercicio.
     * @return El peso máximo estimado para 1 repetición del ejercicio.
     */
    public double calcular1RM(double peso, int reps) {
        if (reps <= 0 || peso <= 0) {
            return 0.0;
        }
        if (reps == 1) {
            return peso; // Si es 1 repetición, ese es su 1RM actual
        }

        // Aplicación de la fórmula de Epley
        return peso * (1.0 + (reps / 30.0));
    }

    public LiveData<List<Serie>> getSeriesDeSesion(int sesionId) {
        return seriesDao.getBySesionId(sesionId);
    }

    public void insertSerie(Serie serie) {
        // Validación de datos antes de insertar en BBDD
        if (serie.getPeso() < 0 || serie.getRepeticiones() < 0) {
            throw new IllegalArgumentException("El peso y las repeticiones no pueden ser negativos.");
        }

        executorService.execute(() -> {
            seriesDao.insert(serie);
        });
    }

    public void updateSerie(Serie serie) {
        executorService.execute(() -> {
            seriesDao.update(serie);
        });
    }

    public void deleteSesion(Sesion sesion) {
        executorService.execute(() -> {
            // SQLite borrará todas sus series automáticamente.
            sesionesDao.delete(sesion);
        });
    }

    public interface RepositoryCallback<T> {
        void onComplete(Resource<T> result);
    }

    public void insertSesion(Sesion sesion, RepositoryCallback<Void> callback) {
        // Notificamos que empezamos a "cargar"
        callback.onComplete(Resource.loading(null));

        executorService.execute(() -> {
            try {
                // Intentamos insertar en Room
                sesionesDao.insert(sesion);

                // Respuesta consistente de Éxito
                callback.onComplete(Resource.success(null));

            } catch (android.database.sqlite.SQLiteConstraintException e) {
                // Manejo de error específico (ej. llave foránea rota)
                callback.onComplete(Resource.error("Error de integridad en base de datos: " + e.getMessage(), null));
            } catch (Exception e) {
                // Manejo de error genérico
                callback.onComplete(Resource.error("Error al guardar la sesión: " + e.getMessage(), null));
            }
        });
    }


}
