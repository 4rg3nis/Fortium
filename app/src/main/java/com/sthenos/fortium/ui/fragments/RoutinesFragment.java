package com.sthenos.fortium.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.ui.adapters.RutinaAdapter;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;

public class RoutinesFragment extends Fragment {

    private RecyclerView rvLibraryRoutines;
    private MaterialButton btnCreateRoutine;

    private RutinaViewModel rutinaViewModel;

    private LinearLayout layoutEmptyState;
    private TextInputEditText etSearchRoutines;
    private ImageButton btnFilter;
    private TextView btnImportJSON;
    private ExtendedFloatingActionButton fabCreateRoutine;
    private RutinaAdapter adapter;

    public RoutinesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_routines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents(view);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    private void initComponents(View view) {
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        rvLibraryRoutines = view.findViewById(R.id.rvLibraryRoutines);
        etSearchRoutines = view.findViewById(R.id.etSearchRoutines);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnImportJSON = view.findViewById(R.id.btnImportJSON);
        fabCreateRoutine = view.findViewById(R.id.fabCreateRoutine);
    }

    private void setupRecyclerView() {
        rvLibraryRoutines.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RutinaAdapter();
        rvLibraryRoutines.setAdapter(adapter);
    }

    private void setupViewModel() {
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);

        // Observamos los datos de la base de datos en tiempo real
        rutinaViewModel.getAllRutinas().observe(getViewLifecycleOwner(), rutinas -> {
            if (rutinas == null || rutinas.isEmpty()) {
                // Si no hay rutinas, mostramos el Empty State y ocultamos la lista
                layoutEmptyState.setVisibility(View.VISIBLE);
                rvLibraryRoutines.setVisibility(View.GONE);
            } else {
                // Si hay rutinas, ocultamos el Empty State y mostramos la lista
                layoutEmptyState.setVisibility(View.GONE);
                rvLibraryRoutines.setVisibility(View.VISIBLE);
                adapter.setRutinas(rutinas);
            }
        });
    }

    private void setupListeners() {
        // Botón FAB (Crear nueva rutina)
        fabCreateRoutine.setOnClickListener(v -> {
            // Aquí en el futuro abriremos un BottomSheet o un Dialog para pedir el nombre
            Toast.makeText(getContext(), "Próximamente: Crear Rutina", Toast.LENGTH_SHORT).show();
        });

        // Botón Importar JSON
        btnImportJSON.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Próximamente: Importar de archivos", Toast.LENGTH_SHORT).show();
        });

        // Botón Filtro
        btnFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Próximamente: Filtros", Toast.LENGTH_SHORT).show();
        });
    }


}