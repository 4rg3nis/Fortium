package com.sthenos.fortium.model.queries;

/**
 * Molde para representar el volumen total de carga por sesión.
 * Utilizado en la gráfica de barras.
 * @author Argenis
 */
public class ProgresoVolumen {
    public String fecha;
    public double totalVolumen;  // El resultado del SUM(peso * reps)

    public ProgresoVolumen(String fecha, double totalVolumen) {
        this.fecha = fecha;
        this.totalVolumen = totalVolumen;
    }

    public String getFecha() { return fecha; }
    public double getTotalVolumen() { return totalVolumen; }
}