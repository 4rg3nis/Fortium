package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.queries.EjercicioConDetalles;
import com.sthenos.fortium.model.entities.RutinaEjercicio;

import java.util.List;

@Dao
public interface RutinasEjerciciosDao {
    // Operaciones basicas

    // Inserta una nueva relación entre una rutina y un ejercicio.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RutinaEjercicio rutinaEjercicio);

    // Actualiza los datos de una relación existente.
    @Update
    void update(RutinaEjercicio rutinaEjercicio);

    // Elimina una relación de rutina-ejercicio de la base de datos.
    @Delete
    void delete(RutinaEjercicio rutinaEjercicio);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RutinaEjercicio> series);

    // Consultas personalizadas

    // Obtiene todas las relaciones de rutina-ejercicio registradas.
    @Query("SELECT * FROM RutinaEjercicios")
    LiveData<List<RutinaEjercicio>> getAll();


    @Query("SELECT * FROM RutinaEjercicios")
    List<RutinaEjercicio> getAllSync();

    // Busca una relación específica por su ID único.
    @Query("SELECT * FROM RutinaEjercicios WHERE id = :id LIMIT 1")
    RutinaEjercicio getById(int id);

    // Busca un ejercicio específico por su ID. Se usa transaction porque room tiene que hacer
    // varias consultas internas para unir dos tablas y con esto se garantiza la integridad.
    @Transaction
    @Query("SELECT * FROM RutinaEjercicios WHERE rutinaId = :rutinaId")
    LiveData<List<EjercicioConDetalles>> getEjerciciosDeRutina(int rutinaId);

    // Query para actualizar el orden de los ejercicios en una rutina, al borrar otro ejercicio.
    @Query("UPDATE RutinaEjercicios SET orden = orden - 1 WHERE rutinaId = :rutinaId AND orden > :ordenBorrado")
    void actualizarOrdenes(int rutinaId, int ordenBorrado);

    @Query("SELECT * FROM RutinaEjercicios WHERE rutinaId = :rutinaId")
    List<RutinaEjercicio> getEjercicioRutinaExport(int rutinaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRutinaEjercicioExport(List<RutinaEjercicio> ejercicios);
}
