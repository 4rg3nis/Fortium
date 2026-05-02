package com.sthenos.fortium.model.queries;

import androidx.room.Embedded;

import com.sthenos.fortium.model.entities.Rutina;

/**
 * Clase que representa una vista resumida de una rutina.
 * @autor Argenis
 */
public class RutinaResumen {
    // Hacemos que Room meta aquí todos los datos normales de la rutina
    @Embedded
    public Rutina rutina;

    // Aquí guardaremos los datos que se quieren mostrar en el modo lista
    public int totalEjercicios;
    public String musculosInvolucrados;
    public String ultimaVez;
}
