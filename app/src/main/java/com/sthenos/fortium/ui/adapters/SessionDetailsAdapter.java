package com.sthenos.fortium.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.queries.SerieHistorial;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SessionDetailsAdapter extends RecyclerView.Adapter<SessionDetailsAdapter.ViewHolder> {

    private List<EjercicioAgrupado> ejercicios = new ArrayList<>();
    private String  unidad = "kg";

    /**
     * Agrupa las series individuales por ejercicio (usando su ID) para mostrarlas en bloques organizados
     * @param datosDb Lista de series de la sesión
     */
    public void setDatosBrutos(List<SerieHistorial> datosDb) {
        // LinkedHashMap para agrupar manteniendo el orden de aparición de los ejercicios
        Map<Integer, EjercicioAgrupado> mapa = new LinkedHashMap<>();

        for (SerieHistorial s : datosDb) {
            if (!mapa.containsKey(s.ejercicioId)) {
                EjercicioAgrupado ej = new EjercicioAgrupado();
                ej.nombreEjercicio = s.nombreEjercicio;
                ej.notas = s.notaEjercicio;
                mapa.put(s.ejercicioId, ej);
            }
            mapa.get(s.ejercicioId).series.add(s);
        }

        this.ejercicios = new ArrayList<>(mapa.values());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detalle_ejercicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EjercicioAgrupado ejercicio = ejercicios.get(position);
        holder.tvNombreEjercicio.setText(ejercicio.nombreEjercicio);
        holder.tvPesoUnidad.setText(unidad.toUpperCase());

        // Lógica para mostrar u ocultar la nota
        String nota = ejercicio.notas;

        if (nota != null && !nota.trim().isEmpty()) {
            holder.tvNotasEjercicio.setVisibility(View.VISIBLE);
            holder.tvNotasEjercicio.setText("Nota: " + nota);
        } else {
            // Si el usuario no escribió nota en esta serie, ocultamos el texto para que no ocupe espacio
            holder.tvNotasEjercicio.setVisibility(View.GONE);
        }

        // Limpiamos para el siguiente ejercicio
        holder.llContenedorSeries.removeAllViews();

        // Inyectamos las series una a una
        int numSerie = 1;
        for (SerieHistorial serie : ejercicio.series) {
            // Creamos un LinearLayout horizontal por cada serie al vuelo
            LinearLayout fila = new LinearLayout(holder.itemView.getContext());
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setPadding(0, 8, 0, 8);

            // Función de ayuda para crear los TextViews
            TextView tvNum = crearTextView(holder.itemView.getContext(), String.valueOf(numSerie));
            TextView tvPeso = crearTextView(holder.itemView.getContext(), String.format(Locale.getDefault(), "%.1f", serie.peso));
            TextView tvReps = crearTextView(holder.itemView.getContext(), String.valueOf(serie.repeticiones));

            fila.addView(tvNum);
            fila.addView(tvPeso);
            fila.addView(tvReps);

            holder.llContenedorSeries.addView(fila);
            numSerie++;
        }
    }

    /**
     * Pequeño método para generar textos con el mismo estilo dinámicamente
     * @param context Contexto de la vista
     * @param texto Texto a mostrar
     * @return TextView con el estilo deseado
     */
    private TextView crearTextView(Context context, String texto) {
        TextView tv = new TextView(context);
        tv.setText(texto);
        tv.setTextColor(android.graphics.Color.WHITE);
        tv.setTextSize(16f);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        return tv;
    }

    @Override
    public int getItemCount() { return ejercicios.size(); }

    public void setUnits(String unidad) {
        this.unidad = unidad;
        notifyDataSetChanged();
    }

    /**
     * Clase interna para el ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEjercicio, tvNotasEjercicio, tvPesoUnidad;
        LinearLayout llContenedorSeries;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreEjercicio = itemView.findViewById(R.id.tvDetalleNombreEjercicio);
            tvNotasEjercicio = itemView.findViewById(R.id.tvNotasEjercicio);
            llContenedorSeries = itemView.findViewById(R.id.llContenedorSeries);
            tvPesoUnidad = itemView.findViewById(R.id.tvPesoUnidad);
        }
    }

    /**
     * Clase interna para agrupar los datos
     */
    private static class EjercicioAgrupado {
        private String nombreEjercicio;
        private String notas;
        private List<SerieHistorial> series = new ArrayList<>();
    }
}