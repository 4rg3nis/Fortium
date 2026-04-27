package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.DistribucionMuscular;
import com.sthenos.fortium.model.entities.Progreso1RM;
import com.sthenos.fortium.model.entities.ProgresoVolumen;
import com.sthenos.fortium.model.entities.Serie;

import java.util.List;

@Dao
public interface SeriesDao {
    // OPERACIONES BÁSICAS (CRUD)

    // Insertar una nueva serie, en caso de que exista la reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Serie serie);

    @Insert
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

    @Query("SELECT Sesiones.fechaInicio AS fecha, MAX(Series.peso) AS pesoMaximo, Series.repeticiones AS reps, Series.rpe_rir AS rpe " +
            "FROM Series " +
            "INNER JOIN Sesiones ON Series.sesionId = Sesiones.id " +
            "WHERE Series.ejercicioId = :ejercicioId " +
            "GROUP BY Sesiones.id " +
            "ORDER BY Sesiones.fechaInicio ASC")
    LiveData<List<Progreso1RM>> getProgresion1RM(int ejercicioId);

    @Query("SELECT Sesiones.fechaInicio as fecha, SUM(Series.peso * Series.repeticiones) as totalVolumen " +
            "FROM Series INNER JOIN Sesiones ON Series.sesionId = Sesiones.id " +
            "GROUP BY Sesiones.id ORDER BY Sesiones.fechaInicio DESC LIMIT 7")
    LiveData<List<ProgresoVolumen>> getUltimas7SesionesVolumen();

    @Query("SELECT Ejercicios.grupoMuscularPrincipal AS musculo, COUNT(Series.id) AS cantidadSeries " +
            "FROM Series " +
            "INNER JOIN Ejercicios ON Series.ejercicioId = Ejercicios.id " +
            "INNER JOIN Sesiones ON Series.sesionId = Sesiones.id " +
            "WHERE Sesiones.fechaInicio >= :fechaHace30Dias " +
            "GROUP BY Ejercicios.grupoMuscularPrincipal")
    LiveData<List<DistribucionMuscular>> getDistribucionMuscular30Dias(String fechaHace30Dias);
}
