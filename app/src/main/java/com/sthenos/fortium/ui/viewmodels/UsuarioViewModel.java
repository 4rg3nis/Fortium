package com.sthenos.fortium.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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

    public LiveData<Usuario> getUsuarioActual(){
        return usuarioRepository.getUsuarioActual();
    }

    public void borrarTodos(){
        usuarioRepository.deleteAll();
    }
}
