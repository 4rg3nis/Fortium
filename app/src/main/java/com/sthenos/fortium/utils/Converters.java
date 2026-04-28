package com.sthenos.fortium.utils;

import androidx.room.TypeConverter;

import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.model.enums.TipoMedida;
import com.sthenos.fortium.model.enums.TipoSerie;
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
    public static String fromTipoMedida(TipoMedida tipo) {
        return tipo == null ? null : tipo.name();
    }

    @TypeConverter
    public static TipoMedida toTipoMedida(String value) {
        return value == null ? null : TipoMedida.valueOf(value);
    }

    @TypeConverter
    public static String fromTipoSerie(TipoSerie tipoSerie){
        return tipoSerie == null ? null : tipoSerie.name();
    }

    @TypeConverter
    public static TipoSerie toTipoSerie(String value){
        return value == null ? null : TipoSerie.valueOf(value);
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
