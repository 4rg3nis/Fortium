package com.sthenos.fortium.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.model.enums.TipoMedida;
import com.sthenos.fortium.utils.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);


    // Patrón Singleton seguro para hilos (Thread-safe)
    public static FortiumDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (FortiumDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FortiumDatabase.class, "fortium_database")
                            .fallbackToDestructiveMigration() // Útil en desarrollo para no manejar migraciones manuales (ESTO BORRA LOS DATOS DE LAS TABLAS)
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

            databaseWriteExecutor.execute(() -> {
                EjerciciosDao ejerciciosDao = INSTANCE.ejerciciosDao();

                Ejercicio sentadilla = new Ejercicio("Sentadilla con Barra", "Cuadriceps",  true, "Flexión de rodillas con barra en la espalda", Equipo.PESO_LIBRE,  "sentadillas_barra.gif");
                Ejercicio pressBanca = new Ejercicio("Press de Banca", "Pecho", true, "Empuje horizontal con barra en banco plano",  Equipo.PESO_LIBRE, "press_banca.gif");
                Ejercicio pesoMuerto = new Ejercicio("Peso Muerto", "Espalda", true, "Levantamiento de barra desde el suelo",  Equipo.PESO_LIBRE, "peso_muerto.gif");
                Ejercicio dominadas = new Ejercicio("Dominadas", "Espalda", true, "Tracción vertical con peso corporal",  Equipo.PESO_CORPORAL,"dominadas.gif");
                Ejercicio pressMilitar = new Ejercicio("Press Militar", "Hombros", true, "Empuje vertical con barra o mancuernas",  Equipo.PESO_LIBRE,"press_militar.gif");

                ejerciciosDao.insert(sentadilla);
                ejerciciosDao.insert(pressBanca);
                ejerciciosDao.insert(pesoMuerto);
                ejerciciosDao.insert(dominadas);
                ejerciciosDao.insert(pressMilitar);
            });
        }

    };

}
