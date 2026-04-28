package com.sthenos.fortium.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.repository.UsuarioRepository;
import com.sthenos.fortium.model.entities.Usuario;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class UsuarioViewModel extends AndroidViewModel {

    private final UsuarioRepository usuarioRepository;

    public UsuarioViewModel(@NonNull Application application) {
        super(application);
        usuarioRepository = UsuarioRepository.getInstance(application);
    }

    public void guardarUsuario(Usuario usuario){
        usuarioRepository.insert(usuario);
    }

    public LiveData<Usuario> getUsuarioActual(){
        return usuarioRepository.getUsuarioActual();
    }

    public void borrarTodos(){
        usuarioRepository.deleteAll();
    }

    public void updateUsuario(Usuario usuarioActual) {
        usuarioRepository.update(usuarioActual);
    }

    /**
     * Calcula la edad a partir de la fecha de nacimiento. Obtiene la fecha de hoy y la fecha de nacimiento se resta a la de hoy.
     * @param usuario
     * @return
     */
    public int calcularEdad(Usuario usuario) {
        if (usuario == null || usuario.getFechaNacimiento() == null) {
            return 0; // Evita el crash si el objeto no ha cargado
        }
        String fechaNacimiento = usuario.getFechaNacimiento();
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaNac = LocalDate.parse(fechaNacimiento, formato);
        return Period.between(fechaNac, fechaActual).getYears();
    }

}
