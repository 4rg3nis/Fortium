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
    private String pesoActual;
    @ColumnInfo(name="genero")
    private String genero;
    @ColumnInfo(name="altura")
    private String altura;
    @ColumnInfo(name="unidadMedida")
    private UnitMeasure unidadmedida;

    public Usuario(int uid, String nombre, String apellido, String fechaNacimiento, String pesoActual, String genero, String altura, UnitMeasure unidadmedida) {
        this.id = uid;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.pesoActual = pesoActual;
        this.genero = genero;
        this.altura = altura;
        this.unidadmedida = unidadmedida;
    }

    public Usuario(String nombre, String apellido, String fechaNacimiento, String pesoActual, String genero, String altura, UnitMeasure unidadmedida) {
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

    public String getPesoActual() {
        return pesoActual;
    }

    public String getGenero() {
        return genero;
    }

    public String getAltura() {
        return altura;
    }

    public UnitMeasure getUnidadmedida() {
        return unidadmedida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setPesoActual(String pesoActual) {
        this.pesoActual = pesoActual;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public void setUnidadmedida(UnitMeasure unidadmedida) {
        this.unidadmedida = unidadmedida;
    }
}
