package com.sthenos.fortium.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.model.queries.HistorialSesion;
import com.sthenos.fortium.model.queries.RutinaResumen;
import com.sthenos.fortium.ui.activities.DetallesSesionActivity;
import com.sthenos.fortium.ui.activities.HistorialActivity;
import com.sthenos.fortium.ui.activities.SettingsActivity;
import com.sthenos.fortium.ui.activities.WorkoutActivity;
import com.sthenos.fortium.ui.adapters.HistorialAdapter;
import com.sthenos.fortium.ui.adapters.RutinaAdapter;
import com.sthenos.fortium.ui.viewmodels.EntrenamientoViewModel;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;
import com.sthenos.fortium.utils.JsonExporter;

import java.io.OutputStream;

public class HomeFragment extends Fragment {

    private TextView tvSaludo, tvPeso, tvViewAll, tvEmptyHistorial, tvVerHistorialCompleto;
    private RecyclerView rvRutinas;
    private RutinaViewModel rutinaViewModel;
    private MaterialButton btnEmpezarEntrenamiento;
    private UsuarioViewModel usuarioViewModel;
    private ImageButton btnSettings;
    private RutinaAdapter adapterRutina;
    private HistorialAdapter historialAdapter;
    private EntrenamientoViewModel entrenamientoViewModel;

    private ActivityResultLauncher<Intent> exportarRutinaLauncher;
    private String jsonAExportar = "";

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        setRecyclerView();
        setObservers();
        setListeners();
        setupHistorialReciente(view);
    }

    /**
     * Configura el RecyclerView para mostrar el historial de sesiones recientes.
     * Si no hay sesiones recientes, muestra un mensaje de "No hay sesiones recientes".
     *
     * @param view La vista del fragmento.
     */
    private void setupHistorialReciente(View view) {
        RecyclerView rvHistorial = view.findViewById(R.id.rvHistorialReciente);

        historialAdapter = new HistorialAdapter(false, new HistorialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistorialSesion sesion) {
                Intent intent = new android.content.Intent(getContext(), DetallesSesionActivity.class);
                intent.putExtra("sesionId", sesion.sesionId);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(HistorialSesion sesion) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("¿Eliminar entrenamiento?")
                        .setMessage("Vas a borrar tu sesión '" + (sesion.nombreRutina != null ? sesion.nombreRutina : "Libre") + "'. Se borrarán todas las series y volumen de tu historial. Esta acción no se puede deshacer.")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            // Llamamos al ViewModel para que la fulmine
                            entrenamientoViewModel.eliminarSesionCompleta(sesion.sesionId);
                            android.widget.Toast.makeText(getContext(), "Sesión eliminada", android.widget.Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
        rvHistorial.setAdapter(historialAdapter);

        entrenamientoViewModel.getHistorialReciente().observe(getViewLifecycleOwner(), sesiones -> {
            if (sesiones != null && !sesiones.isEmpty()) {
                historialAdapter.setSesiones(sesiones);

                // Mostrar lista, ocultar mensaje de vacío
                rvHistorial.setVisibility(View.VISIBLE);
                tvEmptyHistorial.setVisibility(View.GONE);
            } else {
                // Ocultar lista, mostrar mensaje de vacío
                rvHistorial.setVisibility(View.GONE);
                tvEmptyHistorial.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setObservers() {
        rutinaViewModel.getRutinasConResumen().observe(getViewLifecycleOwner(), rutinas -> {
            if (rutinas != null && !rutinas.isEmpty()) {

                // Calculamos el límite para no pasarnos del tamaño real de la lista
                int limite = Math.min(rutinas.size(), 3);

                // Le pasamos al adaptador solo el trocito recortado de la lista
                adapterRutina.setRutinas(rutinas.subList(0, limite));

            }
        });
        usuarioViewModel.getUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                setPeso(usuario.getPesoActual());
                setSaludo(usuario.getNombre());
            }
        });
    }

    private void setListeners() {
        btnEmpezarEntrenamiento.setOnClickListener( v -> {
            Intent intent = new Intent(requireContext(), WorkoutActivity.class);
            intent.putExtra("rutinaId", -1);
            startActivity(intent);
        });
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SettingsActivity.class);
            startActivity(intent);
        });

        tvVerHistorialCompleto.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), HistorialActivity.class);
                startActivity(intent);
        });

        tvViewAll.setOnClickListener(v -> {
            Fragment selectedFragment = new RoutinesFragment();;
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit();
        });
    }

    private void setRecyclerView() {
        rvRutinas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRutinas.setHasFixedSize(true);
        rvRutinas.setAdapter(adapterRutina);
    }

    private void setPeso(Double peso) {
        tvPeso.setText(peso + " kg");
    }

    private void setSaludo(String nombre) {
        tvSaludo.setText("Hola, " + nombre);
    }

    private void initComponents(View view) {
        tvSaludo = view.findViewById(R.id.tvSaludo);
        tvPeso = view.findViewById(R.id.tvPeso);
        tvViewAll = view.findViewById(R.id.tvViewAll);
        rvRutinas = view.findViewById(R.id.rvRutinas);
        btnEmpezarEntrenamiento = view.findViewById(R.id.btnEmpezarEntrenamiento);
        btnSettings = view.findViewById(R.id.btnSettings);
        tvEmptyHistorial = view.findViewById(R.id.tvEmptyHistorial);
        tvVerHistorialCompleto = view.findViewById(R.id.tvVerHistorialCompleto);

        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
        entrenamientoViewModel = new ViewModelProvider(this).get(EntrenamientoViewModel.class);

        setUpAdpaters();

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
    }

    /**
     * Configura el adaptador para el RecyclerView de rutinas.
     */
    private void setUpAdpaters() {
        adapterRutina = new RutinaAdapter(new RutinaAdapter.OnRutinaOpcionesListener() {
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
    }

}