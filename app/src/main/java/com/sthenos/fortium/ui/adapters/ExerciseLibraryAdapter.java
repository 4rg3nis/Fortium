package com.sthenos.fortium.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para el recycler view de la lista de ejercicios.
 * @author Argenis
 */
public class ExerciseLibraryAdapter extends RecyclerView.Adapter<ExerciseLibraryAdapter.ExerciseViewHolder> {

    private List<Ejercicio> listaOriginal = new ArrayList<>();
    private List<Ejercicio> listaMostrada = new ArrayList<>();
    private OnExerciseClickListener listener;

    private Context context;

    public ExerciseLibraryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ExerciseLibraryAdapter.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_list, parent, false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseLibraryAdapter.ExerciseViewHolder holder, int position) {
        Ejercicio ejercicio = listaMostrada.get(position);
        String path = ejercicio.getImagenPath();
        holder.tvName.setText(ejercicio.getNombre());
        holder.tvMuscle.setText(ejercicio.getGrupoMuscularPrincipal());

        Object modeloDeCarga; // Esto puede ser un numero o un texto

        if (path != null && (path.startsWith("/") || path.startsWith("content://"))) {
            // Es una foto de la galeria
            modeloDeCarga = path;
        } else {
            // Es un ejercicio del sistema
            modeloDeCarga = obtenerRecursoDesdeString( path);
        }

        Glide.with(holder.itemView.getContext())
                .asBitmap() // Obliga a que sea estático
                .load(modeloDeCarga)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivExerciseImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onExerciseClick(ejercicio);
        });
    }

    /**
     * Traduce "archivo.gif" -> R.drawable.archivo
     * @param nombreArchivo Nombre del archivo.
     * @return Recurso de la imagen.
     */
    private int obtenerRecursoDesdeString(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return R.drawable.ic_launcher_foreground;
        }

        // Le quitamos la extensión
        String nombreLimpio = nombreArchivo.replaceFirst("[.][^.]+$", "");

        // Buscamos su DNI en la carpeta drawable
        int recursoId = context.getResources().getIdentifier(nombreLimpio, "drawable", context.getPackageName());

        // Si recursoId es 0, significa que no lo encontró.
        return recursoId != 0 ? recursoId : R.drawable.ic_launcher_foreground;
    }

    @Override
    public int getItemCount() {
        return listaMostrada.size();
    }

    /**
     * Establece la liista de ejercicios, tanto para la lista a mostrar que es la que se motrará cuando se aplicque un filtro
     * y la lista origial que es la que tiene todos los ejercicios.
     * @param lista Lista de ejercicios.
     */
    public void setEjercicios(List<Ejercicio> lista) {
        this.listaOriginal = new ArrayList<>(lista);
        this.listaMostrada = new ArrayList<>(lista);
        notifyDataSetChanged();
    }

    /**
     * Establece el listener para cuando se haga click en un ejercicio.
      * @param listener
     */
    public void setListener(OnExerciseClickListener listener) {
        this.listener = listener;
    }

    /**
     * Filtrar el contenido que se muestra de elercicios tanto por lo que se escribe en el buscador
     * como por el grupo muscular del ejercicio.
     * @param textoBusqueda Texto que se escribe en el buscador.
     * @param musculoFiltro Grupo muscular del ejercicio.
     */
    public void filtrar(String textoBusqueda, String musculoFiltro){
        listaMostrada.clear();
        if (textoBusqueda.isEmpty() && (musculoFiltro == null || musculoFiltro.isEmpty())) {
            // En caso de que tanto no haya grupo muscular elegido y no se haya escrito nada en el buscador, se muestran todos los ejercicios.
            listaMostrada.addAll(listaOriginal);
        } else {
            String busquedaLower = textoBusqueda.toLowerCase();
            for (Ejercicio ej : listaOriginal) {
                boolean coincideTexto = ej.getNombre().toLowerCase().contains(busquedaLower);
                // Equals o Contain??
                boolean coincideMusculo = musculoFiltro == null || ej.getGrupoMuscularPrincipal().equalsIgnoreCase(musculoFiltro);

                if (coincideTexto && coincideMusculo) {
                    listaMostrada.add(ej);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder para el ejercicio.
     */
    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMuscle;
        ImageView ivExerciseImage;
        // TODO: Meter la foto del ejercicio.
        ExerciseViewHolder(View itemView) {
            super(itemView);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
            tvName = itemView.findViewById(R.id.tvLibraryExerciseName);
            tvMuscle = itemView.findViewById(R.id.tvLibraryMuscleGroup);
        }
    }

    /**
     * Listener para cuando se haga click en un ejercicio.
     */
    public interface OnExerciseClickListener {
        /**
         * Cuando se haga click en un ejercicio.
         * @param ejercicio Ejercicio que se ha hecho click.
         */
        void onExerciseClick(Ejercicio ejercicio);
    }
}
