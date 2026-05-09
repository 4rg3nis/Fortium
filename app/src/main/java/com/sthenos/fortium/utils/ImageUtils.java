package com.sthenos.fortium.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sthenos.fortium.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Clase de utilidad para manejar la carga de imágenes en toda la app.
 * @author Argenis
 */
public class ImageUtils {

    /**
     * Carga la imagen de un ejercicio en el ImageView proporcionado.
     * Soporta tanto rutas del almacenamiento interno como recursos de la carpeta drawable.
     *
     * @param context Contexto de la aplicación.
     * @param imagePath Ruta de la imagen o nombre del archivo (ej. "press_banca.gif").
     * @param imageView El ImageView donde se mostrará la imagen.
     */
    public static void cargarImagenEjercicio(Context context, String imagePath, ImageView imageView, boolean estatico) {
        Object fuenteDeImagen;

        // Analizamos si es una foto subida o un recurso de la app
        if (imagePath != null && imagePath.startsWith("/")) {
            fuenteDeImagen = imagePath; // Foto del almacenamiento
        } else {
            fuenteDeImagen = obtenerRecursoDesdeString(context, imagePath); // Foto de res/drawable
        }


        if(estatico){
            Glide.with(context)
                    .asBitmap()
                    .load(fuenteDeImagen)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView);

        } else {
            Glide.with(context)
                    .load(fuenteDeImagen)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView);
        }

    }

    /**
     * Traduce "archivo.gif" -> R.drawable.archivo
     * @param nombreArchivo Nombre del archivo.
     * @return Recurso de la imagen.
     */
    private static int obtenerRecursoDesdeString(Context context, String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return R.drawable.ic_launcher_foreground; // Imagen por defecto
        }

        // Le quitamos la extensión al archivo
        String nombreLimpio = nombreArchivo.replaceFirst("[.][^.]+$", "");

        // Buscamos su ID numérico en la carpeta drawable
        int recursoId = context.getResources().getIdentifier(nombreLimpio, "drawable", context.getPackageName());

        return recursoId != 0 ? recursoId : R.drawable.ic_launcher_foreground;
    }

    /**
     * Realiza una copia física de una imagen desde una Uri externa hacia el almacenamiento
     * interno privado de la aplicación.
     * Esta operación se ejecuta en un hilo secundario para evitar bloquear el Main Thread.
     * Es obligatorio usar este método al procesar Uris provenientes del selector del sistema,
     * ya que los permisos de lectura nativos son temporales y se revocan al destruir el ciclo
     * de vida de la app. Además, previene enlaces rotos si el usuario elimina la foto original.
     *
     * @param context  Contexto necesario para acceder al ContentResolver y a la ruta getFilesDir().
     * @param uri      Uri temporal de la imagen original seleccionada por el usuario.
     * @param callback Función que se ejecuta en el hilo principal al finalizar. Devuelve la ruta
     * absoluta del nuevo archivo, o null si falló la copia.
     */
    public static void copiarImagenAInternoAsync(Context context, Uri uri, Consumer<String> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Abre un flujo de entrada para leer los datos de la URI
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                String extension = context.getContentResolver().getType(uri);
                extension = extension != null ? extension.split("/")[1] : "jpg";

                String fileName = "img_ejercicio_" + System.currentTimeMillis() + "." + extension;

                // Crea el archivo de destino en la carpeta privada de la app
                File archivoDestino = new File(context.getFilesDir(), fileName);
                FileOutputStream outputStream = new FileOutputStream(archivoDestino);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.close();
                if (inputStream != null) inputStream.close();

                // Devolvemos la ruta en el hilo principal para que la UI pueda pintarla
                new Handler(Looper.getMainLooper()).post(() -> callback.accept(archivoDestino.getAbsolutePath()));

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.accept(null));
            }
        });
    }
}