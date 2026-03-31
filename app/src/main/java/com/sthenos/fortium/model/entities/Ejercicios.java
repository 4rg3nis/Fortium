package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.model.enums.TipoMedida;

@Entity
public class Ejercicios {
    @PrimaryKey(autoGenerate=true)
    private int uid;

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

    @ColumnInfo(name="tipoMedida")
    private TipoMedida tipoMedida;

    @ColumnInfo(name="equipo")
    private Equipo equipo;

    @ColumnInfo(name="unilateral")
    private boolean unilateral = false;

    @ColumnInfo(name="urlMultimedia")
    private String urlMultimedia;

    @ColumnInfo(name="imagenPath")
    private String imagenPath;

    @ColumnInfo(name="favorito")
    private boolean favorito = false;

    public Ejercicios(int uid, String nombre, String grupoMuscularPrincipal, boolean esPredefinido, String descripcionTecnica, TipoMedida tipoMedida, Equipo equipo, boolean unilateral, String urlMultimedia, String imagenPath, boolean favorito) {
        this.uid = uid;
        this.nombre = nombre;
        this.grupoMuscularPrincipal = grupoMuscularPrincipal;
        this.esPredefinido = esPredefinido;
        this.descripcionTecnica = descripcionTecnica;
        this.tipoMedida = tipoMedida;
        this.equipo = equipo;
        this.unilateral = unilateral;
        this.urlMultimedia = urlMultimedia;
        this.imagenPath = imagenPath;
        this.favorito = favorito;
    }

    public Ejercicios(String nombre, String grupoMuscularPrincipal, boolean esPredefinido, String descripcionTecnica, TipoMedida tipoMedida, Equipo equipo, boolean unilateral, String urlMultimedia, String imagenPath, boolean favorito) {
        this.nombre = nombre;
        this.grupoMuscularPrincipal = grupoMuscularPrincipal;
        this.esPredefinido = esPredefinido;
        this.descripcionTecnica = descripcionTecnica;
        this.tipoMedida = tipoMedida;
        this.equipo = equipo;
        this.unilateral = unilateral;
        this.urlMultimedia = urlMultimedia;
        this.imagenPath = imagenPath;
        this.favorito = favorito;
    }
}
