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

import java.util.List;

@Dao
public interface SesionesDao {
    // OPERACIONES BÁSICAS (CRUD)

    // Insertar una nueva sesión
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Sesion sesion);

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
}
