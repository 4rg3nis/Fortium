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

public class ExerciseSelectionBottomSheet extends BottomSheetDialogFragment {

    // Interfaz de comunicacion
    public interface OnExerciseSelectedListener {
        void onExerciseSelected(Ejercicio ejercicio);
    }

    private OnExerciseSelectedListener listener;
    private RecyclerView rvExercises;
    private SelectableExerciseAdapter adapter;

    // Lista temporal para probar (luego la conectaremos a la BBDD)
    private List<Ejercicio> ejerciciosDisponibles = new ArrayList<>();

    // Constructor vacío requerido por Android
    public ExerciseSelectionBottomSheet() {}

    public void setListener(OnExerciseSelectedListener listener) {
        this.listener = listener;
    }

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

            // Cuando el usuario toca el ejercicio...
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExerciseSelected(ejercicio);
                }
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return ejerciciosDisponibles.size();
        }

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