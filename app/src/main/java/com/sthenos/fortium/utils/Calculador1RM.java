package com.sthenos.fortium.utils;

/**
 * Clase para poder calcular el 1RM
 * @author Argenis
 */
public class Calculador1RM {

    /**
     * Calcula el 1RM final usando la formula de Brzycki y el ajuste biológico.
     * @param pesoLevantado Peso levantado en kg.
     * @param reps Número de repeticiones.
     * @param rpe RPE estimado.
     * @param genero Género del usuario.
     * @param edad Edad del usuario.
     * @return El peso máximo estimado para 1 repetición del ejercicio.
     */
    public static double calcular1RMFinal(double pesoLevantado, int reps, double rpe, String genero, int edad) {

        if (pesoLevantado <= 0 || reps <= 0) return 0.0;

        // Calcular Repeticiones Virtuales
        double rpeEfectivo = (rpe > 0 && rpe <= 10) ? rpe : 10.0;
        double repsTotales = reps + (10.0 - rpeEfectivo);

        double rmBase;

        // SELECCIÓN DE FÓRMULA SEGÚN VOLUMEN
        if (repsTotales <= 12) {
            // Brzycki: Ideal para bajas repeticiones
            rmBase = pesoLevantado / (1.0278 - (0.0278 * repsTotales));
        } else {
            // Epley: Más estable para altas repeticiones (resistencia)
            rmBase = pesoLevantado * (1 + (repsTotales / 30.0));
        }

        // Ajuste biológico (género y edad)
        double cBio = obtenerFactorBio(genero, edad);

        return Math.round(rmBase * cBio * 100.0) / 100.0;
    }

    /**
     * Calcula el factor biológico según el género y la edad.
     * @param genero Género del usuario.
     * @param edad Edad del usuario.
     * @return Factor biológico.
     */
    private static double obtenerFactorBio(String genero, int edad) {
        boolean esHombre = genero.equalsIgnoreCase("Masculino");
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