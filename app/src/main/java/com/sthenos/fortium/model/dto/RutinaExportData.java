package com.sthenos.fortium.model.dto;

import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.entities.RutinaEjercicio;

import java.util.List;

/**
 * Clase que representa el paquete de datos para exportar una rutina.
 * @author Argenis
 */
public class RutinaExportData {
    public Rutina rutina;
    public List<RutinaEjercicio> ejercicios;
}
