package com.demo.services;

import com.demo.entities.*;
import com.demo.entities.Estados.EstadoServidor;
import com.demo.entities.Estados.Evento;
import com.demo.entities.Estados.Trabajo;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@NoArgsConstructor
public class SimulacionPractica extends Simulacion {
    private ArrayList<Trabajo> trabajos = new ArrayList<>(Arrays.asList(Trabajo.values()));
    public ArrayList<FilaVector> cola(double tiempo_simulacion,
                                      double[] probabilidadesOcurrencia,
                                      double[] tiemposDemora,
                                      double limite_inferiorUniforme,
                                      double limite_superiorUniforme,
                                      double tiempo_equipoC,
                                      double tiempo_equipoCHasta) {


        Trabajo[] trabajosArray = trabajos.toArray(new Trabajo[0]);

        double reloj = 0;
        Llegada llegada = new Llegada();
//        llegada.calcularTiempoEntreLlegada(reloj);
//        llegada.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);
        ColaVector colaVector = new ColaVector();
//        colaVector.sumarColaComun();
//        colaVector.sumarColaTrabajoC();
//        colaVector.sumarTrabajoCSegundoPlano();
        int contadorEquipo = 0;
        double horaCambioTrabajoC = 0.0;
        double horaReanudacionTrabajoC = 0.0;
        FinTrabajo finTrabajo = new FinTrabajo();
//        finTrabajo.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);
        Servidor servidor = new Servidor(EstadoServidor.Libre, 0, 0, 0, 0);

        ArrayList<FilaVector> filaVectors = new ArrayList<>();
        FilaVector filaVectorActual = new FilaVector(Evento.Inicio, reloj, llegada, colaVector, contadorEquipo, horaCambioTrabajoC, 0.0, finTrabajo, servidor,     new ArrayList<>());

        filaVectors.add(filaVectorActual);

//        while (reloj < tiempo_simulacion) {
//
//        }

        return filaVectors;

    }
}
