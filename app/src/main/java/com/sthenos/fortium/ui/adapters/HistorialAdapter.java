package com.sthenos.fortium.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sthenos.fortium.R;

import com.sthenos.fortium.model.queries.HistorialSesion;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador personalizado para el RecyclerView de sesiones recientes.
 * @author Argenis
 */
public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<HistorialSesion> sesiones = new ArrayList<>();
    private final boolean enActivity;
    private OnItemClickListener listener;

    /**
     * Interfaz para manejar los clics en los elementos del RecyclerView.
     */
    public interface OnItemClickListener {
        /**
         * Se llama cuando se hace clic en un elemento del RecyclerView. Para ver el detalle de la sesión.
         * @param sesion La sesión seleccionada.
         */
        void onItemClick(HistorialSesion sesion);

        /**
         * Se llama cuando se hace un clic largo en un elemento del RecyclerView. Para eliminar la sesión.
         * @param sesion La sesión seleccionada.
         */
        void onItemLongClick(HistorialSesion sesion);

    }

    public HistorialAdapter(boolean enActivity, OnItemClickListener listener) {
        this.enActivity = enActivity;
        this.listener = listener;
    }


    public void setSesiones(List<HistorialSesion> nuevasSesiones) {
        this.sesiones = nuevasSesiones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = enActivity ? R.layout.item_historial_vertical : R.layout.item_historial_reciente;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistorialSesion sesion = sesiones.get(position);

        // Para entrenamientos que puedes hacer desde el fragment home ( que no tendrán rutina asociado)
        String nombre = (sesion.nombreRutina != null) ? sesion.nombreRutina : "Entrenamiento Libre";
        holder.tvNombreRutina.setText(nombre);

        String fechaLimpia = sesion.fechaInicio;
        if (fechaLimpia != null && fechaLimpia.length() >= 16) {
            fechaLimpia = fechaLimpia.substring(0, 16).replace("-", "/");
        }
        holder.tvFecha.setText(fechaLimpia);

        // Totales de Volumen y Series
        holder.tvSeries.setText(sesion.cantidadSeries + " Series");
        holder.tvVolumen.setText(String.format("%.1f kg", sesion.volumenTotal));

        // Lógica de las Notas
        if (sesion.notas != null && !sesion.notas.trim().isEmpty()) {
            holder.tvNotas.setVisibility(View.VISIBLE);
            holder.tvNotas.setText("\"" + sesion.notas + "\"");
        } else {
            holder.tvNotas.setVisibility(View.GONE); // Se oculta si no hay notas
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sesion);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(sesion);
            }
            return true; // El "true" le dice a Android que ya hemos manejado este toque y que no dispare también el clic normal
        });
    }

    @Override
    public int getItemCount() {
        return sesiones.size();
    }

    /**
     * ViewHolder personalizado para el RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreRutina, tvFecha, tvSeries, tvVolumen, tvNotas;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombreRutina = itemView.findViewById(R.id.tvNombreRutinaSesion);
            tvFecha = itemView.findViewById(R.id.tvFechaSesion);
            tvSeries = itemView.findViewById(R.id.tvSeriesTotales);
            tvVolumen = itemView.findViewById(R.id.tvVolumenTotal);
            tvNotas = itemView.findViewById(R.id.tvNotasSesion);
        }
    }
}