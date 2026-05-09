package com.sthenos.fortium.ui.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sthenos.fortium.R;

/**
 * BottomSheet para filtrar los ejercicios.
 * @author Argenis
 */
public class ExerciseFilterBottomSheet extends BottomSheetDialogFragment {

    private EjercicioViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_exercise_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EjercicioViewModel.class);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupFilterMuscle);

        view.findViewById(R.id.btnApplyFilters).setOnClickListener(v -> {
            int selectedId = chipGroup.getCheckedChipId();
            // Cuando el id del chip seleccionado es NO_ID significa que no se ha seleccionado ningún chip. En ese caso
            // el filtro es nulo, de lo contrario es el texto del chip seleccionado.
            if (selectedId != View.NO_ID) {
                Chip chipSeleccionado = view.findViewById(selectedId);
                viewModel.setFiltroMusculo(chipSeleccionado.getText().toString());
            } else {
                viewModel.setFiltroMusculo(null);
            }

            dismiss();
        });
        // Botón para limpiar los filtros.
        view.findViewById(R.id.btnClearFilters).setOnClickListener(v -> {
            chipGroup.clearCheck();
            viewModel.setFiltroMusculo(null); // Limpiamos el filtro
            dismiss();
        });
    }
}