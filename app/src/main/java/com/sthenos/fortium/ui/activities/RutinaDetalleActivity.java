package com.sthenos.fortium.ui.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.sthenos.fortium.R;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;

public class RutinaDetalleActivity extends AppCompatActivity {

    private int rutinaId;
    private TextView tvRoutineName, tvTotalExercises, tvTotalSets, tvRoutineDesc, tvRoutineDate;
    private RutinaViewModel rutinaViewModel;
    private ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rutina_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rutinaId = getIntent().getIntExtra("rutinaId", -1);

        if(rutinaId == -1){
            finish();
            return;
        }

        initComponents();
        setupViewModel();
        setListeners();
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupViewModel() {
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        rutinaViewModel.getRutinaById(rutinaId).observe(this,rutina -> {
            if(rutina != null) {
                tvRoutineName.setText(rutina.getNombre());
                tvRoutineDesc.setText(rutina.getDescripcion());
                tvRoutineDate.setText("Creada: " + rutina.getFechaCreacion());

                // Aqui se implementaria la logica de obtener el total de ejercicios y series
            }
        });
    }

    private void initComponents() {
        tvRoutineName = findViewById(R.id.tvRoutineName);
        tvTotalExercises = findViewById(R.id.tvTotalExercises);
        tvTotalSets = findViewById(R.id.tvTotalSets);
        tvRoutineDesc = findViewById(R.id.tvRoutineDesc);
        tvRoutineDate = findViewById(R.id.tvRoutineDate);
        btnBack = findViewById(R.id.btnBack);

    }
}