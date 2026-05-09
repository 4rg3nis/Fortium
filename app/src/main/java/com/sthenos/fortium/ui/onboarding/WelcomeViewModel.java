package com.sthenos.fortium.ui.onboarding;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.sthenos.fortium.utils.JsonImporter;

/**
 * ViewModel para la pantalla de bienvenida.
 * @author Argenis
 */
public class WelcomeViewModel extends AndroidViewModel {
    public WelcomeViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Comprueba en las preferencias si el usuario ya ha creado un perfil.
     */
    public boolean isPerfilCreado() {
        SharedPreferences prefs = getApplication().getSharedPreferences("FortiumApp", Context.MODE_PRIVATE);
        return prefs.getBoolean("perfilCreado", false);
    }

    /**
     * Delega la importación de datos al hilo secundario sin bloquear la interfaz.
     */
    public void importarDatos(Uri uri, JsonImporter.ImportCallback callback) {
        JsonImporter.ejecutarImportacionCompleta(getApplication(), uri, callback);
    }
}
