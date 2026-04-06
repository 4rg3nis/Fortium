package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.model.enums.TipoMedida;

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

    public Ejercicio(int id, String nombre, String grupoMuscularPrincipal, boolean esPredefinido, String descripcionTecnica, TipoMedida tipoMedida, Equipo equipo, boolean unilateral, String urlMultimedia, String imagenPath, boolean favorito) {
        this.id = id;
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

    @Ignore
    public Ejercicio(String nombre, String grupoMuscularPrincipal, boolean esPredefinido, String descripcionTecnica, TipoMedida tipoMedida, Equipo equipo, boolean unilateral, String urlMultimedia, String imagenPath, boolean favorito) {
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

    public TipoMedida getTipoMedida() {
        return tipoMedida;
    }

    public void setTipoMedida(TipoMedida tipoMedida) {
        this.tipoMedida = tipoMedida;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public boolean isUnilateral() {
        return unilateral;
    }

    public void setUnilateral(boolean unilateral) {
        this.unilateral = unilateral;
    }

    public String getUrlMultimedia() {
        return urlMultimedia;
    }

    public void setUrlMultimedia(String urlMultimedia) {
        this.urlMultimedia = urlMultimedia;
    }

    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}
