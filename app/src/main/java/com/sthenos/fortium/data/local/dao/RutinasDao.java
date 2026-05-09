package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.queries.RutinaResumen;

import java.util.List;

@Dao
public interface RutinasDao {
    // Operaciones básicas

    // Inserta una nueva rutina. Si ya existe una con el mismo ID, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Rutina rutina);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRutinaExport(Rutina rutina);

    // Permite insertar una lista completa de rutinas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Rutina> rutina);

    // Actualiza los datos de una rutina existente.
    @Update
    void update(Rutina rutina);


    // Elimina una rutina de la base de datos
    @Delete
    void delete(Rutina rutina);

    // Consulñtas personalizadas

    // Obtiene todas las rutinas registradas.
    @Query("SELECT * FROM Rutinas")
    LiveData<List<Rutina>> getAll();

    // Busca una rutina específica por su ID único.
    @Query("SELECT * FROM Rutinas WHERE id = :id LIMIT 1")
    LiveData<Rutina> getById(int id);

    @Query("SELECT * FROM Rutinas")
    List<Rutina> getAllExport();

    /**
     * Obtiene una vista resumida de todas las rutinas.
     * La consulta realiza tres cálculos en tiempo real:
     * 1. Cuenta el total de ejercicios vinculados a cada rutina.
     * 2. Concatena los nombres únicos de los grupos musculares involucrados.
     * 3. Busca la fecha de la sesión de entrenamiento más reciente.
     *
     * @return LiveData con una lista de objetos {@link RutinaResumen}.
     */
    @Query("SELECT r.*, " +
            "(SELECT COUNT(id) FROM RutinaEjercicios WHERE rutinaId = r.id) AS totalEjercicios, " +
            "(SELECT GROUP_CONCAT(DISTINCT e.grupoMuscularPrincipal) FROM RutinaEjercicios re INNER JOIN Ejercicios e ON re.ejercicioId = e.id WHERE re.rutinaId = r.id) AS musculosInvolucrados, " +
            "(SELECT MAX(fechaInicio) FROM Sesiones WHERE rutinaId = r.id) AS ultimaVez " +
            "FROM Rutinas r")
    LiveData<List<RutinaResumen>> getRutinasConResumen();

    // Elimina una rutina específica por su ID.
    @Query("DELETE FROM Rutinas WHERE id = :id")
    void delete(int id);

}
