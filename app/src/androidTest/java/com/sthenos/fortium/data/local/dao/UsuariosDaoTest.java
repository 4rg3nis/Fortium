package com.sthenos.fortium.data.local.dao;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sthenos.fortium.data.local.FortiumDatabase;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.model.enums.UnidadMedida;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests Instrumentados para probar las operaciones de la base de datos.
 * Se ejecutan en el emulador.
 */
@RunWith(AndroidJUnit4.class)
public class UsuariosDaoTest {

    private FortiumDatabase baseDeDatosFantasma;
    private UsuariosDao usuariosDao;

    // @Before se ejecuta siempre antes de cada @Test.
    // Aquí preparamos nuestro "laboratorio".
    @Before
    public void crearBaseDeDatos() {
        Context context = ApplicationProvider.getApplicationContext();

        // Al cerrar la app, desaparece. No afecta a tus datos reales.
        baseDeDatosFantasma = Room.inMemoryDatabaseBuilder(context, FortiumDatabase.class)
                .allowMainThreadQueries() // Permitimos consultar en el hilo principal solo para los tests
                .build();

        usuariosDao = baseDeDatosFantasma.usuariosDao();
    }

    // @After se ejecuta siempre al terminar cada @Test.
    // Aquí limpiamos el "laboratorio".
    @After
    public void destruirBaseDeDatos() {
        baseDeDatosFantasma.close();
    }

    @Test
    public void insertarUsuario_y_obtenerUsuario_funcionaCorrectamente() {
        // Given (Dado un usuario nuevo que queremos guardar)
        Usuario usuarioDePrueba = new Usuario("Argenis", "Prueba", "01/01/2000", 75.0, 180.0, Genero.Masculino, UnidadMedida.KG);

        // When (Cuando lo insertamos en la BD y lo volvemos a leer)
        usuariosDao.insert(usuarioDePrueba);
        Usuario usuarioGuardado = usuariosDao.getUsuario();

        // Then (Entonces el usuario no puede ser nulo y su nombre debe coincidir)
        assertNotNull("El usuario no debería ser nulo tras guardarlo", usuarioGuardado);
        assertEquals("El nombre guardado no coincide", "Argenis", usuarioGuardado.getNombre());
    }

    @Test
    public void actualizarUsuario_modificaLosDatosEnLaBD() {
        // Given (Dado un usuario que ya está guardado en la base de datos)
        Usuario usuarioOriginal = new Usuario("Argenis", "Prueba", "01/01/2000", 75.0, 180.0, Genero.Masculino, UnidadMedida.KG);
        usuariosDao.insert(usuarioOriginal);

        Usuario usuarioModificable = usuariosDao.getUsuario();

        // When (Cuando le cambiamos el peso y hacemos update)
        usuarioModificable.setPesoActual(80.0);
        usuariosDao.update(usuarioModificable);

        // Then (Entonces al leerlo de nuevo, el peso debe ser el nuevo)
        Usuario usuarioTrasUpdate = usuariosDao.getUsuario();
        assertEquals(80.0, usuarioTrasUpdate.getPesoActual(), 0.01);
    }

    @Test
    public void borrarUsuario_loEliminaCompletamenteDeLaBD() {
        // Given (Dado un usuario ya guardado)
        Usuario usuario = new Usuario("Argenis", "Prueba", "01/01/2000", 75.0, 180.0, Genero.Masculino, UnidadMedida.KG);
        usuariosDao.insert(usuario);
        Usuario usuarioGuardado = usuariosDao.getUsuario();

        // When (Cuando lo borramos)
        usuariosDao.delete(usuarioGuardado);

        // Then (Entonces al buscarlo, debe devolver null)
        Usuario usuarioBorrado = usuariosDao.getUsuario();
        assertNull("El usuario debería ser nulo tras borrarlo", usuarioBorrado);
    }
}