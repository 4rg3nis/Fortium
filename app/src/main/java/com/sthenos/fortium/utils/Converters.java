package com.sthenos.fortium.utils;

import androidx.room.TypeConverter;

import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.model.enums.UnidadMedida;

/**
 * Clase para convertir tipos enumerados a cadenas y viceversa.
 */
public class Converters {
    @TypeConverter
    public static String fromEquipo(Equipo equipo) {
        return equipo == null ? null : equipo.name();
    }

    @TypeConverter
    public static Equipo toEquipo(String value) {
        return value == null ? null : Equipo.valueOf(value);
    }

    @TypeConverter
    public static String fromUnitMeasure(UnidadMedida unitMeasure){
        return unitMeasure == null ? null : unitMeasure.name();
    }

    @TypeConverter
    public static UnidadMedida toUnitMeasure(String value){
        return value == null ? null : UnidadMedida.valueOf(value);
    }

    @TypeConverter
    public static String fromGenero(Genero genero) {
        return genero == null ? null : genero.name();
    }

    @TypeConverter
    public static Genero toGenero(String value) {
        return value == null ? null : Genero.valueOf(value);
    }
}
