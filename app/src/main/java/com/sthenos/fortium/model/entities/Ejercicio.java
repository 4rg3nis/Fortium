package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sthenos.fortium.model.enums.Equipo;

@Entity(tableName = "Ejercicios")
public class Ejercicio {
    @PrimaryKey(autoGenerate=true)
    private int id;

    @ColumnInfo(name="nombre")
    @NonNull
    private String nombre;

    @ColumnInfo(name="grupoMuscularPrincipal")
    @NonNull
    private String grupoMuscularPrincipal;

    @ColumnInfo(name="esPredefinido")
    private boolean esPredefinido = false;

    @ColumnInfo(name="descripcionTecnica")
    private String descripcionTecnica;

    @ColumnInfo(name="equipo")
    private Equipo equipo;

    @ColumnInfo(name="imagenPath")
    private String imagenPath;

    public Ejercicio(int id, String nombre, String grupoMuscularPrincipal,  boolean esPredefinido, String descripcionTecnica, Equipo equipo, String imagenPath) {
        this.id = id;
        this.nombre = nombre;
        this.grupoMuscularPrincipal = grupoMuscularPrincipal;
        this.esPredefinido = esPredefinido;
        this.descripcionTecnica = descripcionTecnica;
        this.equipo = equipo;
        this.imagenPath = imagenPath;
    }

    @Ignore
    public Ejercicio(String nombre, String grupoMuscularPrincipal,boolean esPredefinido, String descripcionTecnica, Equipo equipo, String imagenPath) {
        this.nombre = nombre;
        this.grupoMuscularPrincipal = grupoMuscularPrincipal;
        this.esPredefinido = esPredefinido;
        this.descripcionTecnica = descripcionTecnica;
        this.equipo = equipo;
        this.imagenPath = imagenPath;
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

    @NonNull
    public String getGrupoMuscularPrincipal() {
        return grupoMuscularPrincipal;
    }

    public void setGrupoMuscularPrincipal(@NonNull String grupoMuscularPrincipal) {
        this.grupoMuscularPrincipal = grupoMuscularPrincipal;
    }

    public boolean isEsPredefinido() {
        return esPredefinido;
    }

    public void setEsPredefinido(boolean esPredefinido) {
        this.esPredefinido = esPredefinido;
    }

    public String getDescripcionTecnica() {
        return descripcionTecnica;
    }

    public void setDescripcionTecnica(String descripcionTecnica) {
        this.descripcionTecnica = descripcionTecnica;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }
}
