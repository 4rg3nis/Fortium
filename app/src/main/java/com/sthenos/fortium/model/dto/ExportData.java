package com.sthenos.fortium.model.dto;

import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Usuario;
import java.util.List;

/**
 * Clase que representa los datos a exportar.
 * @author Argenis
 */
public class ExportData {
    public Usuario usuario;
    public List<Ejercicio> ejercicios;
    public List<Rutina> rutinas;
    public List<Sesion> sesiones;
    public List<Serie> series;
    public List<RutinaEjercicio> rutinasEjercicios;

    public ExportData(Usuario usuario, List<Ejercicio> ejercicios, List<Rutina> rutinas,List<Sesion> sesiones, List<Serie> series, List<RutinaEjercicio> rutinasEjercicios) {
        this.usuario = usuario;
        this.ejercicios = ejercicios;
        this.rutinas = rutinas;
        this.sesiones = sesiones;
        this.series = series;
        this.rutinasEjercicios = rutinasEjercicios;
    }

    public ExportData() {}
}