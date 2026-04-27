package com.sthenos.fortium.model.entities;

/**
 * Molde para representar la distribución de entrenamiento por grupo muscular.
 * Utilizado en la gráfica de tarta
 * @author Argenis
 */
public class DistribucionMuscular {
    public String musculo;
    public int cantidadSeries;  // El resultado del COUNT(Serie.id)

    public DistribucionMuscular(String musculo, int cantidadSeries) {
        this.musculo = musculo;
        this.cantidadSeries = cantidadSeries;
    }
}