package com.sthenos.fortium.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sthenos.fortium.model.enums.TipoSerie;

@Entity
public class Series {
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

    public Series(int id, int sesionId, int ejercicioId, int peso, int repeticiones, int rpeRir, TipoSerie tipoSerie, int tiempoDescanso, int ordenSesiontSerie) {
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

    public Series(int sesionId, int ejercicioId, int peso, int repeticiones, int rpeRir, TipoSerie tipoSerie, int tiempoDescanso, int ordenSesiontSerie) {
        this.sesionId = sesionId;
        this.ejercicioId = ejercicioId;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.rpeRir = rpeRir;
        this.tipoSerie = tipoSerie;
        this.tiempoDescanso = tiempoDescanso;
        this.ordenSesiontSerie = ordenSesiontSerie;
    }
}
