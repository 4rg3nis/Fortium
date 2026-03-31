package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "RutinaEjercicios")
public class RutinaEjercicio {
    @PrimaryKey(autoGenerate=true)
    private int id;

    @ColumnInfo(name="rutinaId")
    private int rutinaId;

    @ColumnInfo(name="ejercicioId")
    private int ejercicioId;

    @ColumnInfo(name="orden")
    @NonNull
    private int orden;

    public RutinaEjercicio(int id, int rutinaId, int ejercicioId, int orden) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.ejercicioId = ejercicioId;
        this.orden = orden;
    }

    public RutinaEjercicio(int rutinaId, int ejercicioId, int orden) {
        this.rutinaId = rutinaId;
        this.ejercicioId = ejercicioId;
        this.orden = orden;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRutinaId() {
        return rutinaId;
    }

    public void setRutinaId(int rutinaId) {
        this.rutinaId = rutinaId;
    }

    public int getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(int ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
