package com.sthenos.fortium.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.sthenos.fortium.data.local.dao.EjerciciosDao;
import com.sthenos.fortium.data.local.dao.RutinasDao;
import com.sthenos.fortium.data.local.dao.RutinasEjerciciosDao;
import com.sthenos.fortium.data.local.dao.SeriesDao;
import com.sthenos.fortium.data.local.dao.SesionesDao;
import com.sthenos.fortium.data.local.dao.UsuariosDao;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.entities.RutinaEjercicio;
import com.sthenos.fortium.model.entities.Serie;
import com.sthenos.fortium.model.entities.Sesion;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.utils.Converters;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase que representa la base de datos de la aplicación.
 * @author Argenis
 */
// Definimos las entidades, la versión y si queremos exportar el esquema, en desarollo lo dejamos en falso, pero en producción debe de
// estar en true. Esto hace que Room genere un archivo JSON en tu proyecto con la estructura exacta de tu base de datos cada vez que se sube la versión.
@Database(entities = {Ejercicio.class , Rutina.class, Sesion.class, Serie.class, Usuario.class, RutinaEjercicio.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class}) // Registramos el conversor
public abstract class FortiumDatabase extends RoomDatabase {

    // Declaramos los DAOs como métodos abstractos
    public abstract EjerciciosDao ejerciciosDao();
    public abstract RutinasDao rutinasDao();
    public abstract SeriesDao seriesDao();
    public abstract SesionesDao sesionesDao();
    public abstract UsuariosDao usuariosDao();
    public abstract RutinasEjerciciosDao rutinasEjerciciosDao();

    // Patrón Singleton
    private static volatile FortiumDatabase INSTANCE;

    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);


    // Patrón Singleton seguro para hilos
    public static FortiumDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (FortiumDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FortiumDatabase.class, "fortium_database")
                            .fallbackToDestructiveMigration() // En producción quitaría esa línea y programaría un Migration, de lo contrario, borraría todo el historial de entrenamiento de mis usuarios
                            .addCallback(rommCallBack)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Inserta datos al iniciar por primera vez en el aplicación, si ya se tiene la aplicacion estos no se podran insertar
     */
    private static RoomDatabase.Callback rommCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Ejecutamos en segundo plano para no bloquear el hilo principal
            databaseWriteExecutor.execute(() -> {
                EjerciciosDao ejerciciosDao = INSTANCE.ejerciciosDao();

                // Creamos la lista de ejercicios predefinidos
                List<Ejercicio> ejerciciosPredefinidos = Arrays.asList(
                        new Ejercicio("Sentadilla con Barra", "Cuadriceps", true, "Flexión de rodillas con barra en la espalda", Equipo.PESO_LIBRE, "sentadillas_barra.gif"),
                        new Ejercicio("Press de Banca", "Pecho", true, "Empuje horizontal con barra en banco plano", Equipo.PESO_LIBRE, "press_banca.gif"),
                        new Ejercicio("Peso Muerto", "Espalda", true, "Levantamiento de barra desde el suelo", Equipo.PESO_LIBRE, "peso_muerto.gif"),
                        new Ejercicio("Dominadas", "Espalda", true, "Tracción vertical con peso corporal", Equipo.PESO_CORPORAL, "dominadas.gif"),
                        new Ejercicio("Press Militar", "Hombros", true, "Empuje vertical con barra o mancuernas", Equipo.PESO_LIBRE, "press_militar.gif")
                );

                ejerciciosDao.insertAll(ejerciciosPredefinidos);
            });
        }

    };

}
