package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity
public class Rutinas {
    @PrimaryKey(autoGenerate=true)
    private int uid;

    @ColumnInfo(name="usuarioId")
    private int usuarioId;

    @ColumnInfo(name="nombre")
    @NonNull
    private String nombre;

    @ColumnInfo(name="descripcion")
    private String descripcion;

    @ColumnInfo(name="fechaCreacion")
    private String fechaCreacion;

    public Rutinas(int uid, int usuarioId, String nombre, String descripcion, String fechaCreacion) {
        this.uid = uid;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    public Rutinas(int usuarioId, String nombre, String descripcion, String fechaCreacion) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    public Rutinas(int usuarioId, String nombre, String descripcion) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = now();
    }

    private String now(){
        return LocalDate.now().toString();
    }
}
