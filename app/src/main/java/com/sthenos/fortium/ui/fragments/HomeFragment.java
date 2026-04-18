package com.sthenos.fortium.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sthenos.fortium.R;
import com.sthenos.fortium.data.repository.EntrenamientoRepository;
import com.sthenos.fortium.model.entities.Rutina;
import com.sthenos.fortium.ui.activities.MainActivity;
import com.sthenos.fortium.ui.adapters.RutinaAdapter;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView tvSaludo, tvPeso, tvRm;
    private RecyclerView rvRutinas;
    private RutinaViewModel rutinaViewModel;
    private MaterialButton btnEmpezarEntrenamiento;
    private UsuarioViewModel usuarioViewModel;
    private int idUsuairo = -1;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        setExampleRm();
    }

    private void setExampleRm() {
        double pesoLevantado = 120.0;
        int repeticiones = 5;

        double e1rmCalculado = EntrenamientoRepository.getInstance(requireActivity().getApplication()).calcular1RM(pesoLevantado, repeticiones);

        tvRm.setText(String.format("%.1f", e1rmCalculado));
    }

    private void setObservers() {
        rutinaViewModel.getAllRutinas().observe(getViewLifecycleOwner(), rutinas -> {
            ((RutinaAdapter) rvRutinas.getAdapter()).setRutinas(rutinas);
        });
        usuarioViewModel.getUsuarioActual().observe(this, usuario -> {
            idUsuairo = usuario.getId();
            setPeso(usuario.getPesoActual());
            setSaludo(usuario.getNombre());
        });
    }

    private void setListeners() {
        btnEmpezarEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numeroAleatorio = (int) (Math.random() * 100);
                Rutina nuevaRutina = new Rutina(
                        idUsuairo, // usuarioId simulado
                        "Rutina " + numeroAleatorio,
                        "Creada para demostración de base de datos",
                        "2023-11-11"
                );
                rutinaViewModel.insert(nuevaRutina);
                android.widget.Toast.makeText(getContext(),
                        "¡Rutina creada con éxito!", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerView() {
        rvRutinas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRutinas.setHasFixedSize(true);

        final RutinaAdapter adapter = new RutinaAdapter();
        rvRutinas.setAdapter(adapter);
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
        rvRutinas = view.findViewById(R.id.rvRutinas);
        btnEmpezarEntrenamiento = view.findViewById(R.id.btnEmpezarEntrenamiento);
        tvRm = view.findViewById(R.id.tvRm);

        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
    }

}