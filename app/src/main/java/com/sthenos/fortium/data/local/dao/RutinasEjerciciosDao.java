package com.sthenos.fortium.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.RutinaEjercicio;

import java.util.List;

@Dao
public interface RutinasEjerciciosDao {
    // OPERACIONES BÁSICAS (CRUD)

    // Inserta una nueva relación entre una rutina y un ejercicio.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RutinaEjercicio rutinaEjercicio);

    // Actualiza los datos de una relación existente.
    @Update
    void update(RutinaEjercicio rutinaEjercicio);

    // Elimina una relación de rutina-ejercicio de la base de datos.
    @Delete
    void delete(RutinaEjercicio rutinaEjercicio);

    // CONSULTAS PERSONALIZADAS (QUERIES)

    // Obtiene todas las relaciones de rutina-ejercicio registradas.
    @Query("SELECT * FROM RutinaEjercicios")
    List<RutinaEjercicio> getAll();

    // Busca una relación específica por su ID único.
    @Query("SELECT * FROM RutinaEjercicios WHERE id = :id LIMIT 1")
    RutinaEjercicio getById(int id);
}
