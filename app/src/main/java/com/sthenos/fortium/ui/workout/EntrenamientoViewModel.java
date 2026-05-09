package com.sthenos.fortium.ui.workout;

import android.app.Application;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sthenos.fortium.data.repository.EntrenamientoRepository;
import com.sthenos.fortium.domain.calculators.Calculador1RM;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.model.queries.DistribucionMuscular;
import com.sthenos.fortium.model.queries.EjercicioConDetalles;
import com.sthenos.fortium.model.queries.HistorialSesion;
import com.sthenos.fortium.model.queries.Progreso1RM;
import com.sthenos.fortium.model.queries.ProgresoVolumen;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.queries.SerieHistorial;
import com.sthenos.fortium.utils.Converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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

    private final List<Serie> seriesCompletadasEnVivo = new ArrayList<>();
    private final MutableLiveData<Double> volumenEnVivo = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> cantidadSeriesEnVivo = new MutableLiveData<>(0);

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
     * Finaliza la sesión y guarda el entrenamiento.
     * @param rutinaId ID de la rutina
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param notas Notas de la sesión
     * @param seriesCompletadas Series realizadas
     * @param onSuccess Callback para manejar el resultado de la inserción.
     */
    public void finalizarYGuardarSesion(Integer rutinaId, String fechaInicio, String fechaFin,
                                        String notas, List<Serie> seriesCompletadas, Runnable onSuccess) {

        double volumenFinal = 0.0;
        for (Serie s : seriesCompletadas) {
            volumenFinal += (s.getPeso() * s.getRepeticiones());
        }

        Sesion sesionHoy = new Sesion(
                rutinaId,
                fechaInicio,
                fechaFin,
                seriesCompletadas.size(),
                volumenFinal,
                notas
        );

        guardarEntrenamientoCompleto(sesionHoy, seriesCompletadas, onSuccess);
    }

    /**
     * Calcula los puntos de la gráfica aplicando la fórmula científica del 1RM.
     * @param progresos Lista de progresos de 1RM
     * @param usuario Usuario
     * @param edadUsuario Edad del usuario
     * @param callback Callback para manejar el resultado.
     */
    public void procesarDatosGrafica1RM(List<Progreso1RM> progresos,Usuario usuario, int edadUsuario, Consumer<Map<String, Object>> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Float> valoresY = new ArrayList<>();
            List<String> etiquetasX = new ArrayList<>();

            if (usuario != null && progresos != null) {
                String genero = Converters.fromGenero(usuario.getGenero());

                for (Progreso1RM p : progresos) {
                    // Aplicamos la fórmula científica a cada punto de la gráfica
                    double valorCientifico = Calculador1RM.calcular1RMFinal(p.pesoMaximo, p.reps, p.rpe, genero, edadUsuario);
                    valoresY.add((float) valorCientifico);

                    String fechaSegura = p.fecha;
                    if (fechaSegura != null && fechaSegura.length() >= 10) {
                        fechaSegura = fechaSegura.substring(0, 10);
                    }
                    etiquetasX.add(fechaSegura != null ? fechaSegura : "");
                }
            }

            // Empaquetamos y mandamos a la UI
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("valoresY", valoresY);
            resultado.put("etiquetasX", etiquetasX);

            new Handler(Looper.getMainLooper()).post(() -> callback.accept(resultado));
        });
    }

    public LiveData<Double> getVolumenEnVivo() { return volumenEnVivo; }
    public LiveData<Integer> getCantidadSeriesEnVivo() { return cantidadSeriesEnVivo; }
    public List<Serie> getSeriesCompletadasEnVivo() { return seriesCompletadasEnVivo; }

    /**
     * Registra una serie completada.
     * @param ejercicioId ID del ejercicio
     * @param peso Peso
     * @param reps Repeticiones
     * @param rpe
     * @param nota Notas
     * @param tiempoDescanso Tiempo de descanso
     */
    public void registrarSerieCompletada(int ejercicioId, float peso, int reps, float rpe, String nota, int tiempoDescanso) {
        int ordenActual = seriesCompletadasEnVivo.size() + 1;
        Serie nuevaSerie = new Serie(0, ejercicioId, peso, reps, rpe, nota, tiempoDescanso, ordenActual);
        seriesCompletadasEnVivo.add(nuevaSerie);
        recalcularEstadisticasEnVivo();
    }

    /**
     * Recalcula las estadísticas en vivo.
     */
    private void recalcularEstadisticasEnVivo() {
        double volumenTotal = 0;
        for (Serie s : seriesCompletadasEnVivo) {
            volumenTotal += (s.getPeso() * s.getRepeticiones());
        }
        volumenEnVivo.setValue(volumenTotal);
        cantidadSeriesEnVivo.setValue(seriesCompletadasEnVivo.size());
    }

    /**
     * Construye un ejercicio extra para la plantilla.
     * @param ejercicioSeleccionado Ejercicio seleccionado
     * @param rutinaId ID de la rutina
     * @param ordenActual Orden actual
     * @return Ejercicio extra
     */
    public EjercicioConDetalles construirEjercicioExtra(Ejercicio ejercicioSeleccionado, int rutinaId, int ordenActual) {
        EjercicioConDetalles ejercicioExtra = new EjercicioConDetalles();
        ejercicioExtra.ejercicio = ejercicioSeleccionado;
        ejercicioExtra.rutinaEjercicio = new RutinaEjercicio(rutinaId, ejercicioSeleccionado.getId(), 1, 0, ordenActual);
        return ejercicioExtra;
    }

    /**
     * Desmarca una serie completada.
     * @param ejercicioId ID del ejercicio
     * @param peso Peso
     * @param reps Repeticiones
     */
    public void desmarcarSerie(int ejercicioId, float peso, int reps) {
        //Buscamos desde el final hacia el principio para borrar solo la última serie que coincida,
        // evitando borrar múltiples series idénticas de golpe.
        for (int i = seriesCompletadasEnVivo.size() - 1; i >= 0; i--) {
            Serie s = seriesCompletadasEnVivo.get(i);
            if (s.getEjercicioId() == ejercicioId && s.getPeso() == peso && s.getRepeticiones() == reps) {
                seriesCompletadasEnVivo.remove(i);
                break;
            }
        }
        recalcularEstadisticasEnVivo();
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