package com.sthenos.fortium.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.data.local.dao.SeriesDao;
import com.sthenos.fortium.data.local.dao.SesionesDao;
import com.sthenos.fortium.model.Resource;
import com.sthenos.fortium.model.queries.DistribucionMuscular;
import com.sthenos.fortium.model.queries.HistorialSesion;
import com.sthenos.fortium.model.queries.Progreso1RM;
import com.sthenos.fortium.model.queries.ProgresoVolumen;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.queries.SerieHistorial;

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

   public LiveData<List<DistribucionMuscular>> getDistribucionMuscular30Dias(String fechaHace30Dias) {
        return seriesDao.getDistribucionMuscular30Dias(fechaHace30Dias);
   }

    public LiveData<List<ProgresoVolumen>> getUltimas7SesionesVolumen() {
        return seriesDao.getUltimas7SesionesVolumen();
    }

    public LiveData<List<HistorialSesion>> getHistorialReciente() {
        return sesionesDao.getHistorialReciente();
    }

    public LiveData<List<HistorialSesion>> getHistorialCompleto() {
        return sesionesDao.getHistorialCompleto();
    }

    /**
     * Elimina una sesión y sus series de la base de datos.
     * @param sesionId El ID de la sesión a eliminar.
     */
    public void eliminarSesionCompleta(int sesionId) {
        executorService.execute(() -> {
            seriesDao.deleteBySesion(sesionId);
            sesionesDao.deleteSesionById(sesionId);
        });
    }

    public LiveData<Sesion> getSesionById(int sesionId) {
        return sesionesDao.getSesionById(sesionId);
    }

    public LiveData<List<SerieHistorial>> getSeriesDeSesion(int sesionId) {
        return seriesDao.getSeriesDeSesion(sesionId);
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

    /**
     * Guarda un entrenamiento completo (Sesión). Primero lo que hace es guardar la sesión en la base de datos y optener la ID generada.
     * Luego, con la series de la lista, la modificamos una a una para añadirle el ID de la sesión.
     * Y por ultimo insertamos todas las series en la base de datos.
     * @param nuevaSesion La nueva sesión a guardar.
     * @param seriesRealizadas Las series realizadas en esta sesión.
     * @param onSuccess Callback para manejar el resultado de la inserción.
     */
    public void guardarEntrenamientoCompleto(Sesion nuevaSesion, List<Serie> seriesRealizadas, Runnable onSuccess) {
        executorService.execute(() -> {

            // Guardamos la Sesion general y recogemos su DNI (ID)
            long sesionIdGenerado = sesionesDao.insert(nuevaSesion);

            // Le pegamos este ID a TODAS las series que el usuario ha hecho hoy
            for (Serie serie : seriesRealizadas) {
                serie.setSesionId((int) sesionIdGenerado);
            }

            seriesDao.insertAll(seriesRealizadas);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (onSuccess != null) onSuccess.run();
            });
        });
    }

    public LiveData<List<Progreso1RM>> getProgresion1RM(int ejercicioId) {
        return seriesDao.getProgresion1RM(ejercicioId);
    }

}
