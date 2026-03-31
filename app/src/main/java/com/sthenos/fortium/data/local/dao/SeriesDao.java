package com.sthenos.fortium.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Serie;

import java.util.List;

@Dao
public interface SeriesDao {
    // OPERACIONES BÁSICAS (CRUD)

    // Insertar una nueva serie, en caso de que exista la reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Serie serie);

    // Actualizar los datos de una serie existente
    @Update
    void update(Serie serie);

    // Eliminar una serie de la base de datos
    @Delete
    void delete(Serie serie);

    // CONSULTAS PERSONALIZADAS (QUERIES)

    // Obtener todas las series registradas
    @Query("SELECT * FROM Series")
    List<Serie> getAll();

    // Obtener una serie específica por su ID
    @Query("SELECT * FROM Series WHERE id = :id LIMIT 1")
    Serie getById(int id);

    // Obtener todas las series de una sesión específica
    @Query("SELECT * FROM Series WHERE sesionId = :sesionId")
    List<Serie> getBySesionId(int sesionId);

    // Obtener todas las series de un ejercicio específico
    @Query("SELECT * FROM Series WHERE ejercicioId = :ejercicioId")
    List<Serie> getByEjercicioId(int ejercicioId);

}
