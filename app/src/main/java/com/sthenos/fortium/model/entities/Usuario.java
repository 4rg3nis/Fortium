package com.sthenos.fortium.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sthenos.fortium.model.enums.UnidadMedida;
import com.sthenos.fortium.model.enums.Genero;

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
    private Genero genero;
    @ColumnInfo(name="altura")
    private double altura;
    @ColumnInfo(name="unidadMedida")
    private UnidadMedida unidadmedida;

    public Usuario(int id, String nombre, String apellido, String fechaNacimiento, double pesoActual, Genero genero, double altura, UnidadMedida unidadmedida) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.pesoActual = pesoActual;
        this.genero = genero;
        this.altura = altura;
        this.unidadmedida = unidadmedida;
    }

    @Ignore
    public Usuario(String nombre, String apellido, String fechaNacimiento, double pesoActual, double altura, Genero genero, UnidadMedida unidadmedida) {
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

    public Genero getGenero() {
        return genero;
    }

    public double getAltura() {
        return altura;
    }

    public UnidadMedida getUnidadmedida() {
        return unidadmedida;
    }

    public int getId() {
        return id;
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

    public void setPesoActual(double pesoActual) {
        this.pesoActual = pesoActual;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public void setUnidadmedida(UnidadMedida unidadmedida) {
        this.unidadmedida = unidadmedida;
    }
}