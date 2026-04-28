package com.sthenos.fortium.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.model.enums.UnidadMedida;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateUserActivity extends AppCompatActivity {

    private TextInputEditText etDate, etWeight, etHeight, etName, etLastName;
    private TextInputLayout tilWeight, tilHeight, tilName, tilDate, tilLastName;
    private MaterialButtonToggleGroup toggleWeight, toggleGender;
    private ImageButton btnBack;
    private MaterialButton btnSaveContinue;
    private UsuarioViewModel usuarioViewModel;

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
        //usuarioViewModel.borrarTodos();
        setListeners();
    }

    private void setListeners() {
        etDate.setOnClickListener(v -> {
            setDate();
        });
        btnBack.setOnClickListener(v -> finish());
        btnSaveContinue.setOnClickListener(v -> {
            if(validarFormulario()){
                guardarDatosYContinuar();
            }
        });
    }

    private boolean validarFormulario() {
        boolean isValido = true;
        String nombre = etName.getText().toString().trim();
        String apellidos = etLastName.getText().toString().trim();
        String fecha = etDate.getText().toString().trim();
        String strPeso = etWeight.getText().toString();
        String strHeight = etHeight.getText().toString();
        double peso = strPeso.isEmpty() ? 0 : Double.parseDouble(strPeso);
        double altura = strHeight.isEmpty() ? 0 : Double.parseDouble(strHeight);

        if(nombre.isEmpty()){
            tilName.setError("Full name is required");
            isValido = false;
        } else{
            tilName.setError(null);
        }

        if(apellidos.isEmpty()){
            tilLastName.setError("Last name is required");
            isValido = false;
        } else{
            tilLastName.setError(null);
        }

        if(fecha.isEmpty()){
            tilDate.setError("Date is required");
            isValido = false;
        } else{
            tilDate.setError(null);
        }

        if(peso <= 0){
            tilWeight.setError("Weight must be > 0");
            isValido = false;
        } else{
            tilWeight.setError(null);
        }

        if(altura <= 0){
            tilHeight.setError("Height must be > 0");
            isValido = false;
        } else {
            tilHeight.setError(null);
        }

        if(toggleGender.getCheckedButtonId() == -1){
            isValido = false;
            Toast.makeText(this, "Seleciona un genero", Toast.LENGTH_SHORT).show();
        }

        if(toggleWeight.getCheckedButtonId() == -1){
            isValido = false;
            Toast.makeText(this, "Seleciona una medida de peso", Toast.LENGTH_SHORT).show();
        }
        return isValido;
    }

    private void guardarDatosYContinuar() {
        String nombre = etName.getText().toString().trim();
        String apellidos = etLastName.getText().toString().trim();
        String fecha = etDate.getText().toString().trim();
        double peso = Double.parseDouble(etWeight.getText().toString());
        double altura = Double.parseDouble(etHeight.getText().toString());
        Genero genero = Genero.Otros;
        int selectedGenderId = toggleGender.getCheckedButtonId();
        if (selectedGenderId == R.id.btnMale) genero = Genero.Masculino;
        else if (selectedGenderId == R.id.btnFemale) genero = Genero.Femenino;

        UnidadMedida medida = UnidadMedida.LB;
        int selectedWeightId = toggleWeight.getCheckedButtonId();
        if (selectedWeightId == R.id.btn_kg) medida = UnidadMedida.KG;

        usuarioViewModel.guardarUsuario(new Usuario(nombre, apellidos, fecha, peso, altura, genero, medida));
        guardarDatosSharedPref(nombre);
        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
        cambiarActividad();
    }

    private void guardarDatosSharedPref(String name) {
        SharedPreferences prefs = getSharedPreferences("FortiumApp", MODE_PRIVATE);
        prefs.edit().putBoolean("perfilCreado", true).apply();
    }

    private void cambiarActividad() {
        Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setDate() {
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(selection));

            etDate.setText(dateString);
        });
    }

    private void initComponents() {
        usuarioViewModel = new UsuarioViewModel(getApplication());
        etDate = findViewById(R.id.etDate);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        toggleWeight = findViewById(R.id.toggleWeight);
        btnBack = findViewById(R.id.btnBack);
        btnSaveContinue = findViewById(R.id.btnSaveContinue);
        etName = findViewById(R.id.etName);
        toggleGender = findViewById(R.id.toggleGender);
        tilWeight = findViewById(R.id.tilWeight);
        tilHeight = findViewById(R.id.tilHeight);
        tilName = findViewById(R.id.tilName);
        tilDate = findViewById(R.id.tilDate);
        tilLastName = findViewById(R.id.tilLastName);
        etLastName = findViewById(R.id.etLastName);
    }
}