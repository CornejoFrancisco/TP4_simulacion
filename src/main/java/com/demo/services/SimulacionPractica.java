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
        int lugares_libresColaComun = 9;

        double hora_proximaLlegada = llegada.getTiempoHoraProximaLlegada();
        double hora_proximoFinTrabajo = 0;
        double hora_cambio_trabajo_C = 0;
        double hora_reanudacion_trabajo_C = 0;

        double hora_inicio_Ocupacion = 0;

        boolean proxima_llegada = true;
        boolean proximoFinTrabajo = false;
        boolean proximoCambio_trabajo = false;
        boolean proximoReanudacion_trabajo = false;

        FinTrabajo finTrabajo = new FinTrabajo();
        finTrabajo.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);
        Servidor servidor = new Servidor(EstadoServidor.Libre, 0, 0, 0, 0);

        ArrayList<FilaVector> filaVectors = new ArrayList<>();
        FilaVector filaVectorActual = new FilaVector(Evento.Inicio, reloj, llegada, colaVector, contadorEquipo, horaCambioTrabajoC, 0.0, finTrabajo, servidor, new ArrayList<>());

        filaVectors.add(filaVectorActual);
        int contador_equipos = 0;

        double tiempo_ocupacion = 0;

        double tiempo_permanencia_equipos = 0;

        ArrayList<Equipo> equipoList = new ArrayList<>();


        while (reloj < tiempo_simulacion) {

            if (contador_equipos == 0) {
                reloj = reloj + llegada.getTiempoEntreLlegada();
                FilaVector filaVectorAnterior = filaVectors.get(filaVectors.size() - 1);

                Llegada llegadaDipositivo = new Llegada();
                llegada.calcularTiempoEntreLlegada(reloj);
                llegada.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);


                colaVector.setColaComun(filaVectorAnterior.getColaVector().getColaComun());
                colaVector.setColaTrabajoC(filaVectorAnterior.getColaVector().getColaTrabajoC());
                colaVector.setTrabajoCSegundoPlano(filaVectorAnterior.getColaVector().getTrabajoCSegundoPlano());
                colaVector.setLugaresLibres(lugares_libresColaComun);



                finTrabajo.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);

                servidor.setEstado(EstadoServidor.Ocupado);
                servidor.setHoraFinOcupacion(0);
                servidor.setTiempoOcupacionAcum(0);
                servidor.setTiempoPermanenciaEquipoAcum(0);

                Equipo equipo = new Equipo(1, EstadoEquipo.Atendido, llegadaDipositivo.getTrabajo().toString(), filaVectorAnterior.getLlegada().getTiempoEntreLlegada(), filaVectorAnterior.getLlegada().getTiempoEntreLlegada(), finTrabajo1.getHorafinTrabajo(), 0);


                equipoList.add(equipo);


                FilaVector filaVectorActual1 = new FilaVector(Evento.Llegada, reloj, llegadaDipositivo, colaVector, filaVectorAnterior.getContadorEquipo() + 1, filaVectorAnterior.getHoraCambioTrabajoC(), filaVectorAnterior.getHoraReanudacionTrabajoC(), finTrabajo1, servidor, equipoList);
                filaVectors.clear();
                filaVectors.add(filaVectorActual1);

            } else {
                if (proxima_llegada) {


                    FilaVector filaVectorAnterior = filaVectors.get(filaVectors.size() - 1);

                    Llegada llegada1 = new Llegada();
                    llegada1.calcularTiempoEntreLlegada(reloj);
                    llegada1.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);



                    if(servidor.getEstado().equals(EstadoServidor.Ocupado)){
                        if(filaVectorAnterior.getColaVector().getLugaresLibres() >= 1) {


                            if (llegada.getTrabajo().equals(Trabajo.C)) {
                                if (filaVectorAnterior.getColaVector().getLugaresLibres() > 0) {
                                    filaVectorAnterior.getColaVector().setLugaresLibres(filaVectorAnterior.getColaVector().getLugaresLibres() - 1);
                                    filaVectorAnterior.getColaVector().setTrabajoCSegundoPlano(filaVectorAnterior.getColaVector().getTrabajoCSegundoPlano() + 1);
                                }
                            } else {
                                if (filaVectorAnterior.getColaVector().getColaComun() > 0) {
                                    filaVectorAnterior.getColaVector().setColaComun(filaVectorAnterior.getColaVector().getColaComun() + 1);
                                    filaVectorAnterior.getColaVector().setLugaresLibres(filaVectorAnterior.getColaVector().getLugaresLibres() - 1);
                                }

                            }
                        }


                    }else{
                        servidor.setEstado(EstadoServidor.Ocupado);
                        servidor.setHoraFinOcupacion(reloj);
                        llegada.calcularTiempoEntreLlegada(reloj);
                        llegada.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);
                        finTrabajo.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);
                        Equipo equipo = new Equipo();

                        equipo.setEquipo_estado(EstadoEquipo.Atendido);
                        equipo.setTipo_trabajo(llegada.getTrabajo().toString());
                        equipo.setHora_llegada(reloj);
                        equipo.setHora_Inicio_atencion(reloj);


                        if(llegada.getTrabajo().equals(Trabajo.C)){
                            equipo.setEquipo_estado(EstadoEquipo.EncolaC);
                            equipo.setTipo_trabajo(llegada.getTrabajo().toString());
                            equipo.setHora_llegada(reloj);
                            equipo.setHora_Inicio_atencion(horaCambioTrabajoC);
                            equipo.setHora_fin_atencion(finTrabajo.horafinTrabajo);

                            horaCambioTrabajoC = reloj + tiempo_equipoC;

                        }else{
                            equipo.setEquipo_estado(EstadoEquipo.Atendido);
                            equipo.setTipo_trabajo(llegada.getTrabajo().toString());
                            equipo.setHora_llegada(reloj);
                            equipo.setHora_Inicio_atencion(horaCambioTrabajoC);
                            equipo.setHora_fin_atencion(finTrabajo.horafinTrabajo);
                            equipo.setHora_salida(finTrabajo.horafinTrabajo);
                        }

                    }


                }

                if (proximoFinTrabajo) {




                }

                if (proximoCambio_trabajo) {

                }

                if (proximoReanudacion_trabajo) {

                }


            }


            if (hora_proximaLlegada < hora_proximoFinTrabajo && hora_proximaLlegada < hora_cambio_trabajo_C && hora_proximaLlegada < hora_reanudacion_trabajo_C) {
                reloj = hora_proximaLlegada;
                contador_equipos = contador_equipos + 1;
                proxima_llegada = true;
                proximoFinTrabajo = false;
                proximoCambio_trabajo = false;
                proximoReanudacion_trabajo = false;

            }
            ;

            if (hora_proximoFinTrabajo < hora_proximaLlegada && hora_proximoFinTrabajo < hora_cambio_trabajo_C && hora_proximoFinTrabajo < hora_reanudacion_trabajo_C) {
                reloj = hora_proximoFinTrabajo;
                proximoFinTrabajo = true;
                proxima_llegada = false;
                proximoCambio_trabajo = false;
                proximoReanudacion_trabajo = false;

            }
            ;

            if (hora_cambio_trabajo_C < hora_proximaLlegada && hora_cambio_trabajo_C < hora_cambio_trabajo_C && hora_reanudacion_trabajo_C < hora_proximoFinTrabajo) {
                reloj = hora_cambio_trabajo_C;
                proximoCambio_trabajo = true;
                proximoFinTrabajo = false;
                proxima_llegada = false;
                proximoReanudacion_trabajo = false;
            }
            ;

            if (hora_reanudacion_trabajo_C < hora_proximaLlegada && hora_reanudacion_trabajo_C < hora_proximoFinTrabajo && hora_reanudacion_trabajo_C < hora_cambio_trabajo_C) {
                reloj = hora_reanudacion_trabajo_C;
                proximoReanudacion_trabajo = true;
                proximoCambio_trabajo = false;
                proximoFinTrabajo = false;
                proxima_llegada = false;
            }
            ;


        }


        return filaVectors;

    }

}
