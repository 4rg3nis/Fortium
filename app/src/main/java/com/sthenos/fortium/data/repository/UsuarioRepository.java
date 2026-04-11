package com.sthenos.fortium.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.data.local.dao.UsuariosDao;
import com.sthenos.fortium.model.entities.Usuario;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsuarioRepository {
    private  final UsuariosDao usuariosDao;
    private  final ExecutorService executorService;
    private static UsuarioRepository instance;

    private UsuarioRepository(Application application){
        FortiumDatabase db = FortiumDatabase.getInstance(application);
        usuariosDao = db.usuariosDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public static UsuarioRepository getInstance(Application application){
        if(instance == null){
            synchronized (UsuarioRepository.class){
                if(instance == null)
                    instance = new UsuarioRepository(application);
            }
        }
        return instance;
    }

    public void insert(Usuario usuario){
        executorService.execute(() -> usuariosDao.insert(usuario));
    }


    public void deleteAll() {
        executorService.execute(() -> usuariosDao.deleteAll());
    }

    public LiveData<Usuario> getUsuarioActual(){
        return usuariosDao.getUsuarioActual();
    }
}
