package com.demo.entities;


import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Llegada {

    public double rndLlegada;
    public double tiempoEntreLlegada;
    public double tiempoHoraProximaLlegada;

    public double rndTipoTrabajo;
    public Trabajo trabajo;

    public void calcularTiempoEntreLlegada(double reloj) {
        Random random = new Random();
        double ran = random.nextDouble();
        double tiempo_entre_llegada = 0.5 + ran * (1.5 - 0.5);
        this.rndLlegada = ran;
        this.tiempoEntreLlegada = tiempo_entre_llegada;
        this.tiempoHoraProximaLlegada = tiempo_entre_llegada + reloj;
    }

    public void calcularTipoTrabajo(Trabajo[] trabajos, double[] valores_probabilidad) {
        Random random = new Random();
        double ran = random.nextDouble();
        double[][] intervalos_probabilidad = intervalos(valores_probabilidad);
        this.rndTipoTrabajo = ran;

        for (int i = 0; i < intervalos_probabilidad.length; i++) {
            double linf = intervalos_probabilidad[i][0];
            double lsup = intervalos_probabilidad[i][1];

            if (ran >= linf && ran < lsup) {
                this.trabajo = trabajos[i];
                System.out.println("Trabajos:" + trabajos[i]);
                return;
            }
        }

        this.trabajo = Trabajo.D;
    }

    public double[][] intervalos(double[] valores_probabilidad) {
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