package com.sthenos.fortium.domain.calculators;

import com.sthenos.fortium.model.enums.Genero;
import com.sthenos.fortium.utils.Converters;

/**
 * Clase para poder calcular el 1RM
 * @author Argenis
 */
public class Calculador1RM {

    private static final double BRZYCKI_CONST_1 = 1.0278;
    private static final double BRZYCKI_CONST_2 = 0.0278;
    private static final double EPLEY_DIVISOR = 30.0;
    private static final double RPE_MAXIMO = 10.0;

    /**
     * Calcula la estimación de 1RM final aplicando un modelo híbrido y ajustes biológicos.
     *
     * El proceso de cálculo sigue estos pasos:
     * 1. Normalización del RPE: Convierte el esfuerzo percibido en "Repeticiones en Reserva" (RIR).
     * 2. Cálculo de Repeticiones Virtuales: Suma las repeticiones reales y las dejadas en reserva.
     * 3. Selección de Modelo Matemático:
     *    - Hasta 12 reps totales: Utiliza Brzycki (fórmula de alta precisión para fuerza).
     *    - Más de 12 reps totales: Utiliza Epley (fórmula de estabilidad para resistencia).
     * 4. Ajuste Bio-Demográfico: Aplica un coeficiente basado en género y edad.
     *
     * @param pesoLevantado Carga utilizada en la serie (kg). Debe ser mayor a 0.
     * @param reps          Número de repeticiones completadas.
     * @param rpe           Escala de Esfuerzo Percibido (1.0 a 10.0). Si es 0 o inválido, se asume 10 (al fallo).
     * @param genero        Género del usuario según el enumerado {@link Genero}.
     * @param edad          Edad actual del usuario en años.
     * @return El peso máximo teórico (1RM) redondeado a dos decimales. Retorna 0.0 si los datos son inválidos.
     */
    public static double calcular1RMFinal(double pesoLevantado, int reps, double rpe, String genero, int edad) {

        if (pesoLevantado <= 0 || reps <= 0) return 0.0;

        // Calcular Repeticiones
        double rpeEfectivo = (rpe > 0 && rpe <= RPE_MAXIMO) ? rpe : RPE_MAXIMO;
        double repsTotales = reps + (RPE_MAXIMO - rpeEfectivo);

        double rmBase;

        // Selección de formula según volumen
        if (repsTotales <= 12) {
            // Brzycki: Ideal para bajas repeticiones
            rmBase = pesoLevantado / (BRZYCKI_CONST_1 - (BRZYCKI_CONST_2 * repsTotales));
        } else {
            // Epley: Más estable para altas repeticiones (resistencia)
            rmBase = pesoLevantado * (1 + (repsTotales / EPLEY_DIVISOR));
        }

        // Ajuste biológico (género y edad)
        double cBio = obtenerFactorBio(genero, edad);

        return Math.round(rmBase * cBio * 100.0) / 100.0;
    }

    /**
     * Determina el factor de corrección biológico (cBio) basado en la demografía del usuario.
     *
     * Los factores consideran la eficiencia neuromuscular y la densidad ósea promedio:
     * - Jóvenes (<30 años): Máximo potencial neuromuscular.
     * - Adultos (30-50 años): Ligera corrección por inicio de sarcopenia (pérdida de fuerza, masa y equilibrio entre otras).
     * - Senior (>50 años): Ajuste por reducción natural de fibras.
     * - Género Femenino: Incluye un factor de corrección (1.02) en edades tempranas debido
     *   a la mayor capacidad de las mujeres para trabajar con porcentajes altos de carga.
     *
     * @param genero String representativo del género (obtenido vía Converters).
     * @param edad   Edad cronológica del usuario.
     * @return Coeficiente multiplicador (0.82 - 1.02).
     */
    private static double obtenerFactorBio(String genero, int edad) {
        boolean esHombre = genero.equalsIgnoreCase(Converters.fromGenero(Genero.Masculino));
        if (esHombre) {
            if (edad < 30) return 1.0;
            if (edad <= 50) return 0.95;
            return 0.85;
        } else {
            if (edad < 30) return 1.02;
            if (edad <= 50) return 0.97;
            return 0.82;
        }
    }
}