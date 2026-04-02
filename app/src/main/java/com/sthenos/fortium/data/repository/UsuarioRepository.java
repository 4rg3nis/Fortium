package com.sthenos.fortium.data.repository;

import android.app.Application;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.data.local.dao.UsuariosDao;
import com.sthenos.fortium.model.entities.Usuario;

public class UsuarioRepository {
    private final UsuariosDao usuariosDao;

    private static UsuarioRepository instance;

    private UsuarioRepository(Application application){
        FortiumDatabase db = FortiumDatabase.getInstance(application);
        usuariosDao = db.usuariosDao();
    }

    public UsuarioRepository getInstance(Application application){
        if(instance == null){
            synchronized (UsuarioRepository.class){
                if(instance == null)
                    instance = new UsuarioRepository(application);
            }
        }
        return instance;
    }


}
