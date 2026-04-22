package com.sthenos.fortium.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;

import java.util.ArrayList;
import java.util.List;

/**
 * Crea una pequeña ventana en el que muestra el listado de los ejercicios para poder meterlos, en la rutina
 * @author Argenis
 */
public class ExerciseSelectionBottomSheet extends BottomSheetDialogFragment {

    /**
     * Interfaz para gestionar el evento de selección de un ejercicio.
     */
    public interface OnExerciseSelectedListener {
        /**
         * Se dispara cuando el usuario toca un ejercicio de la lista.
         * @param ejercicio El objeto Ejercicio seleccionado.
         */
        void onExerciseSelected(Ejercicio ejercicio);
    }

    private OnExerciseSelectedListener listener;
    private RecyclerView rvExercises;
    private SelectableExerciseAdapter adapter;
    private List<Ejercicio> ejerciciosDisponibles = new ArrayList<>();

    // Constructor vacío requerido por Android
    public ExerciseSelectionBottomSheet() {}

    /**
     * Establece un listener para cuando se seleccione un ejercicio.
     * @param listener Listener a establecer.
     */
    public void setListener(OnExerciseSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Actualiza la fuente de datos del adaptador y refresca la vista.
     * @param ejercicios Lista de objetos {@link Ejercicio} a mostrar.
     */
    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejerciciosDisponibles = ejercicios;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_exercise_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvExercises = view.findViewById(R.id.rvSelectableExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SelectableExerciseAdapter();
        rvExercises.setAdapter(adapter);
    }

    /**
     * Adaptador para el listado de ejercicios.
     */
    private class SelectableExerciseAdapter extends RecyclerView.Adapter<SelectableExerciseAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_selectable, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Ejercicio ejercicio = ejerciciosDisponibles.get(position);
            holder.tvName.setText(ejercicio.getNombre());
            holder.tvMuscle.setText(ejercicio.getGrupoMuscularPrincipal());

            // Establecer el listener para cuando se seleccione un ejercicio en el listado.
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExerciseSelected(ejercicio);
                }
                dismiss(); // Cerramos la vista
            });
        }

        @Override
        public int getItemCount() {
            return ejerciciosDisponibles.size();
        }

        /**
         * ViewHolder para el listado de ejercicios.
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvMuscle;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvExerciseName);
                tvMuscle = itemView.findViewById(R.id.tvExerciseMuscle);
            }
        }
    }
}