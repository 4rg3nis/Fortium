package com.sthenos.fortium.ui.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.utils.ImageUtils;

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

    private ExerciseLibraryAdapter adapter;
    private OnExerciseSelectedListener listener;
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
            adapter.setEjercicios(ejercicios);
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

        RecyclerView rvExercises = view.findViewById(R.id.rvSelectableExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExerciseLibraryAdapter();
        // Si ya teníamos la lista antes de que se creara la vista, se la pasamos
        adapter.setEjercicios(ejerciciosDisponibles);

        // Usamos el listener de nuestro
        adapter.setListener(ejercicio -> {
            if (listener != null) {
                listener.onExerciseSelected(ejercicio);
            }
            dismiss(); // Cerramos la vista
        });

        rvExercises.setAdapter(adapter);
    }
}