package com.demo.entities;

import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class FinTrabajo {

    private int idEquipoFinTrabajo;
    public double rndFinTrabajo;
    public double mediaTiempoAtencion;
    public double tiempoAtencion;
    public double horaFinTrabajo;

    private double rndFinTrabajoCalculo() {
        Random random = new Random();
        double numero_random1 = random.nextDouble();
        double numero_random = Math.round(numero_random1 * 100.0) / 100.0;
        return numero_random;
    }

    public void calcularMediaTiempo(double[] lista_probabilidad,
                                    double[] tiempo_trabajo,
                                    double reloj,
                                    double limite_inferiorUniforme,
                                    double limite_superiorUniforme) {

        double ran = rndFinTrabajoCalculo();

        double[][] intervalos_probabilidad = intervalos(lista_probabilidad);
        this.rndFinTrabajo = ran;

        for (int i = 0; i < intervalos_probabilidad.length; i++) {
            double linf = intervalos_probabilidad[i][0];
            double lsup = intervalos_probabilidad[i][1];

            if (ran >= linf && ran < lsup) {
                this.mediaTiempoAtencion = tiempo_trabajo[i];
                this.tiempoAtencion = (this.mediaTiempoAtencion - limite_inferiorUniforme) +
                        ran * ((this.mediaTiempoAtencion + limite_superiorUniforme) -
                                (this.mediaTiempoAtencion - limite_inferiorUniforme));
                this.horaFinTrabajo = reloj + tiempoAtencion;
                return;
            }

            System.out.println("linf: " + linf + " lsup: " + lsup + " ran: " + ran + "Media:" + mediaTiempoAtencion);
        }
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

    private Double generateRandom() {
        Random rnd = new Random();
        double rndCeroUno = rnd.nextDouble();
        return (Math.round(rndCeroUno * 100.0) / 100.0);
    }

    public void calcularHoraFinTrabajo(Trabajo tipoTrabajo,
                                       ArrayList<Double> tiemposMediosTrabajo,
                                       Double reloj,
                                       Double limite_inferiorUniforme,
                                       Double limite_superiorUniforme
                                           ){
        Double rnd = generateRandom();
        Double mediaTrabajo = calcularMediaTrabajo(tipoTrabajo, tiemposMediosTrabajo);
        Double tiempoAtencion = (mediaTrabajo - limite_inferiorUniforme) +
                rnd * ((mediaTrabajo + limite_superiorUniforme) - (mediaTrabajo - limite_inferiorUniforme));
        this.mediaTiempoAtencion = mediaTrabajo;
        this.rndFinTrabajo = rnd;
        this.tiempoAtencion = tiempoAtencion;
        this.horaFinTrabajo = reloj + tiempoAtencion;
    }

    public Double calcularMediaTrabajo(  Trabajo tipoTrabajo,
                                         ArrayList<Double> tiemposMediosTrabajo) {
        switch (tipoTrabajo) {
            case A:
                return tiemposMediosTrabajo.get(0);
            case B:
                return tiemposMediosTrabajo.get(1);
            case C:
                return tiemposMediosTrabajo.get(2);
            case D:
                return tiemposMediosTrabajo.get(3);
            default:
                return 0.0;
        }
    }

    private ArrayList<Double> calcularLimitesInferiores(ArrayList<Double> arrayProbabilidades) {
        ArrayList<Double> limitesInferiores = new ArrayList<>();
        for (int i = 0; i <= arrayProbabilidades.size() - 1; i++) {
            if (i == 0) {
                limitesInferiores.add(0.00);
            } else {
                Double limiteInferiorAnterior = limitesInferiores.get(i - 1);
                Double probabilidadAnterior = arrayProbabilidades.get(i - 1);
                Double limiteInferiorActual = limiteInferiorAnterior + probabilidadAnterior;
                limitesInferiores.add(limiteInferiorActual);
            }
        }
        return limitesInferiores;
    }

}
