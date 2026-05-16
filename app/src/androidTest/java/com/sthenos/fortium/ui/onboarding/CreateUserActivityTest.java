package com.sthenos.fortium.ui.onboarding;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sthenos.fortium.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests Instrumentados de Interfaz de Usuario usando Espresso.
 * Simula la interacción de un usuario real con la pantalla.
 */
@RunWith(AndroidJUnit4.class)
public class CreateUserActivityTest {

    // Debe abrir automáticamente la pantalla CreateUserActivity en el emulador.
    @Rule
    public ActivityScenarioRule<CreateUserActivity> activityRule =
            new ActivityScenarioRule<>(CreateUserActivity.class);

    @Test
    public void TC10_escribirNombre_seMuestraCorrectamenteEnLaPantalla() {
        // Given: La pantalla se ha abierto sola gracias a la regla de arriba.

        // When: El robot invisible busca el campo "Nombre", escribe "Argenis" y esconde el teclado
        Espresso.onView(ViewMatchers.withId(R.id.etName))
                .perform(ViewActions.typeText("Argenis"), ViewActions.closeSoftKeyboard());

        // Then: Comprobamos que el campo de texto ha registrado correctamente la escritura
        Espresso.onView(ViewMatchers.withId(R.id.etName))
                .check(ViewAssertions.matches(ViewMatchers.withText("Argenis")));
    }

    @Test
    public void TC11_elementosPrincipales_estanVisiblesAlAbrirLaPantalla() {
        // Un test súper básico, comprobar que la pantalla no sale en blanco

        // Comprobamos que el botón de Guardar existe y está visible
        Espresso.onView(ViewMatchers.withId(R.id.btnSaveContinue))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Comprobamos que el botón de ir atrás también se ha pintado en la pantalla
        Espresso.onView(ViewMatchers.withId(R.id.btnBack))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}