package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "RutinaEjercicios", foreignKeys = {@ForeignKey(
        entity = Rutina.class,
        parentColumns = "id",
        childColumns = "rutinaId",
        onDelete = ForeignKey.CASCADE
), @ForeignKey(
        entity = Ejercicio.class,
        parentColumns = "id",
        childColumns = "ejercicioId",
        onDelete = ForeignKey.CASCADE
)}
)
public class RutinaEjercicio {
    @PrimaryKey(autoGenerate=true)
    private int id;

    @ColumnInfo(name="rutinaId")
    private int rutinaId;

    @ColumnInfo(name="ejercicioId")
    private int ejercicioId;

    @ColumnInfo(name="seriesObjetivo")
    private int seriesObjetivo;

    @ColumnInfo(name="repeticionesObjetivo")
    private int repeticionesObjetivo;

    @ColumnInfo(name="notas")
    private String notas;

    @ColumnInfo(name="orden")
    private int orden;

    public RutinaEjercicio(int id, int rutinaId, int ejercicioId, int seriesObjetivo, int repeticionesObjetivo, String notas, int orden) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.ejercicioId = ejercicioId;
        this.seriesObjetivo = seriesObjetivo;
        this.repeticionesObjetivo = repeticionesObjetivo;
        this.notas = notas;
        this.orden = orden;
    }

    @Ignore
    public RutinaEjercicio(int rutinaId, int ejercicioId, int seriesObjetivo, int repeticionesObjetivo, String notas, int orden) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.ejercicioId = ejercicioId;
        this.seriesObjetivo = seriesObjetivo;
        this.repeticionesObjetivo = repeticionesObjetivo;
        this.notas = notas;
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

    public int getSeriesObjetivo() {
        return seriesObjetivo;
    }

    public void setSeriesObjetivo(int seriesObjetivo) {
        this.seriesObjetivo = seriesObjetivo;
    }

    public int getRepeticionesObjetivo() {
        return repeticionesObjetivo;
    }

    public void setRepeticionesObjetivo(int repeticionesObjetivo) {
        this.repeticionesObjetivo = repeticionesObjetivo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
