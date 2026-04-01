package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(tableName = "EjercicioMusculosSecundarios", foreignKeys = @ForeignKey(
        entity = Ejercicio.class,
        parentColumns = "id",
        childColumns = "ejercicioId",
        onDelete = ForeignKey.CASCADE
))
public class EjercicioMusculosSecundario {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="ejercicioId")
    private int ejercicioId;

    @ColumnInfo(name="nombreMusculo")
    @NonNull
    private String nombreMusculo;

    public EjercicioMusculosSecundario(int uid, int ejercicioId, String nombreMusculo) {
        this.id = uid;
        this.ejercicioId = ejercicioId;
        this.nombreMusculo = nombreMusculo;
    }

    public EjercicioMusculosSecundario(int ejercicioId, String nombreMusculo) {
        this.ejercicioId = ejercicioId;
        this.nombreMusculo = nombreMusculo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(int ejercicioId) {
        this.ejercicioId = ejercicioId;
    }

    @NonNull
    public String getNombreMusculo() {
        return nombreMusculo;
    }

    public void setNombreMusculo(@NonNull String nombreMusculo) {
        this.nombreMusculo = nombreMusculo;
    }
}