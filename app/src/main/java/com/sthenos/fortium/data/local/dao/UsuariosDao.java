package com.sthenos.fortium.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sthenos.fortium.model.entities.Usuario;

import java.util.List;

@Dao
public interface UsuariosDao {
    // OPERACIONES BÁSICAS (CRUD).

    // Inserta un nuevo usuario.
    @ Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Usuario usuario);

    // Actualiza los datos del usuario existente.
    @Update
    void update(Usuario usuario);

    // Elimina un usuario de la base de datos.
    @Delete
    void delete(Usuario usuario);

    // CONSULTAS PERSONALIZADAS (QUERIES).

    // Obtiene al usuario.
    @Query("SELECT * FROM Usuarios WHERE id = :id LIMIT 1")
    Usuario getById(int id);

    // Obtiene todos los usuarios registrados.
    @Query("SELECT * FROM Usuarios")
    LiveData<List<Usuario>> getAll(); // en principio no usaré esta.

    // Borra todos los usuarios de la base de datos.
    @Query("DELETE FROM Usuarios")
    void deleteAll();

    @Query("SELECT * FROM Usuarios LIMIT 1")
    LiveData<Usuario> getUsuarioActual();
}
