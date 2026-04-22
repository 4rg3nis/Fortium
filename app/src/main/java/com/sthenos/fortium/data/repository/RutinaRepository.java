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
import com.sthenos.fortium.model.entities.EjercicioConDetalles;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
import com.sthenos.fortium.model.entities.Serie;

import java.util.ArrayList;
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

    /**
     * Inserta una rutina de forma asíncrona y notifica el ID generado
     * en el hilo principal a través del listener.
     * @param rutina La rutina a guardar.
     * @param listener Callback para manejar el resultado de la inserción.
     */
    public void insert(Rutina rutina, OnRutinaCreadaListener listener) {
        executorService.execute(() -> {
            // Guardamos en Room.
            long idLong = rutinasDao.insert(rutina);
            int idGenerado = (int) idLong;

            // Notificamos al hilo principal
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


    /**
     * Vincula un ejercicio a una rutina de forma asíncrona.
     * @param rutinaEjercicio La rutinaEjercicio a guardar.
     * @param onSuccess Callback que se ejecuta en el hilo principal tras completar la inserción.
     */
    public void insertRutinaEjercicio(RutinaEjercicio rutinaEjercicio, Runnable onSuccess){
        executorService.execute(() -> {
            rutinasEjerciciosDao.insert(rutinaEjercicio);
            // Regreso al hilo principal para ejecutar la acción de éxito
            new Handler(Looper.getMainLooper()).post(() -> {
                if(onSuccess != null) onSuccess.run(); // Para volver la hilo principal.
            });
        });
    }

    public LiveData<List<EjercicioConDetalles>> getEjerciciosDeRutina(int rutinaId) {
        return rutinasEjerciciosDao.getEjerciciosDeRutina(rutinaId);
    }

    /**
     * Interfaz de callback para notificar la creación exitosa de una rutina.
     */
    public interface OnRutinaCreadaListener {
        /**
         * Se ejecuta cuando la rutina ha sido guardada, devolviendo su ID generado.
         * @param rutinaId El ID único asignado por la base de datos.
         */
        void onSuccess(int rutinaId);
    }
}
