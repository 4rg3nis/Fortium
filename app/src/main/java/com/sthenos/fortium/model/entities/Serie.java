package com.sthenos.fortium.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sthenos.fortium.model.enums.TipoSerie;

@Entity(tableName = "Series", foreignKeys = {
        @ForeignKey(
                entity = Sesion.class,
                parentColumns = "id",
                childColumns = "sesionId",
                onDelete = ForeignKey.CASCADE)
        , @ForeignKey(
                entity = Ejercicio.class,
                parentColumns = "id",
                childColumns = "ejercicioId",
                onDelete = ForeignKey.CASCADE)
})
public class Serie {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="sesionId")
    private int sesionId;

    @ColumnInfo(name="ejercicioId")
    private int ejercicioId;

    @ColumnInfo(name="peso")
    private float peso;

    @ColumnInfo(name="repeticiones")
    private int repeticiones;

    @ColumnInfo(name="rpe_rir")
    private float rpeRir;

    @ColumnInfo(name="tipoSerie")
    private TipoSerie tipoSerie;

    @ColumnInfo(name="tiempoDescanso")
    private int tiempoDescanso; // Segundos

    @ColumnInfo(name="ordenEnSesion")
    private int ordenEnSesion;

    public Serie(int id, int sesionId, int ejercicioId, float peso, int repeticiones, float rpeRir, TipoSerie tipoSerie, int tiempoDescanso, int ordenEnSesion) {
        this.id = id;
        this.sesionId = sesionId;
        this.ejercicioId = ejercicioId;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.rpeRir = rpeRir;
        this.tipoSerie = tipoSerie;
        this.tiempoDescanso = tiempoDescanso;
        this.ordenEnSesion = ordenEnSesion;
    }

    @Ignore
    public Serie(int sesionId, int ejercicioId, float peso, int repeticiones, float rpeRir, TipoSerie tipoSerie, int tiempoDescanso, int ordenEnSesion) {
        this.sesionId = sesionId;
        this.ejercicioId = ejercicioId;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.rpeRir = rpeRir;
        this.tipoSerie = tipoSerie;
        this.tiempoDescanso = tiempoDescanso;
        this.ordenEnSesion = ordenEnSesion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSesionId() {
        return sesionId;
    }

    public void setSesionId(int sesionId) {
        this.sesionId = sesionId;
    }

    public int getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(int ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public float getRpeRir() {
        return rpeRir;
    }

    public void setRpeRir(int rpeRir) {
        this.rpeRir = rpeRir;
    }

    public TipoSerie getTipoSerie() {
        return tipoSerie;
    }

    public void setTipoSerie(TipoSerie tipoSerie) {
        this.tipoSerie = tipoSerie;
    }

    public int getTiempoDescanso() {
        return tiempoDescanso;
    }

    public void setTiempoDescanso(int tiempoDescanso) {
        this.tiempoDescanso = tiempoDescanso;
    }

    public int getOrdenEnSesion() {
        return ordenEnSesion;
    }

    public void setOrdenEnSesion(int ordenEnSesion) {
        this.ordenEnSesion = ordenEnSesion;
    }
}
