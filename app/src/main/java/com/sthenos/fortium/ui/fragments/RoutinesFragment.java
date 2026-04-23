package com.sthenos.fortium.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.ui.activities.RutinaDetalleActivity;
import com.sthenos.fortium.ui.adapters.RutinaAdapter;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;

public class RoutinesFragment extends Fragment {

    private RecyclerView rvLibraryRoutines;
    private RutinaViewModel rutinaViewModel;
    private UsuarioViewModel usuarioViewModel;

    private LinearLayout layoutEmptyState;
    private TextInputEditText etSearchRoutines;
    private ImageButton btnFilter;
    private TextView btnImportJSON;
    private ExtendedFloatingActionButton fabCreateRoutine;
    private RutinaAdapter adapter;
    private int idUsuario = -1;

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

        setObservers();
        setupListeners();
    }

    private void setObservers() {
        usuarioViewModel.getUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            if(usuario != null){
                idUsuario = usuario.getId();
            }
        });

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

    private void initComponents(View view) {
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        rvLibraryRoutines = view.findViewById(R.id.rvLibraryRoutines);
        etSearchRoutines = view.findViewById(R.id.etSearchRoutines);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnImportJSON = view.findViewById(R.id.btnImportJSON);
        fabCreateRoutine = view.findViewById(R.id.fabCreateRoutine);

        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
    }

    private void setupRecyclerView() {
        rvLibraryRoutines.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RutinaAdapter();
        rvLibraryRoutines.setAdapter(adapter);
    }

    private void setupListeners() {
        // Botón FAB (Crear nueva rutina)
        fabCreateRoutine.setOnClickListener(v -> showCreateRoutineDialog());

        // Botón Importar JSON
        btnImportJSON.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Próximamente: Importar de archivos", Toast.LENGTH_SHORT).show();
        });

        // Botón Filtro
        btnFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Próximamente: Filtros", Toast.LENGTH_SHORT).show();
        });
    }

    private void showCreateRoutineDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_routine, null);

        TextInputEditText tietRoutineTitle = dialogView.findViewById(R.id.etRoutineTitle);
        TextInputEditText tietRoutineDesc = dialogView.findViewById(R.id.etRoutineDesc);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.Theme_Fortium) // Usa tu tema si tienes uno oscuro, o quita el segundo parámetro
                .setView(dialogView)
                .setBackground(new ColorDrawable(Color.TRANSPARENT))
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String title = tietRoutineTitle.getText().toString().trim();
            String desc = tietRoutineDesc.getText().toString().trim();

            if (title.isEmpty()) {
                tietRoutineTitle.setError("Required");
                return;
            }


            String fechaHoy = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault()).format(new java.util.Date());


            Rutina nuevaRutina = new Rutina(title, desc, fechaHoy);

            // Aqui insertamos la rutina, y como todavía sqlite no creó la rutina y el id es '0' pues directamente hacemos
            // aqui el cambio de activity
            rutinaViewModel.insert(nuevaRutina, idGenerado -> {
                dialog.dismiss();
                Toast.makeText(getContext(), "Rutina creada con éxito!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireActivity(), RutinaDetalleActivity.class);
                intent.putExtra("rutinaId", idGenerado);
                startActivity(intent);
            });
        });
        dialog.show();
    }
}