package com.sthenos.fortium.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import com.google.gson.Gson;
import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.model.dto.ExportData;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Clase para importar datos desde un archivo JSON.
 * @author Argenis
 */
public class JsonImporter {

    /**
     * Interfaz para avisar a la Activity de lo que ha pasado
     */
    public interface ImportCallback {
        /**
         * Se ha completado la importación con éxito.
         */
        void onSuccess();

        /**
         * Ha ocurrido un error durante la importación.
         * @param mensaje Mensaje de error.
         */
        void onError(String mensaje);
    }

    /**
     * Importa los datos desde un archivo JSON.
     * @param context Contexto de la aplicación.
     * @param uri Ruta del archivo JSON.
     * @return Datos importados.
     */
    public static ExportData importarDeJson(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            Gson gson = new Gson();
            // Convertimos el texto de vuelta a nuestros objetos Java
            return gson.fromJson(reader, ExportData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ejecuta la importación completa.
     * @param context Contexto de la aplicación.
     * @param uri Ruta del archivo JSON.
     * @param callback Interfaz para avisar a la Activity de lo que ha pasado.
     */
    public static void ejecutarImportacionCompleta(Context context, Uri uri, ImportCallback callback) {
        new Thread(() -> {
            try {
                // Leer el archivo
                ExportData backup = importarDeJson(context, uri);

                if (backup != null && backup.usuario != null) {
                    FortiumDatabase db = FortiumDatabase.getInstance(context.getApplicationContext());

                    // Limpia profunda
                    db.clearAllTables();

                    // Insertar datos
                    db.usuariosDao().insert(backup.usuario);
                    if (backup.ejercicios != null) db.ejerciciosDao().insertAll(backup.ejercicios);
                    if (backup.rutinas != null) db.rutinasDao().insertAll(backup.rutinas);
                    if (backup.sesiones != null) db.sesionesDao().insertAll(backup.sesiones);
                    if (backup.series != null) db.seriesDao().insertAll(backup.series);

                    // Marcar el perfil como creado para que no pida registro nunca más
                    SharedPreferences prefs = context.getSharedPreferences("FortiumApp", Context.MODE_PRIVATE);
                    prefs.edit().putBoolean("perfilCreado", true).apply();

                    // Avisamos de que todo ha ido genial
                    if (callback != null) {
                        callback.onSuccess();
                    }

                } else {
                    if (callback != null) callback.onError("El archivo JSON no es válido o está corrupto.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError("Error crítico al importar los datos.");
            }
        }).start();
    }
}