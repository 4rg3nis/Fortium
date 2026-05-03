package com.sthenos.fortium.ui.workout;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sthenos.fortium.data.repository.EntrenamientoRepository;
import com.sthenos.fortium.model.queries.DistribucionMuscular;
import com.sthenos.fortium.model.queries.EjercicioConDetalles;
import com.sthenos.fortium.model.queries.HistorialSesion;
import com.sthenos.fortium.model.queries.Progreso1RM;
import com.sthenos.fortium.model.queries.ProgresoVolumen;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.queries.SerieHistorial;

import java.util.List;
import java.util.Map;

/**
 * ViewModel para la actividad de entrenamiento.
 * @author Argenis
 */
public class EntrenamientoViewModel extends AndroidViewModel {

    private CountDownTimer restTimer;

    // LiveData modificables para controlar el estado del cronómetro desde el ViewModel
    private final MutableLiveData<Long> tiempoRestante = new MutableLiveData<>();
    private final MutableLiveData<Boolean> timerActivo = new MutableLiveData<>(false);
    private long tiempoActualMilis = 0;
    private final MutableLiveData<Boolean> timerFinalizado = new MutableLiveData<>(false);

    private EntrenamientoRepository repository;

    public EntrenamientoViewModel(@NonNull Application application) {
        super(application);
        repository = EntrenamientoRepository.getInstance(application);
    }

    /**
     * Guarda un entrenamiento completo.
     * @param nuevaSesion La nueva sesión a guardar.
     * @param seriesRealizadas Las series realizadas en esta sesión.
     * @param onSuccess Callback para manejar el resultado de la inserción.
     */
    public void guardarEntrenamientoCompleto(Sesion nuevaSesion, List<Serie> seriesRealizadas, Runnable onSuccess) {
        repository.guardarEntrenamientoCompleto(nuevaSesion, seriesRealizadas, onSuccess);
    }

    public LiveData<List<Progreso1RM>> getProgresion1RM(int ejercicioId) {
        return repository.getProgresion1RM(ejercicioId);
    }

    public  LiveData<List<DistribucionMuscular>> getDistribucionMuscular30Dias(String fecha) {
        return repository.getDistribucionMuscular30Dias( fecha);
    }

    public LiveData<List<ProgresoVolumen>> getUltimas7SesionesVolumen() {
        return repository.getUltimas7SesionesVolumen();
    }

    public LiveData<List<HistorialSesion>> getHistorialReciente() {
        return repository.getHistorialReciente();
    }

    public LiveData<List<HistorialSesion>> getHistorialCompleto() {
        return repository.getHistorialCompleto();
    }

    public void eliminarSesionCompleta(int sesionId) {
        repository.eliminarSesionCompleta(sesionId);
    }

    public LiveData<Sesion> getSesionById(int sesionId) {
        return repository.getSesionById(sesionId);
    }

    public LiveData<List<SerieHistorial>> getSeriesDeSesion(int sesionId) {
        return repository.getSeriesDeSesion(sesionId);
    }

    public LiveData<Map<Integer, List<String>>> getRecords(List<EjercicioConDetalles> ejercicios) {
        MutableLiveData<Map<Integer, List<String>>> recordsLiveData = new MutableLiveData<>();

        repository.buscarRecordsDeEjercicios(ejercicios, records -> {
            // Usamos postValue() porque estamos en un hilo secundario.
            // postValue se encarga automáticamente de mandarlo al hilo principal de la UI de forma segura.
            recordsLiveData.postValue(records);
        });

        return recordsLiveData;
    }

    /**
     * Inicializa el temporizador, en segundos.
     * @param segundos Los segundos que durará el temporizador.
     */
    public void iniciarTemporizador(int segundos) {
        if (restTimer != null) restTimer.cancel();

        tiempoActualMilis = segundos * 1000L;
        timerActivo.setValue(true);

        restTimer = new CountDownTimer(tiempoActualMilis, 1000) {
            // Cada segundo, actualizamos el tiempo restante
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoActualMilis = millisUntilFinished;
                tiempoRestante.setValue(millisUntilFinished / 1000);
            }

            // Cuando el temporizador llega a 0, finalizamos.
            @Override
            public void onFinish() {
                timerActivo.setValue(false);
                tiempoRestante.setValue(0L);
                timerFinalizado.setValue(true);
            }
        }.start();
    }

    public void ajustarTemporizador(int segundosExtra) {
        // Sumamos o restamos 15s al tiempo actual
        long nuevoTiempoSegundos = (tiempoActualMilis / 1000) + segundosExtra;
        if (nuevoTiempoSegundos < 0) nuevoTiempoSegundos = 0;
        iniciarTemporizador((int) nuevoTiempoSegundos);
    }

    public void cancelarTemporizador() {
        if (restTimer != null) {
            restTimer.cancel();
            timerActivo.setValue(false);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Cuando la pantalla se cierra y el ViewModel muere, matamos el cronómetro
        if (restTimer != null) {
            restTimer.cancel();
        }
    }

    /**
     * Resetea el estado del temporizador finalizado.
     */
    public void resetTimerFinalizado() {
        timerFinalizado.setValue(false);
    }

    public LiveData<Long> getTiempoRestante() { return tiempoRestante; }
    public LiveData<Boolean> getTimerActivo() { return timerActivo; }

    public LiveData<Boolean> getTimerFinalizado() { return timerFinalizado; }

}
