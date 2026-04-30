package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.queries.HistorialSesion;

import java.util.List;

@Dao
public interface SesionesDao {
    // OPERACIONES BÁSICAS (CRUD)

    // Insertar una nueva sesión
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Sesion sesion);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Sesion> sesiones);

    // Actualizar los datos de una sesion existente
    @Update
    void update(Sesion sesion);

    // Eliminar una sesion de la base de datos
    @Delete
    void delete(Sesion sesion);

    // CONSULTAS PERSONALIZADAS (QUERIES)

    // Obtener todas las sesiones registradas
    @Query("SELECT * FROM Sesiones")
    LiveData<List<Sesion>> getAll();

    // Obtener una sesión específica por su ID
    @Query("SELECT * FROM Sesiones WHERE id = :id LIMIT 1")
    Sesion getById(int id);

    // Obtener todas las sesiones de una rutina específica
    @Query("SELECT * FROM Sesiones")
    List<Sesion> getAllSesionesSync();

    // Obtener el historial de sesiones recientes, incluyendo el nombre de la rutina.
    // Se hace un left join para un futuro de entrenamientos libre sin rutia. (Entrenamiento rapido)
    @Query("SELECT Sesiones.id AS sesionId, Rutinas.nombre AS nombreRutina, " +
            "Sesiones.fechaInicio, Sesiones.cantidadSeries, Sesiones.volumenTotal, Sesiones.notas " +
            "FROM Sesiones LEFT JOIN Rutinas ON Sesiones.rutinaId = Rutinas.id " +
            "ORDER BY Sesiones.fechaInicio DESC LIMIT 5")
    LiveData<List<HistorialSesion>> getHistorialReciente();

    // Obtener el historial completo de sesiones, incluyendo el nombre de la rutina.
    @Query("SELECT Sesiones.id AS sesionId, Rutinas.nombre AS nombreRutina, " +
            "Sesiones.fechaInicio, Sesiones.cantidadSeries, Sesiones.volumenTotal, Sesiones.notas " +
            "FROM Sesiones LEFT JOIN Rutinas ON Sesiones.rutinaId = Rutinas.id " +
            "ORDER BY Sesiones.fechaInicio DESC")
    LiveData<List<HistorialSesion>> getHistorialCompleto();


    @Query("DELETE FROM Sesiones WHERE id = :sesionId")
    void deleteSesionById(int sesionId);
}
