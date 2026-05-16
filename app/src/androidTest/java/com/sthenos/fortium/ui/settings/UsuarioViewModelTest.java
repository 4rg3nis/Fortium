package com.sthenos.fortium.ui.settings;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.model.enums.UnidadMedida;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Tests para la lógica de negocio del UsuarioViewModel.
 */
@RunWith(AndroidJUnit4.class)
public class UsuarioViewModelTest {

    private UsuarioViewModel viewModel;

    @Before
    public void setUp() {
        // Obtenemos un contexto real de la aplicación para poder instanciar el AndroidViewModel
        Application app = ApplicationProvider.getApplicationContext();
        viewModel = new UsuarioViewModel(app);
    }

    @Test
    public void TC07_calcularEdad_conFechaValida_devuelveEdadCorrectaDinamicamente() {
        // Given (Dado un usuario nacido el 1 de enero del año 2000)
        // Usamos datos randomms para los campos que no importan en este test
        Usuario usuarioPrueba = new Usuario(
                "Test", "Test", "01/01/2000",
                75.0, 180.0, Genero.Masculino, UnidadMedida.KG
        );

        // When (Cuando el ViewModel calcula su edad)
        int edadObtenida = viewModel.calcularEdad(usuarioPrueba);

        // Then (Entonces el resultado debe ser el año actual menos 2000)
        // Calculamos lo esperado dinámicamente para que el test nunca caduque
        int anioActual = LocalDate.now().getYear();
        int edadEsperada = anioActual - 2000;

        assertEquals("La edad calculada no coincide con los años transcurridos", edadEsperada, edadObtenida);
    }

    @Test
    public void TC08_calcularEdadDesdeString_conFechaInvalida_atrapaErrorYDevuelveCero() {
        // Given (Dada una fecha imposible que haría crashear la clase LocalDate)
        String fechaImposible = "32/13/1999";

        // When (Cuando intentamos calcular la edad)
        int edadObtenida = viewModel.calcularEdadDesdeString(fechaImposible);

        // Then (Entonces el try-catch del ViewModel debe actuar y devolver 0)
        assertEquals("Ante una fecha imposible, el sistema debe protegerse y devolver 0", 0, edadObtenida);
    }

    @Test
    public void TC09_calcularEdadDesdeString_conTextoBasura_atrapaErrorYDevuelveCero() {
        // Given (Dado un texto que el usuario ha introducido intentando romper la app)
        String textoBasura = "Hola mundo!";

        // When (Cuando intentamos calcular la edad)
        int edadObtenida = viewModel.calcularEdadDesdeString(textoBasura);

        // Then (Entonces la app no debe crashear, debe devolver 0)
        assertEquals("Ante texto basura, el sistema debe protegerse y devolver 0", 0, edadObtenida);
    }
}