package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.EjercicioMusculosSecundario;

import java.util.List;

@Dao
public interface EjercicioMusculosSecundariosDao {

    // OPERACIONES BÁSICAS (CRUD)

    // Inserta un nuevo musculo secundario para un ejercicio. Si ya existe uno con el mismo ID, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EjercicioMusculosSecundario ejercicioMusculosSecundario);

    // Permite insertar una lista completa de musculos secundarios.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<EjercicioMusculosSecundario> ejercicioMusculosSecundario);

    // Actualiza los datos de un musculo secundario existente.
    @Update
    void update(EjercicioMusculosSecundario ejercicioMusculosSecundario);

    // Elimina un musculo secundario de la base de datos
    @Delete
    void delete(EjercicioMusculosSecundario ejercicioMusculosSecundario);

    // CONSULTAS PERSONALIZADAS (QUERIES)

    // Obtiene todos los musculos secundarios registrados
    @Query("SELECT * FROM EjercicioMusculosSecundarios")
    LiveData<List<Ejercicio>> getAll();

    // Busca un musculo secundario específico por su ID único
    @Query("SELECT * FROM EjercicioMusculosSecundarios WHERE id = :id LIMIT 1")
    Ejercicio getById(int id);


}
