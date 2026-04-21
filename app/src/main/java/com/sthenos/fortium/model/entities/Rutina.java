package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

public class Rutina {
    @PrimaryKey(autoGenerate=true)
    private int id;

    @ColumnInfo(name="nombre")
    @NonNull
    private String nombre;

    @ColumnInfo(name="descripcion")
    private String descripcion;

    @ColumnInfo(name="fechaCreacion")
    private String fechaCreacion;

    public Rutina(int id,String nombre, String descripcion, String fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    @Ignore
    public Rutina(String nombre, String descripcion, String fechaCreacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    @Ignore
    public Rutina(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = now();
    }

    private String now(){
        return LocalDate.now().toString();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
