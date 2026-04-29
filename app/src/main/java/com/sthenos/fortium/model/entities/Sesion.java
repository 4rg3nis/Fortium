package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Sesiones", foreignKeys = {
        @ForeignKey(
                entity = Rutina.class,
                parentColumns = "id",
                childColumns = "rutinaId",
                onDelete = ForeignKey.SET_NULL) // En caso de que la rutin ase borre, esto sigue.
        },
        indices = {@Index("rutinaId")} // Para optimizar las consultas de rutina
)
public class Sesion {

    @PrimaryKey(autoGenerate=true)
    private int id;

    // En esta se usa integer para que pueda ser nulo.
    @ColumnInfo(name="rutinaId")
    private Integer rutinaId;

    @ColumnInfo(name="fechaInicio")
    @NonNull
    private String fechaInicio;

    @ColumnInfo(name="fechaFin")
    private String fechaFin;

    @ColumnInfo(name="cantidadSeries")
    private int cantidadSeries;

    @ColumnInfo(name = "volumenTotal")
    private double volumenTotal;

    @ColumnInfo(name="notas")
    private String notas;

    public Sesion(int id, Integer rutinaId, @NonNull String fechaInicio, String fechaFin, int cantidadSeries, double volumenTotal, String notas) {
        this.id = id;
        this.rutinaId = rutinaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadSeries = cantidadSeries;
        this.volumenTotal = volumenTotal;
        this.notas = notas;
    }

    @Ignore
    public Sesion( Integer rutinaId, @NonNull String fechaInicio, String fechaFin, int cantidadSeries, double volumenTotal, String notas) {
        this.rutinaId = rutinaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadSeries = cantidadSeries;
        this.volumenTotal = volumenTotal;
        this.notas = notas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getRutinaId() {
        return rutinaId;
    }

    public void setRutinaId(Integer rutinaId) {
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


    public double getVolumenTotal() {
        return volumenTotal;
    }

    public void setVolumenTotal(double volumenTotal) {
        this.volumenTotal = volumenTotal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
