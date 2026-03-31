package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RutinaEjercicios {
    @PrimaryKey(autoGenerate=true)
    private int id;

    @ColumnInfo(name="rutinaId")
    private int rutinaId;

    @ColumnInfo(name="ejercicioId")
    private int ejercicioId;

    @ColumnInfo(name="orden")
    @NonNull
    private int orden;

    public RutinaEjercicios(int id, int rutinaId, int ejercicioId, int orden) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.ejercicioId = ejercicioId;
        this.orden = orden;
    }

    public RutinaEjercicios(int rutinaId, int ejercicioId, int orden) {
        this.rutinaId = rutinaId;
        this.ejercicioId = ejercicioId;
        this.orden = orden;
    }
}
