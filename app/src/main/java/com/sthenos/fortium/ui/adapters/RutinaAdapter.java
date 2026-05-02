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
import com.sthenos.fortium.model.queries.RutinaResumen;
import com.sthenos.fortium.ui.activities.RutinaDetalleActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {
    private List<RutinaResumen> rutinasList = new ArrayList<>();

    @NonNull
    @Override
    public RutinaAdapter.RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine, parent, false);
        return new RutinaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaAdapter.RutinaViewHolder holder, int position) {
        RutinaResumen resumen = rutinasList.get(position);

        holder.tvRoutineTitle.setText(resumen.rutina.getNombre());

        holder.tvRoutineSubtitle.setText(resumen.totalEjercicios + " Ejercicios - " +
                (resumen.musculosInvolucrados != null ? resumen.musculosInvolucrados.replace(",", ", ") : "Sin Musculos"));

        if (resumen.ultimaVez != null) {
            holder.tvRoutineDate.setText("Última vez: " + formatearTiempoTranscurrido(resumen.ultimaVez));
        } else {
            holder.tvRoutineDate.setText("Nunca entrenado");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RutinaDetalleActivity.class);
            intent.putExtra("rutinaId", resumen.rutina.getId());
            v.getContext().startActivity(intent);
        });

        holder.btnRoutineOptions.setOnClickListener(v -> {
            // Por ahora ponemos un Toast, en el futuro aquí abriremos un PopupMenu
            Toast.makeText(v.getContext(), "Opciones de: " + resumen.rutina.getNombre(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Convierte una fecha SQL (yyyy-MM-dd HH:mm:ss) en tiempo relativo.
     * @param fechaSql Fecha obtenida de la base de datos.
     * @return "Hoy", "Ayer", "Hace n días" o la fecha original si hay un error.
     */
    private String formatearTiempoTranscurrido(String fechaSql) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date fecha = sdf.parse(fechaSql);
            if (fecha == null) return fechaSql;

            long diferenciaMillis = System.currentTimeMillis() - fecha.getTime();
            long dias = diferenciaMillis / (1000 * 60 * 60 * 24);

            if (dias == 0) return "Hoy";
            if (dias == 1) return "Ayer";
            return "Hace " + dias + " días";
        } catch (Exception e) {
            return fechaSql; // Si falla el parseo, devolvemos la fecha en bruto
        }
    }

    @Override
    public int getItemCount() {
        return rutinasList.size();
    }

    public void setRutinas(List<RutinaResumen> rutinas) {
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