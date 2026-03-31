package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class EjercicioMusculosSecundarios {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name="ejercicioId")
    private int ejercicioId;

    @ColumnInfo(name="nombreMusculo")
    @NonNull
    private String nombreMusculo;

    public EjercicioMusculosSecundarios(int uid, int ejercicioId, String nombreMusculo) {
        this.uid = uid;
        this.ejercicioId = ejercicioId;
        this.nombreMusculo = nombreMusculo;
    }

    public EjercicioMusculosSecundarios(int ejercicioId, String nombreMusculo) {
        this.ejercicioId = ejercicioId;
        this.nombreMusculo = nombreMusculo;
    }
}