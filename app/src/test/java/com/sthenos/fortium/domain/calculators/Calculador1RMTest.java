package com.sthenos.fortium.domain.calculators;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests Unitarios para la lógica matemática del cálculo del 1RM.
 * Se utiliza el patrón Given-When-Then para estructurar las pruebas.
 */
public class Calculador1RMTest {

    // El delta es el margen de error permitido al comparar números decimales
    private static final double DELTA = 0.01;

    @Test
    public void calcular1RM_conPocasRepeticiones_usaBrzyckiYFactorHombreJoven() {
        // Given (Dado que... preparamos los datos iniciales)
        double pesoLevantado = 100.0;
        int reps = 10;
        double rpe = 10.0; // Al fallo, sin repeticiones en reserva
        String genero = "Masculino";
        int edad = 25; // Hombre joven, factor biológico = 1.0

        // When (Cuando... ejecutamos el método que queremos probar)
        double resultadoObtenido = Calculador1RM.calcular1RMFinal(pesoLevantado, reps, rpe, genero, edad);

        // Then (Entonces... comprobamos si el resultado es el que dice la matemática)
        // Matemática Brzycki: 100 / (1.0278 - (0.0278 * 10)) = 133.37
        double resultadoEsperado = 133.37;

        // AssertEquals comprueba que el esperado y el obtenido sean iguales.
        assertEquals(resultadoEsperado, resultadoObtenido, DELTA);
    }

    @Test
    public void calcular1RM_conMuchasRepeticionesYRIR_usaEpleyYFactorMujerAdulta() {
        // Given (Dado un escenario de resistencia con Epley)
        double pesoLevantado = 50.0;
        int reps = 12;
        double rpe = 8.0; // RPE 8 significa 2 repeticiones en reserva  (Por lo que serian 14)
        String genero = "Femenino";
        int edad = 35; // Mujer adulta, factor biológico = 0.97

        // When (Cuando calculamos el 1RM)
        double resultadoObtenido = Calculador1RM.calcular1RMFinal(pesoLevantado, reps, rpe, genero, edad);

        // Then (Entonces el resultado debe coincidir con la fórmula de Epley ajustada)
        // Matemática Epley: 50 * (1 + (14 / 30.0)) = 73.33
        // Factor biológico: 73.33 * 0.97 = 71.13
        double resultadoEsperado = 71.13;

        assertEquals(resultadoEsperado, resultadoObtenido, DELTA);
    }

    @Test
    public void calcular1RM_conValoresInvalidos_devuelveCero() {
        // Given (Dado un usuario que introduce un peso de 0 por error)
        double pesoLevantado = 0.0;
        int reps = 10;
        double rpe = 8.0;
        String genero = "Masculino";
        int edad = 30;

        // When
        double resultadoObtenido = Calculador1RM.calcular1RMFinal(pesoLevantado, reps, rpe, genero, edad);

        // Then (El sistema debe devolver 0.0)
        double resultadoEsperado = 0.0;
        assertEquals(resultadoEsperado, resultadoObtenido, DELTA);
    }
}