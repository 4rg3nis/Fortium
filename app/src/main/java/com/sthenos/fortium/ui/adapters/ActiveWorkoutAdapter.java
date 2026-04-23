package com.sthenos.fortium.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.EjercicioConDetalles;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ActiveWorkoutAdapter extends RecyclerView.Adapter<ActiveWorkoutAdapter.ExerciseViewHolder> {
    private final Context context;
    // Asumiremos que tienes 3 ejercicios cargados para probar
    private int cantidadEjercicios = 3;
    private List<EjercicioConDetalles> listaEjercicios = new ArrayList<>();

    public ActiveWorkoutAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ActiveWorkoutAdapter.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_active_exercise, parent, false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveWorkoutAdapter.ExerciseViewHolder holder, int position) {
        EjercicioConDetalles item = listaEjercicios.get(position);
        holder.tvExerciseName.setText(item.ejercicio.getNombre());

        holder.layoutSetsContainer.removeAllViews();

        // Creamos tantas filas como el usuario configuró en la plantilla
        int seriesObjetivo = item.rutinaEjercicio.getSeriesObjetivo();

        for (int i = 1; i <= seriesObjetivo; i++) {
            agregarFilaSerie(holder.layoutSetsContainer, i, item.rutinaEjercicio.getRepeticionesObjetivo());
        }

        // Lógica del botón "+ Añadir Serie"
        holder.btnAddSet.setOnClickListener(v -> {
            int nuevaSerieNum = holder.layoutSetsContainer.getChildCount() + 1;
            // TODO: Hacer que siga el mismo patron que los otros y poner que las repeticiones sea igual que el ultimo?
            agregarFilaSerie(holder.layoutSetsContainer, nuevaSerieNum, 0);
        });

    }

    private void agregarFilaSerie(LinearLayout container, int numeroSerie, int repeticionesObjetivo) {
        // Inflamos el XML de la fila individual
        View filaView = LayoutInflater.from(context).inflate(R.layout.item_workout_set_row, container, false);

        // Buscamos los elementos dentro de ESA fila específica
        TextView tvSetNumber = filaView.findViewById(R.id.tvSetNumber);
        TextView etRepsInput = filaView.findViewById(R.id.etRepsInput);
        ToggleButton btnCheck = filaView.findViewById(R.id.btnCheckSet);

        tvSetNumber.setText(String.valueOf(numeroSerie));
        etRepsInput.setHint(String.valueOf(repeticionesObjetivo));

        // Borrar serie al mantener pulsado el número
        tvSetNumber.setOnLongClickListener(v -> {
            container.removeView(filaView);
            // TODO: Recalcular los números de las series restantes para que no salte del 1 al 3
            return true;
        });

        // Cambiar comportamiento al marcar el Check (Ej: Iniciar temporizador de descanso)
        btnCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // El usuario completó la serie
                // Aquí podrías leer el peso y reps de los EditText y guardarlos en un modelo temporal
            }
        });

        // Añadimos la fila al contenedor visual
        container.addView(filaView);
    }

    // En ActiveWorkoutAdapter.java
    public void setEjercicios(List<EjercicioConDetalles> ejercicios) {
        this.listaEjercicios = ejercicios;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (listaEjercicios == null) {
            return 0;
        }
        return listaEjercicios.size();
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, btnAddSet;
        LinearLayout layoutSetsContainer;

        ExerciseViewHolder(View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvActiveExerciseName);
            layoutSetsContainer = itemView.findViewById(R.id.layoutSetsContainer);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
        }
    }
}
