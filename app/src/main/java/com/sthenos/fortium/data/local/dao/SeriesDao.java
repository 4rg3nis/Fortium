package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.queries.DistribucionMuscular;
import com.sthenos.fortium.model.queries.Progreso1RM;
import com.sthenos.fortium.model.queries.ProgresoVolumen;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.queries.SerieHistorial;

import java.util.List;

@Dao
public interface SeriesDao {
    // OPERACIONES BÁSICAS (CRUD)

    // Insertar una nueva serie, en caso de que exista la reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Serie serie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Serie> series);

    // Actualizar los datos de una serie existente
    @Update
    void update(Serie serie);

    // Eliminar una serie de la base de datos
    @Delete
    void delete(Serie serie);

    // CONSULTAS PERSONALIZADAS (QUERIES)

    // Obtener todas las series registradas
    @Query("SELECT * FROM Series")
    LiveData<List<Serie>> getAll();

    // Obtener una serie específica por su ID
    @Query("SELECT * FROM Series WHERE id = :id LIMIT 1")
    Serie getById(int id);

    // Obtener todas las series de una sesión específica
    @Query("SELECT * FROM Series WHERE sesionId = :sesionId")
    LiveData<List<Serie>> getBySesionId(int sesionId);

    // Obtener todas las series de un ejercicio específico
    @Query("SELECT * FROM Series WHERE ejercicioId = :ejercicioId")
    LiveData<List<Serie>> getByEjercicioId(int ejercicioId);

    /**
     * Obtiene el historial de récords personales por sesión para un ejercicio específico.
     *
     * Esta consulta busca la serie más pesada (peso máximo) que el usuario levantó
     * en cada día de entrenamiento para ver cómo ha ido progresando su fuerza.
     *
     * @param ejercicioId El ID del ejercicio que queremos analizar.
     * @return Una lista de objetos Progreso1RM ordenados por fecha.
     */
    @Query("SELECT Sesiones.fechaInicio AS fecha, MAX(Series.peso) AS pesoMaximo, Series.repeticiones AS reps, Series.rpe_rir AS rpe " +
            "FROM Series " +
            "INNER JOIN Sesiones ON Series.sesionId = Sesiones.id " +
            "WHERE Series.ejercicioId = :ejercicioId " +
            "GROUP BY Sesiones.id " +
            "ORDER BY Sesiones.fechaInicio ASC")
    LiveData<List<Progreso1RM>> getProgresion1RM(int ejercicioId);

    /**
     * Calcula el volumen total de entrenamiento de las últimas 7 sesiones.
     *
     * El "Volumen" es la carga total levantada (Peso x Repeticiones).
     * Útil para ver la carga de trabajo acumulada en la última semana o entrenamientos.
     *
     * @return Una lista de objetos ProgresoVolumen con la fecha y el total de kilos movidos.
     */
    @Query("SELECT Sesiones.fechaInicio as fecha, SUM(Series.peso * Series.repeticiones) as totalVolumen " +
            "FROM Series INNER JOIN Sesiones ON Series.sesionId = Sesiones.id " +
            "GROUP BY Sesiones.id ORDER BY Sesiones.fechaInicio DESC LIMIT 7")
    LiveData<List<ProgresoVolumen>> getUltimas7SesionesVolumen();

    /**
     * Calcula cuántas series se han realizado para cada grupo muscular en los últimos 30 días.
     *
     * @param fechaHace30Dias La fecha exacta desde la que queremos empezar a contar.
     * @return Una lista que asocia cada grupo muscular con su número total de series.
     */
    @Query("SELECT Ejercicios.grupoMuscularPrincipal AS musculo, COUNT(Series.id) AS cantidadSeries " +
            "FROM Series " +
            "INNER JOIN Ejercicios ON Series.ejercicioId = Ejercicios.id " +
            "INNER JOIN Sesiones ON Series.sesionId = Sesiones.id " +
            "WHERE Sesiones.fechaInicio >= :fechaHace30Dias " +
            "GROUP BY Ejercicios.grupoMuscularPrincipal")
    LiveData<List<DistribucionMuscular>> getDistribucionMuscular30Dias(String fechaHace30Dias);

    @Query("SELECT * FROM Series")
    List<Serie> getAllSeriesSync();

    @Query("DELETE FROM Series WHERE sesionId = :sesionId")
    void deleteBySesion(int sesionId);

    /**
     * Obtiene el listado detallado de todas las series realizadas en una sesión específica.
     *
     * Combina los datos técnicos de la serie (peso, reps) con la información del ejercicio
     * (nombre) para mostrar un resumen entendible de lo que se hizo en ese entrenamiento.
     *
     * @param sesionId El ID único de la sesión que queremos consultar.
     * @return Una lista de objetos SerieHistorial, ordenados tal cual se hicieron.
     */
    @Query("SELECT Ejercicios.id as ejercicioId, Ejercicios.nombre as nombreEjercicio, Series.peso, Series.repeticiones, Series.ordenEnSesion " +
            "FROM Series INNER JOIN Ejercicios ON Series.ejercicioId = Ejercicios.id " +
            "WHERE Series.sesionId = :sesionId ORDER BY Series.ordenEnSesion ASC")
    LiveData<List<SerieHistorial>> getSeriesDeSesion(int sesionId);

    /**
     * Obtiene un resumen de texto (ej: "100 x 8") de todas las series realizadas
     * la última vez que se hizo un ejercicio específico.
     *
     * Es ideal para mostrarle al usuario qué hizo el último día (su récord previo)
     * justo antes de que empiece a anotar las series de hoy.
     *
     * @param ejercicioId El ID del ejercicio del que queremos recordar el historial.
     * @return Una lista de Strings formateados "Peso x Repeticiones".
     */
    @Query("SELECT Series.peso || ' x ' || Series.repeticiones " +
            "FROM Series " +
            "WHERE Series.ejercicioId = :ejercicioId AND Series.sesionId = " +
            "(SELECT Sesiones.id FROM Sesiones INNER JOIN Series AS s2 ON s2.sesionId = Sesiones.id " +
            "WHERE s2.ejercicioId = :ejercicioId ORDER BY Sesiones.fechaInicio DESC LIMIT 1) " +
            "ORDER BY Series.ordenEnSesion ASC")
    List<String> getUltimasSeriesDeEjercicio(int ejercicioId);
}
