package com.sthenos.fortium.ui.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

        holder.tvRoutineTitle.setText(rutinaActual.getNombre());

        holder.tvRoutineSubtitle.setText(rutinaActual.getDescripcion());

        holder.tvRoutineDate.setText("Creada: " + rutinaActual.getFechaCreacion());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RutinaDetalleActivity.class);
            intent.putExtra("rutinaId", rutinaActual.getId());
            v.getContext().startActivity(intent);
        });

        holder.btnRoutineOptions.setOnClickListener(v -> {
            // Por ahora ponemos un Toast, en el futuro aquí abriremos un PopupMenu
            Toast.makeText(v.getContext(), "Opciones de: " + rutinaActual.getNombre(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return rutinasList.size();
    }

    public void setRutinas(List<Rutina> rutinas) {
        this.rutinasList = rutinas;
        notifyDataSetChanged();
    }

    class RutinaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRoutineTitle;
        private TextView tvRoutineSubtitle;
        private TextView tvRoutineDate;
        private ImageButton btnRoutineOptions;

        public RutinaViewHolder(View itemView) {
            super(itemView);
            tvRoutineTitle = itemView.findViewById(R.id.tvRoutineTitle);
            tvRoutineSubtitle = itemView.findViewById(R.id.tvRoutineSubtitle);
            tvRoutineDate = itemView.findViewById(R.id.tvRoutineDate);
            btnRoutineOptions = itemView.findViewById(R.id.btnRoutineOptions);
        }
    }
}