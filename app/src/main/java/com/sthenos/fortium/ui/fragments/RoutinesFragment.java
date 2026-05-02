package com.sthenos.fortium.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.sthenos.fortium.model.queries.RutinaResumen;
import com.sthenos.fortium.ui.activities.RutinaDetalleActivity;
import com.sthenos.fortium.ui.adapters.RutinaAdapter;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;
import com.sthenos.fortium.utils.JsonExporter;
import com.sthenos.fortium.utils.JsonImporter;

import java.util.Locale;

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

    private ActivityResultLauncher<Intent> exportarRutinaLauncher;
    private ActivityResultLauncher<Intent> importarRutinaLauncher;
    private String jsonAExportar = "";

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
        // Observamos los datos de la base de datos en tiempo real
        rutinaViewModel.getRutinasConResumen().observe(getViewLifecycleOwner(), rutinas -> {
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

        initExportImport();
    }

    private void initExportImport() {
        exportarRutinaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null && !jsonAExportar.isEmpty()) {
                            boolean exito = JsonExporter.exportarStringAJson(requireContext(), uri, jsonAExportar);

                            if (exito) {
                                Toast.makeText(getContext(), "¡Rutina exportada con éxito!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error al escribir el archivo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        importarRutinaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            String jsonLeido = JsonImporter.leerArchivoComoString(requireContext(), uri);

                            if (jsonLeido != null && !jsonLeido.isEmpty()) {
                                rutinaViewModel.importarRutinaFromJson(jsonLeido,
                                        () -> Toast.makeText(getContext(), "¡Rutina importada con éxito!",Toast.LENGTH_SHORT).show(),
                                        (error) -> Toast.makeText(getContext(), "Error al importar: " + error, Toast.LENGTH_LONG).show()
                                );
                            } else {
                                Toast.makeText(getContext(), "No se pudo leer el archivo JSON", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void setupRecyclerView() {
        rvLibraryRoutines.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RutinaAdapter(new RutinaAdapter.OnRutinaOpcionesListener() {
            @Override
            public void onExportar(RutinaResumen rutina) {
                rutinaViewModel.generarJsonDeRutina(rutina.rutina, jsonGenerado -> {
                    // Guardamos el texto generado en nuestra variable global
                    jsonAExportar = jsonGenerado;

                    // Abrimos el explorador de archivos para que elija dónde guardarlo
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/json"); // Tipo de archivo

                    String nombreSugerido = "rutina_" + rutina.rutina.getNombre().toLowerCase().replace(" ", "_") + ".json";
                    intent.putExtra(Intent.EXTRA_TITLE, nombreSugerido);

                    exportarRutinaLauncher.launch(intent);
                });
            }

            @Override
            public void onEliminar(RutinaResumen rutina) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("¿Eliminar Rutina?")
                        .setMessage("¿Estás seguro de que quieres borrar '" + rutina.rutina.getNombre() + "'? Tu historial de entrenamientos se mantendrá.")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            rutinaViewModel.deleteRutina(rutina.rutina.getId());
                            Toast.makeText(getContext(), "Rutina eliminada", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
        rvLibraryRoutines.setAdapter(adapter);
    }

    private void setupListeners() {
        // Botón FAB (Crear nueva rutina)
        fabCreateRoutine.setOnClickListener(v -> showCreateRoutineDialog());

        // Botón Importar JSON
        btnImportJSON.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            importarRutinaLauncher.launch(intent);
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

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.Theme_Fortium)
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


            String fechaHoy = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new java.util.Date());


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