package com.sthenos.fortium.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.ui.activities.ExerciseFormActivity;
import com.sthenos.fortium.ui.adapters.ExerciseLibraryAdapter;
import com.sthenos.fortium.ui.viewmodels.EjercicioViewModel;

/**
 * Fragment para la lista de ejercicios.
 * @author Argenis
 */
public class ExerciseFragment extends Fragment {
    private ExerciseLibraryAdapter adapter;
    private EjercicioViewModel viewModel;
    private String filtroMusculoActual = null;
    private String textoBusquedaActual = "";

    private TextInputEditText etSearch;
    private ChipGroup chipGroupActiveFilters;
    private ImageButton btnFilters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents(view);
        setupRecyclerView(view);
        setupViewModel();
        setupListeners(view);
    }

    /**
     * Configura los listeners.
     * @param view
     */
    private void setupListeners(View view) {
        // Listener para cuando se escribe en el buscador. Por cada cambio que tenga el texto se filtra el recycler view.
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoBusquedaActual = s.toString();
                adapter.filtrar(textoBusquedaActual, filtroMusculoActual);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnFilters.setOnClickListener(v -> abrirBottomSheetFiltros());

        view.findViewById(R.id.fabAddCustomExercise).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ExerciseFormActivity.class);
            // Al no pasarle ejercicioId, el activity sabrá automáticamente que es modo creacion
            startActivity(intent);
        });
    }

    /**
     * Abre el bottom sheet de filtros.
     */
    private void abrirBottomSheetFiltros() {
        ExerciseFilterBottomSheet bottomSheet = new ExerciseFilterBottomSheet();
        bottomSheet.setListener(musculoSeleccionado -> {
            filtroMusculoActual = musculoSeleccionado;
            adapter.filtrar(textoBusquedaActual, filtroMusculoActual);
            // Refresca la UI de filtros activos
            actualizarChipVisual();
        });
        bottomSheet.show(getChildFragmentManager(), "Filtros");
    }

    /**
     * Actualiza la vista de los chips de los filtros.
     */
    private void actualizarChipVisual() {
        // Limpiamos los filtros visuales previos para redibujar desde cero
        chipGroupActiveFilters.removeAllViews();
        // Si el filtro no es nulo, lo añadimos a la vista.
        if (filtroMusculoActual != null && !filtroMusculoActual.isEmpty()) {
            Chip chip = new Chip(requireContext());
            chip.setText(filtroMusculoActual);
            chip.setCloseIconVisible(true);

            // Si el usuario le da a la X del chip, quitamos el filtro
            chip.setOnCloseIconClickListener(v -> {
                filtroMusculoActual = null;
                chipGroupActiveFilters.removeAllViews();
                adapter.filtrar(textoBusquedaActual, filtroMusculoActual);
            });
            // Añade el chip configurado al contenedor para que sea visible en la interfaz
            chipGroupActiveFilters.addView(chip);
        }
    }

    /**
     * Configura el view model.
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(EjercicioViewModel.class);
        viewModel.getAllEjercicios().observe(getViewLifecycleOwner(), ejercicios -> {
            if (ejercicios != null) {
                adapter.setEjercicios(ejercicios);
            }
        });
    }

    /**
     * Configura el recycler view.
     * @param view Vista.
     */
    private void setupRecyclerView(View view) {
        RecyclerView rvExerciseLibrary = view.findViewById(R.id.rvExerciseLibrary);
        rvExerciseLibrary.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExerciseLibraryAdapter(requireContext());
        adapter.setListener(ejercicio -> {
            Intent intent = new Intent(requireContext(), ExerciseFormActivity.class);

            // Pasamos el ID del ejercicio.
            intent.putExtra("ejercicioId",  ejercicio.getId());

            startActivity(intent);
        });
        rvExerciseLibrary.setAdapter(adapter);
    }

    /**
     * Inicializa los componentes de la vista.
     * @param view Vista.
     */
    private void initComponents(View view) {
        etSearch = view.findViewById(R.id.etSearchExercise);
        chipGroupActiveFilters = view.findViewById(R.id.chipGroupActiveFilters);
        btnFilters = view.findViewById(R.id.btnOpenFilters);
    }
}
