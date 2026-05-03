package com.sthenos.fortium.ui.workout;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.queries.EjercicioConDetalles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveWorkoutAdapter extends RecyclerView.Adapter<ActiveWorkoutAdapter.ExerciseViewHolder> {
    private final Context context;
    private OnSetActionListener listener;
    private List<EjercicioConDetalles> listaEjercicios = new ArrayList<>();

    // Memoria para los tiempos de descanso para cada ejercicio.
    private SparseIntArray tiemposDescanso = new SparseIntArray();

    private static final int TIEMPO_DESCANSO_INICIAL_DEFAULT = 90;

    // Memoria temporal para las notas mientras dura el entrenamiento
    private Map<Integer, String> notasTemporales = new HashMap<>();

    private Map<Integer, List<String>> ultimosRecords = new HashMap<>();

    private String unidad = "kg";


    public ActiveWorkoutAdapter(Context context, OnSetActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Actualiza los records de los ejercicios.
     * @param records Los nuevos records de los ejercicios.
     */
    public void setUltimosRecords(Map<Integer, List<String>> records) {
        this.ultimosRecords = records;
        notifyDataSetChanged();
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
        notifyDataSetChanged(); // Refrescamos para que cambien las cabeceras
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

        if (holder.tvUnidadMedida != null) {
            holder.tvUnidadMedida.setText(unidad);
        }

        // Pongo el texto formateado en el botón.
        int tiempoActualSegundos = tiemposDescanso.get(position);
        holder.btnRestTimerConfig.setText(formatearTiempo(tiempoActualSegundos));

        // Lógica del botón "Configurar tiempo de descanso" para cada ejercicio.
        holder.btnRestTimerConfig.setOnClickListener(v -> {
            // Obtenemos la posición real en el momento del click.
            // No usamos la 'position' del onBindViewHolder porque si se eliminan o añaden
            // ejercicios, esa variable quedaría desactualizada (apuntando al índice incorrecto).
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                mostrarSelectorDeTiempo(currentPos, holder.btnRestTimerConfig);
            }
        });

        // Limpia el contenedor para evitar que se dupliquen o mezclen series al reciclar la vista
        holder.layoutSetsContainer.removeAllViews();

        // Creamos tantas filas como el usuario configuró en la plantilla
        int seriesObjetivo = item.rutinaEjercicio.getSeriesObjetivo();
        for (int i = 1; i <= seriesObjetivo; i++) {
            agregarFilaSerie(holder.layoutSetsContainer, i, item.rutinaEjercicio.getRepeticionesObjetivo(), position, holder.etExerciseNotes);
        }

        // Lógica del botón "+ Añadir Serie"
        holder.btnAddSet.setOnClickListener(v -> {
            int nuevaSerieNum = holder.layoutSetsContainer.getChildCount() + 1;
            agregarFilaSerie(holder.layoutSetsContainer, nuevaSerieNum, 0, position, holder.etExerciseNotes);
        });

        // Evitamos que el reciclaje de vistas mezcle los datos:
        // Si el EditText ya tenía un TextWatcher de otro ejercicio, lo quitamos
        // antes de cambiar el texto para que no se dispare y sobrescriba notas ajenas.
        if (holder.etExerciseNotes.getTag() instanceof TextWatcher) {
            holder.etExerciseNotes.removeTextChangedListener((TextWatcher) holder.etExerciseNotes.getTag());
        }

        // Cargamos la nota desde nuestra memoria temporal del adaptador
        String notaActual = notasTemporales.get(position);
        holder.etExerciseNotes.setText(notaActual != null ? notaActual : "");

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Guardamos en el mapa usando la posición del ejercicio como clave
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    notasTemporales.put(currentPos, s.toString());
                }
            }
        };

        // Se lo enganchamos al EditText y lo guardamos en el Tag para poder quitarlo luego
        holder.etExerciseNotes.addTextChangedListener(watcher);
        holder.etExerciseNotes.setTag(watcher);
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
        int[] opcionesSeg = {30, 60, 90, 120, 150, 180, 300};

        new MaterialAlertDialogBuilder(context)
                .setTitle("Descanso para este ejercicio")
                .setItems(opcionesTxt, (dialog, which) -> {
                    int nuevoTiempo = opcionesSeg[which];
                    tiemposDescanso.put(position, nuevoTiempo);
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
     * @param etExerciseNotes El EditText de las notas del ejercicio
     */
    private void agregarFilaSerie(LinearLayout container, int numeroSerie, int repeticionesObjetivo, int positionEjercicio, EditText etExerciseNotes) {
        View filaView = LayoutInflater.from(context).inflate(R.layout.item_workout_set_row, container, false);

        TextView tvSetNumber = filaView.findViewById(R.id.tvSetNumber);
        TextView tvRecord = filaView.findViewById(R.id.tvRecordAnterior);
        TextView tvRepsInput = filaView.findViewById(R.id.etRepsInput);
        ToggleButton btnCheck = filaView.findViewById(R.id.btnCheckSet);

        tvSetNumber.setText(String.valueOf(numeroSerie));

        if (tvRecord != null) {
            int idEjercicioReal = listaEjercicios.get(positionEjercicio).ejercicio.getId();
            List<String> recordsEjercicio = ultimosRecords.get(idEjercicioReal);
            int indiceLista = numeroSerie - 1;

            if (recordsEjercicio != null && indiceLista < recordsEjercicio.size()) {
                tvRecord.setText(recordsEjercicio.get(indiceLista));
                tvRecord.setVisibility(View.VISIBLE);
            } else {
                tvRecord.setText("-");
            }
        }

        tvRepsInput.setHint(String.valueOf(repeticionesObjetivo));

        EditText etWeightInput = filaView.findViewById(R.id.etWeightInput);
        etWeightInput.setHint(unidad);
        EditText etRpeInput = filaView.findViewById(R.id.etRpeInput);
        EditText etRepsInput = filaView.findViewById(R.id.etRepsInput);

        tvSetNumber.setOnLongClickListener(v -> {
            container.removeView(filaView);
            for (int i = 0; i < container.getChildCount(); i++) {
                View filaRestante = container.getChildAt(i);
                TextView tvNumeroRestante = filaRestante.findViewById(R.id.tvSetNumber);

                if (tvNumeroRestante != null) {
                    tvNumeroRestante.setText(String.valueOf(i + 1));
                }
            }
            return true;
        });

        // Cambiar comportamiento al marcar el Check, en la que se guardará la serie en la memoria temporal para que se pueda
        // borrar cuando se quiera.
        btnCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                String pesoStr = etWeightInput.getText().toString();
                String repsStr = etRepsInput.getText().toString();
                String rpeStr = etRpeInput.getText().toString();

                if (pesoStr.isEmpty() || repsStr.isEmpty() || rpeStr.isEmpty()) {
                    Toast.makeText(context, "Introduce el peso, las repeticiones y el rpe", Toast.LENGTH_SHORT).show();

                    // Desactivamos el check porque la validación falló
                    btnCheck.setChecked(false);
                    return;
                }

                float peso = pesoStr.isEmpty() ? 0.0f : Float.parseFloat(pesoStr);
                int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                float rpe = rpeStr.isEmpty() ? 0.0f : Float.parseFloat(rpeStr);

                if (rpe > 10.0f) {
                    android.widget.Toast.makeText(context, "El RPE máximo es 10", android.widget.Toast.LENGTH_SHORT).show();
                    btnCheck.setChecked(false);
                    return;
                }

                etWeightInput.setEnabled(false);
                etRepsInput.setEnabled(false);
                etRpeInput.setEnabled(false);

                if (listener != null) {
                    int tiempoParaEsteEjercicio = tiemposDescanso.get(positionEjercicio);
                    int idEjercicioReaL = listaEjercicios.get(positionEjercicio).ejercicio.getId();

                    String notaStr = etExerciseNotes.getText().toString().trim();

                    listener.onSetCompleted(tiempoParaEsteEjercicio, idEjercicioReaL, peso, reps, rpe, notaStr);
                }
            } else {
                etWeightInput.setEnabled(true);
                etRepsInput.setEnabled(true);
                etRpeInput.setEnabled(true);

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

        container.addView(filaView);
    }

    public void setEjercicios(List<EjercicioConDetalles> ejercicios) {
        this.listaEjercicios = ejercicios;

        // Limpiamos memorias al actualizar la lista
        tiemposDescanso.clear();
        notasTemporales.clear();

        for (int i = 0; i < ejercicios.size(); i++) {
            tiemposDescanso.put(i, TIEMPO_DESCANSO_INICIAL_DEFAULT);
        }
        notifyDataSetChanged();
    }

    public void addEjercicioEnVivo(EjercicioConDetalles nuevoEjercicio) {
        if (listaEjercicios == null) {
            listaEjercicios = new ArrayList<>();
        }

        listaEjercicios.add(nuevoEjercicio);
        int nuevaPosicion = listaEjercicios.size() - 1;

        tiemposDescanso.put(nuevaPosicion, 90);
        notifyItemInserted(nuevaPosicion);
    }

    @Override
    public int getItemCount() {
        if (listaEjercicios == null) {
            return 0;
        }
        return listaEjercicios.size();
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, btnAddSet, btnRestTimerConfig, tvUnidadMedida;
        EditText etExerciseNotes;
        LinearLayout layoutSetsContainer;

        ExerciseViewHolder(View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvActiveExerciseName);
            layoutSetsContainer = itemView.findViewById(R.id.layoutSetsContainer);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
            btnRestTimerConfig = itemView.findViewById(R.id.btnRestTimerConfig);
            etExerciseNotes = itemView.findViewById(R.id.etExerciseNotes);
            tvUnidadMedida = itemView.findViewById(R.id.tvUnidadMedida);
        }
    }

    public interface OnSetActionListener {
        void onSetCompleted(int tiempoDescanso, int ejercicioId, float peso, int reps, float rpe, String nota);
        void onSetUnchecked(int ejercicioId, float peso, int reps);
    }
}