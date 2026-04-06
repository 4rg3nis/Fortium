package com.sthenos.fortium.ui.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.sthenos.fortium.data.repository.UsuarioRepository;
import com.sthenos.fortium.model.entities.Usuario;

public class UsuarioViewModel extends AndroidViewModel {

    private final UsuarioRepository usuarioRepository;

    public UsuarioViewModel(@NonNull Application application) {
        super(application);
        usuarioRepository = UsuarioRepository.getInstance(application);
    }

    public void guardarUsuario(Usuario usuario){
        usuarioRepository.insert(usuario);
    }

    public void borrarTodos(){
        usuarioRepository.deleteAll();
    }
}
