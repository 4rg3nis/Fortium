package com.sthenos.fortium.ui.adapters;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.EjercicioConDetalles;

import java.util.ArrayList;
import java.util.List;

public class ActiveWorkoutAdapter extends RecyclerView.Adapter<ActiveWorkoutAdapter.ExerciseViewHolder> {
    private final Context context;
    private OnSetActionListener listener;
    private List<EjercicioConDetalles> listaEjercicios = new ArrayList<>();

    // Memoria para los tiempos de descanso para cada ejercicio.
    private SparseIntArray tiemposDescanso = new SparseIntArray();

    private static final int TIEMPO_DESCANSO_INICIAL_DEFAULT = 90;

    public ActiveWorkoutAdapter(Context context, OnSetActionListener listener) {
        this.context = context;
        this.listener = listener;
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

        // Pongo el texto formateado en el botón.
        int tiempoActualSegundos = tiemposDescanso.get(position);
        holder.btnRestTimerConfig.setText(formatearTiempo(tiempoActualSegundos));

        // Lógica del botón "Configurar tiempo de descanso" para cada ejercicio.
        holder.btnRestTimerConfig.setOnClickListener(v -> {
            mostrarSelectorDeTiempo(position, holder.btnRestTimerConfig);
        });

        // Limpia el contenedor para evitar que se dupliquen o mezclen series al reciclar la vista
        holder.layoutSetsContainer.removeAllViews();

        // Creamos tantas filas como el usuario configuró en la plantilla
        int seriesObjetivo = item.rutinaEjercicio.getSeriesObjetivo();
        for (int i = 1; i <= seriesObjetivo; i++) {
            agregarFilaSerie(holder.layoutSetsContainer, i, item.rutinaEjercicio.getRepeticionesObjetivo(), position);
        }

        // Lógica del botón "+ Añadir Serie"
        holder.btnAddSet.setOnClickListener(v -> {
            int nuevaSerieNum = holder.layoutSetsContainer.getChildCount() + 1;
            // TODO: Hacer que siga el mismo patron que los otros y poner que las repeticiones sea igual que el ultimo?
            agregarFilaSerie(holder.layoutSetsContainer, nuevaSerieNum, 0, position);
        });

    }

    /**
     * Muestra un selector de tiempo de descanso para el ejercicio actual. El usuario puede seleccionar el tiempo
     * que quiere para este ejercicio sin que afecte a los otros. Vienen unos por defectos que suelen ser los 'normales'
     * a la hora de descansar. Dependiendo del tipo de entrenamienteo que sea, 'fuerza', 'hipertrofia', etc...
     * @param position La posición del ejercicio en la lista
     * @param btnRestTimerConfig El botón que contiene el tiempo de descanso actual del ejercicio en cuestión (para actualizarlo)
     */
    private void mostrarSelectorDeTiempo(int position, TextView btnRestTimerConfig) {
        String[] opcionesTxt = {"30 seg", "1 min", "1 min 30 seg", "2 min", "2 min 30 seg", "3 min", "5 min"};
        // El valor real en segundos de cada opción
        int[] opcionesSeg = {30, 60, 90, 120, 150, 180, 300};

        new MaterialAlertDialogBuilder(context)
                .setTitle("Descanso para este ejercicio")
                .setItems(opcionesTxt, (dialog, which) -> {
                    // Guardamos el nuevo tiempo en la memoria
                    int nuevoTiempo = opcionesSeg[which];
                    tiemposDescanso.put(position, nuevoTiempo);

                    // Actualizamos el botón visualmente con el nuevo tiempo formateado.
                    btnRestTimerConfig.setText(formatearTiempo(nuevoTiempo));
                })
                .show();
    }

    /**
     * Formateamos el tiempo en minutos y segundos para que se vea como "01:30"
     * @param tiempoActualSegundos El tiempo en segundos que queremos formatear
     * @return El tiempo formateado en formato "01:30" o "00:00" si es 0
     */
    private String formatearTiempo(int tiempoActualSegundos) {
        int minutos = tiempoActualSegundos / 60;
        int segundos = tiempoActualSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    /**
     * Agrega la fila de serie, en el ejercicio correspondiente, al contenedor de series.
     * @param container EL contenedor de series del ejercicio
     * @param numeroSerie El numero del orden de la serie
     * @param repeticionesObjetivo EL numero de repeticiones
     * @param positionEjercicio La posicion del ejercicio para saber el tiempo de descanso correspondiente a ese ejercicio.
     */
    private void agregarFilaSerie(LinearLayout container, int numeroSerie, int repeticionesObjetivo, int positionEjercicio) {
        // Inflamos el XML de la fila individual
        View filaView = LayoutInflater.from(context).inflate(R.layout.item_workout_set_row, container, false);

        // Buscamos los elementos dentro de ESA fila específica
        TextView tvSetNumber = filaView.findViewById(R.id.tvSetNumber);
        TextView tvRepsInput = filaView.findViewById(R.id.etRepsInput);
        ToggleButton btnCheck = filaView.findViewById(R.id.btnCheckSet);

        tvSetNumber.setText(String.valueOf(numeroSerie));
        tvRepsInput.setHint(String.valueOf(repeticionesObjetivo));

        EditText etWeightInput = filaView.findViewById(R.id.etWeightInput);
        EditText etRpeInput = filaView.findViewById(R.id.etRpeInput);
        EditText etRepsInput = filaView.findViewById(R.id.etRepsInput);

        // Borrar serie al mantener pulsado el número
        tvSetNumber.setOnLongClickListener(v -> {
            container.removeView(filaView);

            // Bucle para recalcular el orden de las series.
            for (int i = 0; i < container.getChildCount(); i++) {
                View filaRestante = container.getChildAt(i);
                TextView tvNumeroRestante = filaRestante.findViewById(R.id.tvSetNumber);

                if (tvNumeroRestante != null) {
                    // 'i' empieza en 0, así que le sumamos 1 para que las series sean 1, 2, 3...
                    tvNumeroRestante.setText(String.valueOf(i + 1));
                }
            }
            return true;
        });

        // Cambiar comportamiento al marcar el Check, en la que se guardará la serie en la memoria temporal para que se pueda
        // borrar cuando se quiera.
        btnCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Leemos lo que ha escrito el usuario (Si está vacío, ponemos un 0)
                String pesoStr = etWeightInput.getText().toString();
                String repsStr = etRepsInput.getText().toString();
                String rpeStr = etRpeInput.getText().toString();

                float peso = pesoStr.isEmpty() ? 0.0f : Float.parseFloat(pesoStr);
                int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                float rpe = rpeStr.isEmpty() ? 0.0f : Float.parseFloat(rpeStr);

                if (rpe > 10.0f) {
                    android.widget.Toast.makeText(context, "El RPE máximo es 10", android.widget.Toast.LENGTH_SHORT).show();
                    btnCheck.setChecked(false); // Desmarcamos el botón automáticamente
                    return; // Cortamos la ejecución aquí, no se guarda nada
                }

                // Bloqueamos los campos para que no pueda editarlos mientras está en check
                etWeightInput.setEnabled(false);
                etRepsInput.setEnabled(false);
                etRpeInput.setEnabled(false);

                // Avisamos a la Activity para que guarde esta serie en la memoria temporal
                if (listener != null) {
                    int tiempoParaEsteEjercicio = tiemposDescanso.get(positionEjercicio);

                    int idEjercicioReaL = listaEjercicios.get(positionEjercicio).ejercicio.getId();

                    listener.onSetCompleted(tiempoParaEsteEjercicio, idEjercicioReaL, peso, reps, rpe);
                }
            } else {
                // Si DESMARCA la serie

                // Desbloqueamos los campos
                etWeightInput.setEnabled(true);
                etRepsInput.setEnabled(true);
                etRpeInput.setEnabled(true);

                // Avisamos a la Activity para que borre esta serie de la memoria temporal
                if (listener != null) {
                    String pesoStr = etWeightInput.getText().toString();
                    String repsStr = etRepsInput.getText().toString();

                    float peso = pesoStr.isEmpty() ? 0.0f : Float.parseFloat(pesoStr);
                    int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                    int idEjercicioReaL = listaEjercicios.get(positionEjercicio).ejercicio.getId();

                    listener.onSetUnchecked(idEjercicioReaL, peso, reps);
                }
            }
        });

        // Añadimos la fila al contenedor visual
        container.addView(filaView);
    }

    /**
     * Actualiza la lista de ejercicios con los nuevos datos. Tambien limpia el mapa de tiempos de descanso y
     * le asigna los 90 segundos por defecto a cada ejercicio.
     * @param ejercicios La lista de ejercicio qeu se quiere que se muestre en el adaptador.
     */
    public void setEjercicios(List<EjercicioConDetalles> ejercicios) {
        this.listaEjercicios = ejercicios;

        // Asignamos 90 segundos por defecto a cada ejercicio al cargar
        tiemposDescanso.clear();
        for (int i = 0; i < ejercicios.size(); i++) {
            tiemposDescanso.put(i, TIEMPO_DESCANSO_INICIAL_DEFAULT);
        }
        notifyDataSetChanged();
    }

    /**
     * Añade un nuevo ejercicio a la lista de ejercicios.
     * @param nuevoEjercicio El nuevo ejercicio a añadir
     */
    public void addEjercicioEnVivo(EjercicioConDetalles nuevoEjercicio) {
        if (listaEjercicios == null) {
            listaEjercicios = new ArrayList<>();
        }

        listaEjercicios.add(nuevoEjercicio);
        int nuevaPosicion = listaEjercicios.size() - 1;

        // Le ponemos 90 segundos de descanso por defecto al nuevo ejercicio
        tiemposDescanso.put(nuevaPosicion, 90);

        // Avisamos al RecyclerView de que se ha insertado un elemento nuevo al final
        notifyItemInserted(nuevaPosicion);
    }

    @Override
    public int getItemCount() {
        if (listaEjercicios == null) {
            return 0;
        }
        return listaEjercicios.size();
    }

    /**
     * ViewHolder del adaptador de {@link ActiveWorkoutAdapter}
     */
    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, btnAddSet, btnRestTimerConfig;
        LinearLayout layoutSetsContainer;

        ExerciseViewHolder(View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvActiveExerciseName);
            layoutSetsContainer = itemView.findViewById(R.id.layoutSetsContainer);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
            btnRestTimerConfig = itemView.findViewById(R.id.btnRestTimerConfig);
        }
    }

    // Creamos la interfaz, que actua como túnel.
    public interface OnSetActionListener {
        // Le pasaremos los segundos que debe descansar (ej: 90)
        void onSetCompleted(int tiempoDescansoSegundos);

        // Pasamos todos los datos que el usuario ha escrito
        void onSetCompleted(int tiempoDescanso, int ejercicioId, float peso, int reps, float rpe);

        // Necesitamos saber si el usuario se arrepiente y desmarca la serie
        void onSetUnchecked(int ejercicioId, float peso, int reps);
    }
}
