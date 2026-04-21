package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Sesiones", foreignKeys = {
        @ForeignKey(
                entity = Rutina.class,
                parentColumns = "id",
                childColumns = "rutinaId",
                onDelete = ForeignKey.CASCADE)
})
public class Sesion {

    @PrimaryKey(autoGenerate=true)
    private int id;

    @ColumnInfo(name="rutinaId")
    private int rutinaId;

    @ColumnInfo(name="fechaInicio")
    @NonNull
    private String fechaInicio;

    @ColumnInfo(name="fechaFin")
    private String fechaFin;

    @ColumnInfo(name="cantidadSeries")
    private int cantidadSeries;

    @ColumnInfo(name="recordPersonal")
    private boolean recordPersonal = false;

    @ColumnInfo(name="comentarioGeneral")
    private String comentarioGeneral;

    public Sesion(int id, int rutinaId, @NonNull String fechaInicio, String fechaFin, int cantidadSeries, boolean recordPersonal, String comentarioGeneral) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadSeries = cantidadSeries;
        this.recordPersonal = recordPersonal;
        this.comentarioGeneral = comentarioGeneral;
    }

    @Ignore
    public Sesion( int rutinaId, @NonNull String fechaInicio, String fechaFin, int cantidadSeries, boolean recordPersonal, String comentarioGeneral) {
        this.rutinaId = rutinaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadSeries = cantidadSeries;
        this.recordPersonal = recordPersonal;
        this.comentarioGeneral = comentarioGeneral;
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

    @NonNull
    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(@NonNull String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getCantidadSeries() {
        return cantidadSeries;
    }

    public void setCantidadSeries(int cantidadSeries) {
        this.cantidadSeries = cantidadSeries;
    }

    public boolean isRecordPersonal() {
        return recordPersonal;
    }

    public void setRecordPersonal(boolean recordPersonal) {
        this.recordPersonal = recordPersonal;
    }

    public String isComentarioGeneral() {
        return comentarioGeneral;
    }

    public void setComentarioGeneral(String comentarioGeneral) {
        this.comentarioGeneral = comentarioGeneral;
    }

    public String getComentarioGeneral() {
        return comentarioGeneral;
    }
}
