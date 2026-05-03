package com.sthenos.fortium.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sthenos.fortium.R;

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
}