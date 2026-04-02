package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Ejercicio;

import java.util.List;

@Dao
public interface EjerciciosDao {
    // OPERACIONES BÁSICAS (CRUD)

    // Inserta un nuevo ejercicio. Si ya existe uno con el mismo ID, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ejercicio ejercicio);

    // Permite insertar una lista completa de ejercicios (útil para la carga inicial)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Ejercicio> ejercicios);

    // Actualiza los datos de un ejercicio existente
    @Update
    void update(Ejercicio ejercicio);

    // Elimina un ejercicio de la base de datos
    @Delete
    void delete(Ejercicio ejercicio);

    // CONSULTAS PERSONALIZADAS (QUERIES)

    // Obtiene todos los ejercicios registrados
    @Query("SELECT * FROM Ejercicios")
    LiveData<List<Ejercicio>> getAll();

    // Busca un ejercicio específico por su ID único
    @Query("SELECT * FROM Ejercicios WHERE id = :id LIMIT 1")
    Ejercicio getById(int id);

    // Obtiene todos los ejercicios marcados como favoritos (Room guarda los booleanos como 1 o 0)
    @Query("SELECT * FROM Ejercicios WHERE favorito = 1")
    LiveData<List<Ejercicio>> getFavoritos();

    // Filtra los ejercicios por un grupo muscular específico
    @Query("SELECT * FROM Ejercicios WHERE grupoMuscularPrincipal = :grupoMuscular")
    LiveData<List<Ejercicio>> getByGrupoMuscular(String grupoMuscular);

    // Filtra para obtener solo los ejercicios predefinidos del sistema
    @Query("SELECT * FROM Ejercicios WHERE esPredefinido = 1")
    LiveData<List<Ejercicio>> getPredefinidos();
}
