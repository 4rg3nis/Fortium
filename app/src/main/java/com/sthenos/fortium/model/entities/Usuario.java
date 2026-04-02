package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sthenos.fortium.model.enums.UnitMeasure;

@Entity(tableName = "Usuarios")
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name="nombre")
    @NonNull
    private String nombre;
    @ColumnInfo(name="apellido")
    private String apellido;
    @ColumnInfo(name="fechaNacimiento")
    private String fechaNacimiento;
    @ColumnInfo(name="pesoActual")
    private double pesoActual;
    @ColumnInfo(name="genero")
    private String genero;
    @ColumnInfo(name="altura")
    private double altura;
    @ColumnInfo(name="unidadMedida")
    private UnitMeasure unidadmedida;

    public Usuario(int uid, String nombre, String apellido, String fechaNacimiento, double pesoActual, String genero, double altura, UnitMeasure unidadmedida) {
        this.id = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.pesoActual = pesoActual;
        this.genero = genero;
        this.altura = altura;
        this.unidadmedida = unidadmedida;
    }

    public Usuario(String nombre, String apellido, String fechaNacimiento, double pesoActual, String genero, double altura, UnitMeasure unidadmedida) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.pesoActual = pesoActual;
        this.genero = genero;
        this.altura = altura;
        this.unidadmedida = unidadmedida;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public double getPesoActual() {
        return pesoActual;
    }

    public String getGenero() {
        return genero;
    }

    public double getAltura() {
        return altura;
    }

    public UnitMeasure getUnidadmedida() {
        return unidadmedida;
    }
}