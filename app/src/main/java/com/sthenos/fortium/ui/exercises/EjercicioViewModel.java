package com.sthenos.fortium.ui.exercises;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sthenos.fortium.data.repository.EjercicioRepository;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.enums.Equipo;
import com.sthenos.fortium.utils.Converters;

import java.util.List;
import java.util.function.Consumer;

public class EjercicioViewModel extends AndroidViewModel {

    private final EjercicioRepository repository;

    // Esto es para pasar filtros de búsqueda.
    private final MutableLiveData<String> filtroMusculo = new MutableLiveData<>(null);


    public EjercicioViewModel(@NonNull Application application) {
        super(application);
        repository = EjercicioRepository.getInstance(application);
    }

    public LiveData<List<Ejercicio>> getAllEjercicios() {
        return repository.getAllEjercicios();
    }

    public void insertEjercicio(Ejercicio ejercicio) {
        repository.insertEjercicio(ejercicio);
    }

    public void updateEjercicio(Ejercicio ejercicioAEditar) {
        repository.updateEjercicio(ejercicioAEditar);
    }

    public LiveData<Ejercicio> getEjercicioById(int id) {
        return repository.getEjercicioById(id);
    }

    public void deleteEjercicio(Ejercicio ejercicio) {
        repository.deleteEjercicio(ejercicio);
    }


    public LiveData<String> getFiltroMusculo() {
        return filtroMusculo;
    }

    public void setFiltroMusculo(String musculo) {
        filtroMusculo.setValue(musculo);
    }

    public void guardarEjercicio(int idActual, Ejercicio ejercicioExistente, String nombre, String musculo, String descripcion, String equipoString, String imagenPath, Runnable onSuccess, Consumer<String> onError) {

        if (nombre.isEmpty() || musculo.isEmpty()) {
            onError.accept("Nombre y Músculo son obligatorios");
            return;
        }

        // El ViewModel se encarga de la lógica de transformación
        String equipoFormateado = equipoString.replace(" ", "_").toUpperCase();
        Equipo equipoEnum = Converters.toEquipo(equipoFormateado);

        if (equipoEnum == null) {
            onError.accept("Equipo no válido, selecciona uno de la lista");
            return;
        }

        if (idActual != -1 && ejercicioExistente != null) {
            // Modo edición
            ejercicioExistente.setImagenPath(imagenPath);
            ejercicioExistente.setNombre(nombre);
            ejercicioExistente.setGrupoMuscularPrincipal(musculo);
            ejercicioExistente.setDescripcionTecnica(descripcion);
            ejercicioExistente.setEquipo(equipoEnum);

            updateEjercicio(ejercicioExistente);
            onSuccess.run();
        } else {
            // Modo creación
            Ejercicio nuevoEjercicio = new Ejercicio(nombre, musculo, false, descripcion, equipoEnum, imagenPath);
            insertEjercicio(nuevoEjercicio);
            onSuccess.run();
        }
    }
}
