package com.sthenos.fortium.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sthenos.fortium.model.enums.TipoSerie;

@Entity(tableName = "Series")
public class Serie {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="sesionId")
    private int sesionId;

    @ColumnInfo(name="ejercicioId")
    private int ejercicioId;

    @ColumnInfo(name="peso")
    private int peso; // REAL???????????

    @ColumnInfo(name="repeticiones")
    private int repeticiones;

    @ColumnInfo(name="rpe_rir")
    private int rpeRir; // REAL???????????

    @ColumnInfo(name="tipoSerie")
    private TipoSerie tipoSerie;

    @ColumnInfo(name="tiempoDescanso")
    private int tiempoDescanso; // Segundos

    @ColumnInfo(name="ordenSesiontSerie")
    private int ordenSesiontSerie;

    public Serie(int id, int sesionId, int ejercicioId, int peso, int repeticiones, int rpeRir, TipoSerie tipoSerie, int tiempoDescanso, int ordenSesiontSerie) {
        this.id = id;
        this.sesionId = sesionId;
        this.ejercicioId = ejercicioId;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.rpeRir = rpeRir;
        this.tipoSerie = tipoSerie;
        this.tiempoDescanso = tiempoDescanso;
        this.ordenSesiontSerie = ordenSesiontSerie;
    }

    public Serie(int sesionId, int ejercicioId, int peso, int repeticiones, int rpeRir, TipoSerie tipoSerie, int tiempoDescanso, int ordenSesiontSerie) {
        this.sesionId = sesionId;
        this.ejercicioId = ejercicioId;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.rpeRir = rpeRir;
        this.tipoSerie = tipoSerie;
        this.tiempoDescanso = tiempoDescanso;
        this.ordenSesiontSerie = ordenSesiontSerie;
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

    public int getPeso() {
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

    public int getRpeRir() {
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

    public int getOrdenSesiontSerie() {
        return ordenSesiontSerie;
    }

    public void setOrdenSesiontSerie(int ordenSesiontSerie) {
        this.ordenSesiontSerie = ordenSesiontSerie;
    }
}
