package com.sthenos.fortium.model.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Representa la unión entre un ejercicio y sus detalles dentro de una rutina.
 * @author Argenis
 */
public class EjercicioConDetalles {
    // Datos específicos de la relación (series, repeticiones, etc.)
    @Embedded
    public RutinaEjercicio rutinaEjercicio;

    // Y aquí le decimos a Room que busque automáticamente el Ejercicio completo.
    // Carga automáticamente la información general del ejercicio vinculado
    @Relation(parentColumn = "ejercicioId", entityColumn = "id")
    public Ejercicio ejercicio;
}
