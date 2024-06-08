package com.demo.services;

import com.demo.entities.*;
import com.demo.entities.Estados.EstadoEquipo;
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
        llegada.calcularTiempoEntreLlegada(reloj);
        llegada.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);
        ColaVector colaVector = new ColaVector();
        colaVector.sumarColaComun();
        colaVector.sumarColaTrabajoC();
        colaVector.sumarTrabajoCSegundoPlano();
        int contadorEquipo = 0;
        double horaCambioTrabajoC = 0.0;
        double horaReanudacionTrabajoC = 0.0;
        double lugares_libresColaComun = 9;

        double hora_proximaLlegada = llegada.getTiempoHoraProximaLlegada();
        double hora_proximoFinTrabajo = 0;
        double hora_cambio_trabajo_C = 0;
        double hora_reanudacion_trabajo_C = 0;

        FinTrabajo finTrabajo = new FinTrabajo();
        finTrabajo.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);
        Servidor servidor = new Servidor(EstadoServidor.Libre, 0, 0, 0, 0);

        ArrayList<FilaVector> filaVectors = new ArrayList<>();
        FilaVector filaVectorActual = new FilaVector(Evento.Inicio, reloj, llegada, colaVector, contadorEquipo, horaCambioTrabajoC, 0.0, finTrabajo, servidor,     new ArrayList<>());
        System.out.println(filaVectorActual);
        filaVectors.add(filaVectorActual);
        int contador_equipos = 0;

        if(llegada.getTrabajo().equals(Trabajo.C)) {
            hora_cambio_trabajo_C = reloj + tiempo_equipoC;
            hora_reanudacion_trabajo_C = reloj + tiempo_equipoCHasta;
        }


        while (reloj < tiempo_simulacion) {


            // La primera vuelta sin importa que tipo de equipo sea, siempre se va a atender y a su vez tambien siempre va a calcular proxima llegada
            if(contador_equipos == 0) {
                reloj = reloj + llegada.getTiempoEntreLlegada();
                FilaVector filaVectorAnterior = filaVectorActual;

                Llegada llegadaDipositivo = new Llegada();
                llegada.calcularTiempoEntreLlegada(reloj);
                llegada.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);

                ColaVector colaVector1 = new ColaVector();
                colaVector1.setColaComun(filaVectorAnterior.getColaVector().getColaComun());
                colaVector1.setColaTrabajoC(filaVectorAnterior.getColaVector().getColaTrabajoC());
                colaVector1.setTrabajoCSegundoPlano(filaVectorAnterior.getColaVector().getTrabajoCSegundoPlano());


                FinTrabajo finTrabajo1 = new FinTrabajo();
                finTrabajo1.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);


                Servidor servidor1 = new Servidor(EstadoServidor.Ocupado, reloj, 0, 0, 0);


                Equipo equipo = new Equipo(1, EstadoEquipo.Atendido, llegadaDipositivo.getTrabajo().toString(), filaVectorAnterior.getLlegada().getTiempoEntreLlegada(), filaVectorAnterior.getLlegada().getTiempoEntreLlegada(), finTrabajo1.getHorafinTrabajo(), 0);

                ArrayList<Equipo> equipoList = new ArrayList<>();
                equipoList.add(equipo);


                FilaVector filaVectorActual1 = new FilaVector(Evento.Llegada, reloj, llegadaDipositivo, colaVector1, filaVectorAnterior.getContadorEquipo() + 1, filaVectorAnterior.getHoraCambioTrabajoC(), filaVectorAnterior.getHoraReanudacionTrabajoC(), finTrabajo1, servidor1, equipoList);

                filaVectors.add(filaVectorActual1);

            } else {




            }




            if (hora_proximaLlegada < hora_proximoFinTrabajo && hora_proximaLlegada < hora_cambio_trabajo_C && hora_proximaLlegada < hora_reanudacion_trabajo_C) {
                reloj = hora_proximaLlegada;
                contador_equipos = contador_equipos + 1;
            };

            if (hora_proximoFinTrabajo < hora_proximaLlegada && hora_proximoFinTrabajo < hora_cambio_trabajo_C && hora_proximoFinTrabajo < hora_reanudacion_trabajo_C) {
                reloj = hora_proximoFinTrabajo;
            };

            if (hora_cambio_trabajo_C < hora_proximaLlegada && hora_cambio_trabajo_C < hora_cambio_trabajo_C && hora_reanudacion_trabajo_C < hora_proximoFinTrabajo) {
                reloj = hora_cambio_trabajo_C;
            };

            if (hora_reanudacion_trabajo_C < hora_proximaLlegada && hora_reanudacion_trabajo_C < hora_proximoFinTrabajo && hora_reanudacion_trabajo_C < hora_cambio_trabajo_C) {
                reloj = hora_reanudacion_trabajo_C;
            };


        }



        return filaVectors;

    }
}
