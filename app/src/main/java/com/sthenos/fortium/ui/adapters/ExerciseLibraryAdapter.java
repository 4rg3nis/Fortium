package com.sthenos.fortium.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    @NonNull
    @Override
    public ExerciseLibraryAdapter.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_list, parent, false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseLibraryAdapter.ExerciseViewHolder holder, int position) {
        Ejercicio ejercicio = listaMostrada.get(position);
        holder.tvName.setText(ejercicio.getNombre());
        holder.tvMuscle.setText(ejercicio.getGrupoMuscularPrincipal());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onExerciseClick(ejercicio);
        });
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
        // TODO: Meter la foto del ejercicio.
        ExerciseViewHolder(View itemView) {
            super(itemView);
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
