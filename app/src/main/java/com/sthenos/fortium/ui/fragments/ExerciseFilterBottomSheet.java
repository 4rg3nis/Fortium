package com.sthenos.fortium.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sthenos.fortium.R;

/**
 * BottomSheet para filtrar los ejercicios.
 * @author Argenis
 */
public class ExerciseFilterBottomSheet extends BottomSheetDialogFragment {
    private OnFilterAppliedListener listener;
    private String filtroActual = null;

    /**
     * Listener para cuando se aplique un filtro.
     */
    public interface OnFilterAppliedListener {
        /**
         * Cuando se aplique un filtro.
         * @param musculoSeleccionado Grupo muscular seleccionado.
         */
        void onFilterApplied(String musculoSeleccionado);
    }

    /**
     * Establece el listener.
     * @param listener Listener para cuando se aplique un filtro.
     */
    public void setListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_exercise_filters, container, false);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupFilterMuscle);

        view.findViewById(R.id.btnApplyFilters).setOnClickListener(v -> {
            int selectedId = chipGroup.getCheckedChipId();
            // Cuando el id del chip seleccionado es NO_ID significa que no se ha seleccionado ningún chip. En ese caso
            // el filtro es nulo, de lo contrario es el texto del chip seleccionado.
            if (selectedId != View.NO_ID) {
                Chip chipSeleccionado = view.findViewById(selectedId);
                filtroActual = chipSeleccionado.getText().toString();
            } else {
                filtroActual = null;
            }

            if (listener != null) listener.onFilterApplied(filtroActual);
            dismiss();
        });
        // Botón para limpiar los filtros.
        view.findViewById(R.id.btnClearFilters).setOnClickListener(v -> {
            chipGroup.clearCheck();
            filtroActual = null;
            if (listener != null) listener.onFilterApplied(null);
            dismiss();
        });

        return view;
    }
}