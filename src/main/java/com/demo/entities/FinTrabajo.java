package com.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class FinTrabajo {

    private int idEquipoFinTrabajo;
    public double rndFinTrabajo;

    public double mediaTiempoAtencion;
    public double tiempoAtencion;

    public double horaEvento;

    private double rndFinTrabajoCalculo() {
        Random random = new Random();
        double numero_random1 = random.nextDouble();
        double numero_random = Math.round(numero_random1 * 100.0) / 100.0;
        return numero_random;
    }

    public void calcularMediaTiempo(double[] lista_probabilidad, double[] tiempo_trabajo, double reloj) {

        double ran = rndFinTrabajoCalculo();

        double[][] intervalos_probabilidad = intervalos(lista_probabilidad);
        this.rndFinTrabajo = ran;

        for (int i = 0; i < intervalos_probabilidad.length; i++) {
            double linf = intervalos_probabilidad[i][0];
            double lsup = intervalos_probabilidad[i][1];

            if (ran >= linf && ran < lsup) {
                this.mediaTiempoAtencion = tiempo_trabajo[i];
                this.tiempoAtencion = (this.mediaTiempoAtencion - 0.083) + ran * ((this.mediaTiempoAtencion + 0.083) - (this.mediaTiempoAtencion - 0.083));
                this.horaEvento = reloj + tiempoAtencion;
                return;
            }
        }

        // Si no se encuentra ningún intervalo adecuado, se asigna un valor por defecto
        // Esto puede cambiar dependiendo de tus necesidades
        this.mediaTiempoAtencion = 0; // Asigna un valor por defecto
        this.tiempoAtencion = 0; // Asigna un valor por defecto
    }

    private double[][] intervalos(double[] valores_probabilidad) {
        int n = valores_probabilidad.length;
        double[][] intervalo_proba = new double[n][2];
        double primero = 0.0;

        for (int i = 0; i < n; i++) {
            if (i + 1 == n) {
                intervalo_proba[i][0] = primero;
                intervalo_proba[i][1] = 1.0;
                return intervalo_proba;
            }

            double ultimo = primero + valores_probabilidad[i];
            intervalo_proba[i][0] = primero;
            intervalo_proba[i][1] = ultimo;
            primero = ultimo;
        }
        return intervalo_proba;
    }


}
