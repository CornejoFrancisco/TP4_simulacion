package com.demo.services;

import com.demo.entities.*;
import com.demo.entities.Estados.EstadoEquipo;
import com.demo.entities.Estados.EstadoServidor;
import com.demo.entities.Estados.Evento;
import com.demo.entities.Estados.Trabajo;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

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

        tiempo_simulacion = 10;

        Trabajo[] trabajosArray = trabajos.toArray(new Trabajo[0]);

        double reloj = 0;
        // Llegada de un equipo
        Llegada llegada = new Llegada();
        llegada.calcularTiempoEntreLlegada(reloj);
        llegada.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);

        // Cola de equipos
        ColaVector colaVector = new ColaVector();
        colaVector.sumarColaComun();
        colaVector.sumarColaTrabajoC();
        colaVector.sumarTrabajoCSegundoPlano();


        int contadorEquipo = 0;

        double horaCambioTrabajoC = 0.0;
        double horaReanudacionTrabajoC = 0.0;
        int lugares_libresColaComun = 9;

        double hora_proximaLlegada = llegada.getTiempoHoraProximaLlegada();
        double hora_proximoFinTrabajo = 100;
        double hora_cambio_trabajo_C = 100;
        double hora_reanudacion_trabajo_C = 100;



        boolean proxima_llegada = true;
        boolean proximoFinTrabajo = false;
        boolean proximoCambio_trabajo = false;
        boolean proximoReanudacion_trabajo = false;

        // esto todavia no va, ya que calcula una vez que entra un equipo

        FinTrabajo finTrabajo = new FinTrabajo();
        /*finTrabajo.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);*/

        // crea el servidor
        Servidor servidor = new Servidor(EstadoServidor.Libre, 0, 0, 0, 0);

        // para guardas las cosas que nos piden
        ArrayList<FilaVector> filaVectors = new ArrayList<>();

        ArrayList<Equipo> trabajos_equipos = new ArrayList<>();

        ArrayList<EquipoCola> colaComun = new ArrayList<>();

        ArrayList<EquipoCola> colaTrabajoC = new ArrayList<>();


        // La lista del vector
        FilaVector filaVectorActual = new FilaVector(Evento.Inicio, reloj, llegada, colaVector, contadorEquipo, horaCambioTrabajoC, 0.0, finTrabajo, servidor, new ArrayList<>());

        // Aca tengo la fila anterior
        filaVectors.add(filaVectorActual);

        int contador_equipos = 0;

        double tiempo_ocupacion = 0;

        double tiempo_permanencia_equipos = 0;

        reloj = llegada.tiempoHoraProximaLlegada;





        while (reloj < tiempo_simulacion) {
            // Llega la primera equipo;
            if (contador_equipos == 0) {
                contador_equipos += 1;

                Llegada llegadaDipositivo = new Llegada();
                llegadaDipositivo.calcularTiempoEntreLlegada(reloj);
                llegadaDipositivo.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);

                hora_proximaLlegada = reloj + llegadaDipositivo.getTiempoEntreLlegada();

                FilaVector filaVectorAnterior = filaVectors.get(filaVectors.size() - 1);

                finTrabajo.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);
                double hora_salida = finTrabajo.getHorafinTrabajo();
                hora_proximoFinTrabajo = hora_salida;

                servidor.setEstado(EstadoServidor.Ocupado);
                servidor.setHoraFinOcupacion(0);
                servidor.setTiempoOcupacionAcum(0);
                servidor.setTiempoPermanenciaEquipoAcum(0);

                if(llegadaDipositivo.getTrabajo() == Trabajo.C){
                    hora_cambio_trabajo_C = reloj + tiempo_equipoC;
                    colaTrabajoC.add(new EquipoCola(Trabajo.C, 0, 0, 0));
                    colaVector.setColaTrabajoC(colaVector.getColaTrabajoC() + 1);
                }

                Equipo equipo = new Equipo(1, EstadoEquipo.Atendido, llegadaDipositivo.getTrabajo(), filaVectorAnterior.getLlegada().getTiempoEntreLlegada(), filaVectorAnterior.getLlegada().getTiempoEntreLlegada(), finTrabajo.getHorafinTrabajo(), hora_salida);
                trabajos_equipos.add(equipo);
                FilaVector filaVectorActual1 = new FilaVector(Evento.Llegada, reloj, llegadaDipositivo, colaVector, filaVectorAnterior.getContadorEquipo() + 1, filaVectorAnterior.getHoraCambioTrabajoC(), filaVectorAnterior.getHoraReanudacionTrabajoC(), finTrabajo, servidor, trabajos_equipos);
                filaVectors.clear();
                filaVectors.add(filaVectorActual1);

            } else {
                if(proxima_llegada) {

                    FilaVector filaVectorAnterior = filaVectors.get(filaVectors.size() - 1);

                    Llegada llegada1 = new Llegada();
                    llegada1.calcularTiempoEntreLlegada(reloj);
                    llegada1.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);
                    hora_proximaLlegada = reloj + llegada1.getTiempoEntreLlegada();

                    finTrabajo.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);
                    hora_proximoFinTrabajo = finTrabajo.getHorafinTrabajo();

                    if(servidor.getEstado().equals(EstadoServidor.Ocupado)){
                        if(filaVectorAnterior.getColaVector().getLugaresLibres() >= 1) {
                            if (llegada.getTrabajo().equals(Trabajo.C)) {

                                if (filaVectorAnterior.getColaVector().getLugaresLibres() > 0) {
                                    filaVectorAnterior.getColaVector().setLugaresLibres(filaVectorAnterior.getColaVector().getLugaresLibres() - 1);
                                    filaVectorAnterior.getColaVector().setTrabajoCSegundoPlano(filaVectorAnterior.getColaVector().getTrabajoCSegundoPlano() + 1);
                                    double hora_atencion = reloj + tiempo_equipoC;
                                    double hora_cambioHora = reloj + tiempo_equipoC;
                                    double hora_fin_Anterior = finTrabajo.getHorafinTrabajo();

                                    EquipoCola equipoC = new EquipoCola(Trabajo.C, hora_atencion, hora_cambioHora,  hora_fin_Anterior);
                                    colaTrabajoC.add(equipoC);
                                }
                            } else {
                                if (filaVectorAnterior.getColaVector().getColaComun() > 0) {
                                    filaVectorAnterior.getColaVector().setColaComun(filaVectorAnterior.getColaVector().getColaComun() + 1);
                                    filaVectorAnterior.getColaVector().setLugaresLibres(filaVectorAnterior.getColaVector().getLugaresLibres() - 1);
                                    EquipoCola equipoCola = new EquipoCola(llegada1.getTrabajo(), reloj, 0, 0);
                                    colaComun.add(equipoCola);
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


                        if(llegada.getTrabajo().equals(Trabajo.C)){
                            equipo.setEquipo_estado(EstadoEquipo.EncolaC);
                            equipo.setTipo_trabajo(llegada.getTrabajo());
                            equipo.setHora_llegada(reloj);
                            equipo.setHora_Inicio_atencion(horaCambioTrabajoC);
                            equipo.setHora_fin_atencion(finTrabajo.horafinTrabajo);

                            horaCambioTrabajoC = reloj + tiempo_equipoC;

                        }else{
                            equipo.setEquipo_estado(EstadoEquipo.Atendido);
                            equipo.setTipo_trabajo(llegada.getTrabajo());
                            equipo.setHora_llegada(reloj);
                            equipo.setHora_Inicio_atencion(horaCambioTrabajoC);
                            equipo.setHora_fin_atencion(finTrabajo.horafinTrabajo);
                            equipo.setHora_salida(finTrabajo.horafinTrabajo);
                            hora_proximoFinTrabajo = finTrabajo.getHorafinTrabajo();
                        }

                    }
                    FilaVector filaVectorActual2 = new FilaVector(Evento.Llegada, reloj, llegada1, colaVector, filaVectorAnterior.getContadorEquipo() + 1, horaCambioTrabajoC, horaReanudacionTrabajoC, finTrabajo, servidor, trabajos_equipos);
                    filaVectors.add(filaVectorActual2);

                }

                if (proximoFinTrabajo) {

                    if(colaTrabajoC.size() != 0){


                        Equipo equipo = new Equipo();

                        equipo.setEquipo_estado(EstadoEquipo.Finalizado);

                        colaTrabajoC.remove(0);

                        equipo.setHora_salida(reloj);



                        double hora_llegada = equipo.getHora_llegada();
                        double hora_salida = equipo.getHora_salida();
                        double tiempo_permanencia = hora_salida - hora_llegada;
                        tiempo_permanencia_equipos += tiempo_permanencia;

                    }else{
                        if(colaComun.size() != 0){
                            EquipoCola equipoCola = colaComun.get(0);
                            colaComun.remove(0);
                            trabajos_equipos.get(trabajos_equipos.size() - 1).setHora_Inicio_atencion(reloj);
                            trabajos_equipos.get(trabajos_equipos.size() - 1).setHora_fin_atencion(reloj + finTrabajo.getHorafinTrabajo());
                            trabajos_equipos.get(trabajos_equipos.size() - 1).setHora_salida(reloj + finTrabajo.getHorafinTrabajo());
                            double hora_llegada = trabajos_equipos.get(trabajos_equipos.size() - 1).getHora_llegada();
                            double hora_salida = trabajos_equipos.get(trabajos_equipos.size() - 1).getHora_salida();
                            double tiempo_permanencia = hora_salida - hora_llegada;
                            tiempo_permanencia_equipos += tiempo_permanencia;
                        }
                        else{
                            servidor.setEstado(EstadoServidor.Libre);
                            servidor.setHoraFinOcupacion(0);
                            servidor.setTiempoOcupacionAcum(servidor.getTiempoOcupacionAcum() + tiempo_ocupacion);
                            servidor.setTiempoPermanenciaEquipoAcum(servidor.getTiempoPermanenciaEquipoAcum() + tiempo_permanencia_equipos);
                            tiempo_ocupacion = 0;
                            tiempo_permanencia_equipos = 0;
                        }

                    }
                    hora_proximoFinTrabajo = 100;
                    FilaVector filaVectorActual3 = new FilaVector(Evento.FinTrabajo, reloj, llegada, colaVector, contadorEquipo, horaCambioTrabajoC, horaReanudacionTrabajoC, finTrabajo, servidor, trabajos_equipos);
                    filaVectors.add(filaVectorActual3);
                }

                if (proximoCambio_trabajo) {






                }

                if (proximoReanudacion_trabajo) {
                    if (servidor.getEstado().equals(EstadoServidor.Libre)){

                        EquipoCola equipoColaAnterior = colaTrabajoC.get(0);
                        colaTrabajoC.remove(0);
                        Llegada llegada1 = new Llegada();
                        horaReanudacionTrabajoC = equipoColaAnterior.getHora_finatencion() - tiempo_equipoCHasta;
                        hora_reanudacion_trabajo_C = horaReanudacionTrabajoC;

                        servidor.setEstado(EstadoServidor.Libre);

                        FilaVector filaVector = new FilaVector(Evento.Cambio, reloj, llegada1, colaVector, contadorEquipo, horaCambioTrabajoC, horaReanudacionTrabajoC, finTrabajo, servidor, trabajos_equipos);

                        filaVectors.add(filaVector);
                    }


                }


            }



            if (hora_proximaLlegada < hora_proximoFinTrabajo && hora_proximaLlegada < hora_cambio_trabajo_C && hora_proximaLlegada < hora_reanudacion_trabajo_C) {
                System.out.println("Llegada");
                reloj = hora_proximaLlegada;
                contador_equipos = contador_equipos + 1;
                proxima_llegada = true;
                proximoFinTrabajo = false;
                proximoCambio_trabajo = false;
                proximoReanudacion_trabajo = false;

            }
            ;

            if (hora_proximoFinTrabajo < hora_proximaLlegada && hora_proximoFinTrabajo < hora_cambio_trabajo_C && hora_proximoFinTrabajo < hora_reanudacion_trabajo_C) {
                System.out.println("Fin Trabajo");
                reloj = hora_proximoFinTrabajo;
                proximoFinTrabajo = true;
                proxima_llegada = false;
                proximoCambio_trabajo = false;
                proximoReanudacion_trabajo = false;

            }
            ;

            if (hora_cambio_trabajo_C < hora_proximaLlegada && hora_cambio_trabajo_C < hora_cambio_trabajo_C && hora_reanudacion_trabajo_C < hora_proximoFinTrabajo) {
                System.out.println("Cambio Trabajo"
                );
                reloj = hora_cambio_trabajo_C;
                proximoCambio_trabajo = true;
                proximoFinTrabajo = false;
                proxima_llegada = false;
                proximoReanudacion_trabajo = false;
            }
            ;

            if (hora_reanudacion_trabajo_C < hora_proximaLlegada && hora_reanudacion_trabajo_C < hora_proximoFinTrabajo && hora_reanudacion_trabajo_C < hora_cambio_trabajo_C) {
                System.out.println("Reanudacion Trabajo");
                reloj = hora_reanudacion_trabajo_C;
                proximoReanudacion_trabajo = true;
                proximoCambio_trabajo = false;
                proximoFinTrabajo = false;
                proxima_llegada = false;
            };


        }

        return filaVectors;

    }

}
