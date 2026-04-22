package com.sthenos.fortium.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.EjercicioConDetalles;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para el listado de ejercicios de una rutina.
 * @author Argenis
 */
public class RutinaEjercicioAdapter extends RecyclerView.Adapter<RutinaEjercicioAdapter.ViewHolder> {
    private List<EjercicioConDetalles> ejerciciosList = new ArrayList<>();

    @NonNull
    @Override
    public RutinaEjercicioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rutina_ejercicio, parent, false);
        return new RutinaEjercicioAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaEjercicioAdapter.ViewHolder holder, int position) {
        EjercicioConDetalles ejercicio = ejerciciosList.get(position);

        holder.tvExerciseName.setText(ejercicio.ejercicio.getNombre());

        holder.tvSetsCount.setText(ejercicio.rutinaEjercicio.getSeriesObjetivo() + " Sets");

        holder.tvRepsCount.setText(ejercicio.rutinaEjercicio.getRepeticionesObjetivo() + " Reps");

        // Por ahora dejamos la foto por defecto, en caso de que se quiera poner usar Picasso

        holder.btnDelete.setOnClickListener(v -> {
            // TODO incluirlo mas adelante
        });
    }

    @Override
    public int getItemCount() {
        return ejerciciosList.size();
    }

    // Método vital para que la lista se refresque sola
    public void setEjercicios(List<EjercicioConDetalles> ejercicios) {
        this.ejerciciosList = ejercicios;
        notifyDataSetChanged(); // Refresca la interfaz
    }

    /**
     * ViewHolder para el listado de ejercicios.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvSetsCount, tvRepsCount;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvSetsCount = itemView.findViewById(R.id.tvSetsCount);
            tvRepsCount = itemView.findViewById(R.id.tvRepsCount);
            btnDelete = itemView.findViewById(R.id.btnDeleteExercise);
        }
    }
}
