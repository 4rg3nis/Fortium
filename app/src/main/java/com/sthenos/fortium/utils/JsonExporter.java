package com.sthenos.fortium.utils;

import android.content.Context;
import android.net.Uri;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sthenos.fortium.model.dto.ExportData;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Clase para exportar datos a un archivo JSON.
 * @author Argenis
 */
public class JsonExporter {

    /**
     * Exporta los datos a un archivo JSON.
     * @param context Contexto de la aplicación.
     * @param fileUri Ruta del archivo donde se exportarán los datos.
     * @param data Datos a exportar.
     * @return True si la exportación fue exitosa, false en caso contrario.
     */
    public static boolean exportarAJson(Context context, Uri fileUri, ExportData data) {
        try {
            // Convertir el objeto a un String JSON con un buen formato
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(data);

            // Escribir el String en el archivo seleccionado por el usuario
            OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri);
            if (outputStream != null) {
                outputStream.write(jsonString.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}