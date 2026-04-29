package com.sthenos.fortium.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.ui.activities.SettingsActivity;
import com.sthenos.fortium.ui.adapters.HistorialAdapter;
import com.sthenos.fortium.ui.adapters.RutinaAdapter;
import com.sthenos.fortium.ui.viewmodels.EntrenamientoViewModel;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;

public class HomeFragment extends Fragment {

    private TextView tvSaludo, tvPeso, tvViewAll, tvEmptyHistorial;
    private RecyclerView rvRutinas;
    private RutinaViewModel rutinaViewModel;
    private MaterialButton btnEmpezarEntrenamiento;
    private UsuarioViewModel usuarioViewModel;
    private ImageButton btnSettings;
    private int idUsuario = -1;
    private RutinaAdapter adapterRutina;
    private HistorialAdapter historialAdapter;
    private EntrenamientoViewModel entrenamientoViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
     * @param view La vista del fragmento.
     */
    private void setupHistorialReciente(View view) {
        androidx.recyclerview.widget.RecyclerView rvHistorial = view.findViewById(R.id.rvHistorialReciente);

        historialAdapter = new HistorialAdapter();
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
        rutinaViewModel.getAllRutinas().observe(getViewLifecycleOwner(), rutinas -> {
            if (rutinas != null && !rutinas.isEmpty()) {

                // Calculamos el límite para no pasarnos del tamaño real de la lista
                int limite = Math.min(rutinas.size(), 3);

                // Le pasamos al adaptador solo el trocito recortado de la lista
                adapterRutina.setRutinas(rutinas.subList(0, limite));

            }
        });
        usuarioViewModel.getUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                idUsuario = usuario.getId();
                setPeso(usuario.getPesoActual());
                setSaludo(usuario.getNombre());
                Log.d("DEBUG_FORTIUM", "Usuario cargado con ID: " + idUsuario);
            } else {
                Log.d("DEBUG_FORTIUM", "Esperando a que el usuario se cargue...");
            }
        });
    }

    private void setListeners() {
        btnEmpezarEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numeroAleatorio = (int) (Math.random() * 100);
                Rutina nuevaRutina = new Rutina(
                        // TODO: Cambiar esto..
                        idUsuario, // usuarioId simulado
                        "Rutina " + numeroAleatorio,
                        "Creada para demostración de base de datos",
                        "2023-11-11"
                );
                rutinaViewModel.insert(nuevaRutina);
                android.widget.Toast.makeText(getContext(),
                        "¡Rutina creada con éxito!", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), SettingsActivity.class);
                startActivity(intent);
            }});
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

        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
        entrenamientoViewModel = new ViewModelProvider(this).get(EntrenamientoViewModel.class);
        adapterRutina = new RutinaAdapter();
    }

}