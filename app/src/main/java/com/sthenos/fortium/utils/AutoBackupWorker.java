package com.sthenos.fortium.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.model.dto.ExportData;

import java.io.OutputStream;

/**
 * Clase encargada de ejecutar el respaldo automático de la base de datos en segundo plano.
 * Utiliza WorkManager para garantizar que la tarea se realice incluso si la app está cerrada.
 * @author Argenis
 */
public class AutoBackupWorker extends Worker {

    public AutoBackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        // Acceso a preferencias para obtener la ubicación del backup
        SharedPreferences prefs = context.getSharedPreferences("FortiumApp", Context.MODE_PRIVATE);

        // Obtener y validar la URI de la carpeta seleccionada por el usuario
        String uriString = prefs.getString("backupFolderUri", null);
        if (uriString == null) {
            return Result.failure(); // No hay carpeta configurada
        }

        Uri uriCarpeta = Uri.parse(uriString);
        // Esto nos permite manipular la carpeta (crear archivos, buscar, borrar) fácilmente.
        DocumentFile carpetaDestino = DocumentFile.fromTreeUri(context, uriCarpeta);

        // Validar si la carpeta aún existe (no ha sido movida o borrada)
        if (carpetaDestino == null || !carpetaDestino.exists()) {
            return Result.failure();
        }

        try {
            FortiumDatabase db = FortiumDatabase.getInstance(context);

            // Recopilamos todos los datos
            ExportData backup = new ExportData();
            backup.usuario = db.usuariosDao().getUsuario();
            backup.ejercicios = db.ejerciciosDao().getAllEjerciciosSync();
            backup.sesiones = db.sesionesDao().getAllSesionesSync();
            backup.rutinas = db.rutinasDao().getAllExport();
            backup.series = db.seriesDao().getAllSeriesSync();

            // Convertimos a JSON
            String jsonString = new Gson().toJson(backup);

            // Escribimos en la carpeta elegida por el usuario
            // Buscamos si ya existía una copia antigua para machacarla, si no, creamos una nueva
            DocumentFile archivoBackup = carpetaDestino.findFile("fortium_autobackup.json");
            if (archivoBackup == null) {
                archivoBackup = carpetaDestino.createFile("application/json", "fortium_autobackup.json");
            }

            if (archivoBackup != null) {
                try (OutputStream out = context.getContentResolver().openOutputStream(archivoBackup.getUri())) {
                    if (out != null) {
                        out.write(jsonString.getBytes());
                    }
                }
                Log.d("FortiumBackup", "Copia automática exitosa.");
                return Result.success();
            } else {
                return Result.failure();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FortiumBackup", "Error en backup automático", e);
            // Result.retry() le dice a Android que falló por algo temporal y lo intente más tarde
            return Result.retry();
        }
    }
}