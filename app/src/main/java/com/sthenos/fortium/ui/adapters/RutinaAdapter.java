package com.sthenos.fortium.ui.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.ui.activities.RutinaDetalleActivity;

import java.util.ArrayList;
import java.util.List;

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {
    private List<Rutina> rutinasList = new ArrayList<>();

    @NonNull
    @Override
    public RutinaAdapter.RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new RutinaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaAdapter.RutinaViewHolder holder, int position) {
        Rutina rutinaActual = rutinasList.get(position);

        holder.tvRoutineName.setText(rutinaActual.getNombre());
        // Como es una versión simplificada, mostramos la descripción en lugar de la cuenta de ejercicios
        holder.tvRoutineDesc.setText(rutinaActual.getDescripcion());

        // Añadimos el clic a la tarjeta entera (itemView)
        holder.itemView.setOnClickListener(v -> {
            // Creamos un Intent para viajar a la nueva Activity
            Intent intent = new Intent(v.getContext(), RutinaDetalleActivity.class);

            intent.putExtra("rutinaId", rutinaActual.getId());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rutinasList.size();
    }

    // Método vital: el ViewModel llama a este método cuando detecta cambios en la BBDD
    public void setRutinas(List<Rutina> rutinas) {
        this.rutinasList = rutinas;
        notifyDataSetChanged();
    }

    class RutinaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRoutineName;
        private TextView tvRoutineDesc;

        public RutinaViewHolder(View itemView) {
            super(itemView);
            tvRoutineName = itemView.findViewById(R.id.tvRoutineName);
            tvRoutineDesc = itemView.findViewById(R.id.tvRoutineDesc);
        }
    }
}
