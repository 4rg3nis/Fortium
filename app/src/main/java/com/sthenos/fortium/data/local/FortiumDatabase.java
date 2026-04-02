package com.sthenos.fortium.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sthenos.fortium.data.local.dao.EjercicioMusculosSecundariosDao;
import com.sthenos.fortium.data.local.dao.EjerciciosDao;
import com.sthenos.fortium.data.local.dao.RutinasDao;
import com.sthenos.fortium.data.local.dao.RutinasEjerciciosDao;
import com.sthenos.fortium.data.local.dao.SeriesDao;
import com.sthenos.fortium.data.local.dao.SesionesDao;
import com.sthenos.fortium.data.local.dao.UsuariosDao;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.EjercicioMusculosSecundario;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.utils.Converters;

/**
 * Clase que representa la base de datos de la aplicación.
 */
// Definimos las entidades, la versión y si queremos exportar el esquema
@Database(entities = {Ejercicio.class , Rutina.class, Sesion.class, EjercicioMusculosSecundario.class, Serie.class, Usuario.class, RutinaEjercicio.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class}) // Registramos los conversores
public abstract class FortiumDatabase extends RoomDatabase {

    // Declaramos los DAOs como métodos abstractos
    public abstract EjerciciosDao ejerciciosDao();
    public abstract RutinasDao rutinasDao();
    public abstract SeriesDao seriesDao();
    public abstract SesionesDao sesionesDao();
    public abstract UsuariosDao usuariosDao();
    public abstract RutinasEjerciciosDao rutinasEjerciciosDao();
    public abstract EjercicioMusculosSecundariosDao ejercicioMusculosSecundariosDao();

    // Patrón Singleton
    private static volatile FortiumDatabase INSTANCE;

    // Patrón Singleton seguro para hilos (Thread-safe)
    public static FortiumDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (FortiumDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FortiumDatabase.class, "fortium_database")
                            .fallbackToDestructiveMigration() // Útil en desarrollo para no manejar migraciones manuales (ESTO BORRA LOS DATOS DE LAS TABLAS)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
