package com.sthenos.fortium.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.entities.RutinaEjercicio;

import java.util.List;

public class RutinaConEjercicios {
    @Embedded
    public Rutina rutina;

    // Le decimos a Room cómo obtener los ejercicios a través de la tabla intermedia
    @Relation(
            parentColumn = "id", // El ID de la Rutina
            entityColumn = "id", // El ID del Ejercicio
            associateBy = @Junction(
                    value = RutinaEjercicio.class,
                    parentColumn = "rutinaId",
                    entityColumn = "ejercicioId"
            )
    )
    public List<Ejercicio> ejercicios;
}
