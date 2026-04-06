package com.sthenos.fortium.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.sthenos.fortium.R;

public class CreateUserActivity extends AppCompatActivity {

    private TextInputEditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initComponents();
        setDate();
    }

    private void setDate() {
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Selecciona tu fecha");

                // Seleccionar la fecha de hoy por defecto
                builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());

                final MaterialDatePicker<Long> materialDatePicker = builder.build();

                // Mostrar el calendario
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

                // Escuchar cuando el usuario pulsa Aceptar
                materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                    // Convertir los milisegundos a un formato de fecha legible (dd/MM/yyyy)
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                    String dateString = sdf.format(new java.util.Date(selection));

                    etDate.setText(dateString);
                });
            }
        });
    }

    private void initComponents() {
        etDate = findViewById(R.id.etDate);
    }
}