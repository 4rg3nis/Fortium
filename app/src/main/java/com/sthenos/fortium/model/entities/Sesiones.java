package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sesiones {

    @PrimaryKey(autoGenerate=true)
    private int id;

    @ColumnInfo(name="usuarioId")
    private int usuarioId;

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
    private boolean comentarioGeneral;

    public Sesiones(int id, int usuarioId, int rutinaId, @NonNull String fechaInicio, String fechaFin, int cantidadSeries, boolean recordPersonal, boolean comentarioGeneral) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.rutinaId = rutinaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadSeries = cantidadSeries;
        this.recordPersonal = recordPersonal;
        this.comentarioGeneral = comentarioGeneral;
    }

    public Sesiones(int usuarioId, int rutinaId, @NonNull String fechaInicio, String fechaFin, int cantidadSeries, boolean recordPersonal, boolean comentarioGeneral) {
        this.usuarioId = usuarioId;
        this.rutinaId = rutinaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadSeries = cantidadSeries;
        this.recordPersonal = recordPersonal;
        this.comentarioGeneral = comentarioGeneral;
    }
}
