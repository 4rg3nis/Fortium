package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Rutina;

import java.util.List;

@Dao
public interface RutinasDao {
    // OPERACIONES BÁSICAS (CRUD)

    // Inserta una nueva rutina. Si ya existe una con el mismo ID, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Rutina rutina);

    // Permite insertar una lista completa de rutinas (útil para la carga inicial).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Rutina> rutina);

    // Actualiza los datos de una rutina existente.
    @Update
    void update(Rutina rutina);


    // Elimina una rutina de la base de datos
    @Delete
    void delete(Rutina rutina);

    // CONSULTAS PERSONALIZADAS (QUERIES)

    // Obtiene todas las rutinas registradas.
    @Query("SELECT * FROM Rutinas")
    LiveData<List<Rutina>> getAll();

    // Busca una rutina específica por su ID único.
    @Query("SELECT * FROM Rutinas WHERE id = :id LIMIT 1")
    LiveData<Rutina> getById(int id);



}
