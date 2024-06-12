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
import java.util.Comparator;
import java.util.Optional;

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
        Llegada llegada_primera = new Llegada();
        llegada_primera.calcularTiempoEntreLlegada(reloj);
        llegada_primera.setTrabajo(null);
        llegada_primera.setRndTipoTrabajo(0.0);


        // Cola de equipos
        ColaVector colaVectorInicio = new ColaVector(0, 0, 0, 9);


        int contadorEquipo = 0;

        double horaCambioTrabajoC = 0.0;
        double horaReanudacionTrabajoC = 0.0;
        int lugares_libresColaComun = 9;

        double hora_proximaLlegada = llegada_primera.getTiempoHoraProximaLlegada();
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
        Servidor servidorInicio = new Servidor(EstadoServidor.Libre, 0, 0, 0, 0);

        // para guardas las cosas que nos piden
        ArrayList<FilaVector> filaVectors = new ArrayList<>();

        ArrayList<Equipo> trabajos_equipos = new ArrayList<>();

        ArrayList<Equipo> colaComun = new ArrayList<>();

        ArrayList<Equipo> colaTrabajoC = new ArrayList<>();

        ArrayList<FinTrabajo> colaTrabajoFinalizado = new ArrayList<>();

        // La lista del vector
        FilaVector filaVectorActual = new FilaVector(Evento.Inicio, reloj, llegada_primera, colaVectorInicio, contadorEquipo, horaCambioTrabajoC, 0.0, finTrabajo, servidorInicio, new ArrayList<>());

        System.out.println(filaVectorActual.toString());


        // Aca tengo la fila anterior
        filaVectors.add(filaVectorActual);

        int contador_equipos = 0;

        double tiempo_ocupacion = 0;

        double tiempo_permanencia_equipos = 0;

        reloj = llegada_primera.tiempoHoraProximaLlegada;


        while (reloj < tiempo_simulacion) {

            FilaVector filaVectorAnterior = filaVectors.get(filaVectors.size() - 1);

            // Llega la primera equipo;
            if (contador_equipos == 0) {
                contador_equipos += 1;


                ColaVector colaVector1 = new ColaVector(filaVectorAnterior.getColaVector().getColaComun(), filaVectorAnterior.getColaVector().getColaTrabajoC(), filaVectorAnterior.getColaVector().getTrabajoCSegundoPlano(), filaVectorAnterior.getColaVector().getLugaresLibres());
                System.out.println(colaVector1.toString());
                Llegada llegadaDipositivo = new Llegada();
                llegadaDipositivo.calcularTiempoEntreLlegada(reloj);
                llegadaDipositivo.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);


                hora_proximaLlegada = reloj + llegadaDipositivo.getTiempoEntreLlegada();


                FinTrabajo finTrabajo1 = new FinTrabajo();
                finTrabajo1.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);

                colaTrabajoFinalizado.add(finTrabajo1);

                hora_proximoFinTrabajo = finTrabajo1.getHorafinTrabajo();

                Servidor servidorPrimerVuelta = new Servidor(EstadoServidor.Libre, filaVectorAnterior.getServidor().horaInicioOcupacion, filaVectorAnterior.getServidor().horaFinOcupacion,filaVectorAnterior.getServidor().tiempoPermanenciaEquipoAcum, filaVectorAnterior.getServidor().tiempoOcupacionAcum);

                servidorPrimerVuelta.setEstado(EstadoServidor.Ocupado);
                servidorPrimerVuelta.setHoraFinOcupacion(0);
                servidorPrimerVuelta.setTiempoOcupacionAcum(0);
                servidorPrimerVuelta.setTiempoPermanenciaEquipoAcum(0);

                if (llegadaDipositivo.getTrabajo().equals(Trabajo.C)) {
                    hora_cambio_trabajo_C = reloj + tiempo_equipoC;
                    colaVector1.sumarColaTrabajoC();

                } else {
                    colaVector1.sumarColaComun();
                }


                Equipo equipo = new Equipo(contador_equipos, EstadoEquipo.Atendido, llegadaDipositivo.getTrabajo(), filaVectorAnterior.getLlegada().getTiempoEntreLlegada(), filaVectorAnterior.getLlegada().getTiempoEntreLlegada(), finTrabajo.getHorafinTrabajo(), 0);
                trabajos_equipos.add(equipo);

                colaComun.add(equipo);

                FilaVector filaVectorActual1 = new FilaVector(Evento.Llegada, reloj, llegadaDipositivo, colaVector1, filaVectorAnterior.getContadorEquipo() + 1, filaVectorAnterior.getHoraCambioTrabajoC(), filaVectorAnterior.getHoraReanudacionTrabajoC(), finTrabajo1, servidorPrimerVuelta, trabajos_equipos);
                //filaVectors.clear();
                filaVectors.add(filaVectorActual1);


            } else {
                if (proxima_llegada) {

                    ColaVector colaVectorProximaLlegada = new ColaVector(filaVectorAnterior.getColaVector().getColaComun(), filaVectorAnterior.getColaVector().getColaTrabajoC(), filaVectorAnterior.getColaVector().getTrabajoCSegundoPlano(), filaVectorAnterior.getColaVector().getLugaresLibres());
                    Servidor servidorAnterior = filaVectorAnterior.servidor;
                    Llegada llegada1 = new Llegada();
                    llegada1.calcularTiempoEntreLlegada(reloj);
                    llegada1.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);
                    hora_proximaLlegada = reloj + llegada1.getTiempoEntreLlegada();
                    FinTrabajo finTrabajo2 = new FinTrabajo();
                    finTrabajo2.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);


                    colaTrabajoFinalizado.add(finTrabajo2);
                    System.out.println(colaTrabajoFinalizado.toString());

                    for (FinTrabajo fintrabajo01 : colaTrabajoFinalizado) {
                        if (fintrabajo01.getHorafinTrabajo() < hora_proximoFinTrabajo) {
                            hora_proximoFinTrabajo = fintrabajo01.getHorafinTrabajo();
                            System.out.println("Hora fin trabajo" + hora_proximoFinTrabajo);
                        }
                    }


                    Servidor servidorProximaLlegada = new Servidor(EstadoServidor.Libre, filaVectorAnterior.getServidor().horaInicioOcupacion, filaVectorAnterior.getServidor().horaFinOcupacion,filaVectorAnterior.getServidor().tiempoPermanenciaEquipoAcum, filaVectorAnterior.getServidor().tiempoOcupacionAcum);


                    if (servidorAnterior.getEstado().equals(EstadoServidor.Ocupado)) {
                        if (filaVectorAnterior.getColaVector().getLugaresLibres() >= 1) {
                            if (llegada1.getTrabajo().equals(Trabajo.C)) {
                                colaVectorProximaLlegada.sumarColaTrabajoC();
                                double hora_atencion = reloj + tiempo_equipoC;
                                double hora_cambioHora = reloj + tiempo_equipoC;
                                double hora_fin_Anterior = finTrabajo2.getHorafinTrabajo();
                                Equipo equipoColaC = new Equipo(contador_equipos, EstadoEquipo.EncolaC, llegada1.getTrabajo(), reloj, 0, 0, 0);
                                colaTrabajoC.add(equipoColaC);

                            } else {

                                colaVectorProximaLlegada.sumarColaComun();
                                Equipo equipoColaComun = new Equipo(contador_equipos, EstadoEquipo.EnCola, llegada1.getTrabajo(), reloj, 0, 0, 0);
                                colaComun.add(equipoColaComun);

                            }
                        }

                        servidorProximaLlegada.setEstado(EstadoServidor.Ocupado);

                    } else {

                        servidorProximaLlegada.setEstado(EstadoServidor.Ocupado);
                        servidorProximaLlegada.setHoraFinOcupacion(reloj);
                        llegada1.calcularTiempoEntreLlegada(reloj);
                        llegada1.calcularTipoTrabajo(trabajosArray, probabilidadesOcurrencia);
                        finTrabajo2.calcularMediaTiempo(probabilidadesOcurrencia, tiemposDemora, reloj);

                        Equipo equipo = new Equipo();


                        if (llegada1.getTrabajo().equals(Trabajo.C)) {
                            equipo.setEquipo_estado(EstadoEquipo.EncolaC);
                            equipo.setTipo_trabajo(llegada1.getTrabajo());
                            equipo.setHora_llegada(reloj);
                            equipo.setHora_Inicio_atencion(horaCambioTrabajoC);
                            equipo.setHora_fin_atencion(finTrabajo.horafinTrabajo);

                            horaCambioTrabajoC = reloj + tiempo_equipoC;

                        } else {
                            equipo.setEquipo_estado(EstadoEquipo.Atendido);
                            equipo.setTipo_trabajo(llegada1.getTrabajo());
                            equipo.setHora_llegada(reloj);
                            equipo.setHora_Inicio_atencion(horaCambioTrabajoC);
                            equipo.setHora_fin_atencion(finTrabajo.horafinTrabajo);
                            equipo.setHora_salida(finTrabajo.horafinTrabajo);
                            hora_proximoFinTrabajo = finTrabajo.getHorafinTrabajo();
                        }

                    }

                    Servidor servidor1 = new Servidor(EstadoServidor.Ocupado, reloj, 0, 0, 0);
                    FilaVector filaVectorActual2 = new FilaVector(Evento.Llegada, reloj, llegada1, colaVectorProximaLlegada, filaVectorAnterior.getContadorEquipo() + 1, horaCambioTrabajoC, horaReanudacionTrabajoC, finTrabajo2, servidorProximaLlegada, trabajos_equipos);



                    filaVectors.add(filaVectorActual2);

                }

                if (proximoFinTrabajo) {

                    Llegada llegadaFinTrabajo = new Llegada();

                    ColaVector colaVectorFinTrabajo = new ColaVector(filaVectorAnterior.getColaVector().getColaComun(), filaVectorAnterior.getColaVector().getColaTrabajoC(), filaVectorAnterior.getColaVector().getTrabajoCSegundoPlano(), filaVectorAnterior.getColaVector().getLugaresLibres());
                    filaVectorAnterior.getServidor();
                    Servidor servidorFinTrabajo = new Servidor(EstadoServidor.Libre, filaVectorAnterior.getServidor().horaInicioOcupacion, filaVectorAnterior.getServidor().horaFinOcupacion,filaVectorAnterior.getServidor().tiempoPermanenciaEquipoAcum, filaVectorAnterior.getServidor().tiempoOcupacionAcum);

                    if (colaTrabajoC.size() != 0) {
                        System.out.println(colaTrabajoC.toString());
                        Optional<Equipo> equipoConMenorLlegada = colaTrabajoC.stream()
                                .filter(e -> e.getEquipo_estado().equals(EstadoEquipo.EncolaC) && e.getHora_fin_atencion() == 0.0)
                                .min(Comparator.comparingDouble(Equipo::getHora_llegada));

                        if(equipoConMenorLlegada.isPresent()) {
                            Equipo equipoMenorC = equipoConMenorLlegada.get();
                            equipoMenorC.setEquipo_estado(EstadoEquipo.Atendido);
                            equipoMenorC.setHora_Inicio_atencion(reloj);
                            equipoMenorC.setHora_fin_atencion(reloj + finTrabajo.getHorafinTrabajo());
                            equipoMenorC.setHora_salida(reloj + finTrabajo.getHorafinTrabajo());
                            double hora_llegada = equipoMenorC.getHora_llegada();

                            horaCambioTrabajoC = reloj + 0.25;
                            double hora_salida = equipoMenorC.getHora_salida();
                            double tiempo_permanencia = hora_salida - hora_llegada;
                            tiempo_permanencia_equipos += tiempo_permanencia;
                            colaVectorFinTrabajo.setColaTrabajoC(colaVectorFinTrabajo.getColaTrabajoC() - 1);

                            trabajos_equipos.add(equipoMenorC);
                        }

                    } else {
                        if (colaComun.size() != 0) {
                            System.out.println("Cola Comun" );
                            System.out.println(colaComun.toString());

                            Optional<Equipo> equipoConMenorLlegada = colaComun.stream()
                                    .filter(e -> e.getEquipo_estado().equals(EstadoEquipo.EnCola) && e.getHora_fin_atencion() == 0.0)
                                    .min(Comparator.comparingDouble(Equipo::getHora_llegada));
                            if (equipoConMenorLlegada.isPresent()) {
                                Equipo equipoMenor = equipoConMenorLlegada.get();
                                equipoMenor.setEquipo_estado(EstadoEquipo.Atendido);
                                equipoMenor.setHora_Inicio_atencion(reloj);
                                equipoMenor.setHora_fin_atencion(reloj + finTrabajo.getHorafinTrabajo());
                                equipoMenor.setHora_salida(reloj + finTrabajo.getHorafinTrabajo());
                                double hora_llegada = equipoMenor.getHora_llegada();
                                double hora_salida = equipoMenor.getHora_salida();
                                double tiempo_permanencia = hora_salida - hora_llegada;
                                tiempo_permanencia_equipos += tiempo_permanencia;
                                colaVectorFinTrabajo.setColaComun(colaVectorFinTrabajo.getColaComun() - 1);
                                trabajos_equipos.add(equipoMenor);
                            }
                        }

                    }

                    FilaVector filaVectorActual3 = new FilaVector(Evento.FinTrabajo, reloj, llegadaFinTrabajo, colaVectorFinTrabajo, contadorEquipo, horaCambioTrabajoC, horaReanudacionTrabajoC, finTrabajo, servidorFinTrabajo, trabajos_equipos);

                    filaVectors.add(filaVectorActual3);

                    for (FinTrabajo fintrabajo01 : colaTrabajoFinalizado) {
                        if (fintrabajo01.getHorafinTrabajo() < hora_proximoFinTrabajo && fintrabajo01.getHorafinTrabajo() < reloj ) {
                            hora_proximoFinTrabajo = fintrabajo01.getHorafinTrabajo();
                            System.out.println("Hora fin trabajo: " + hora_proximoFinTrabajo);
                        }else{
                            hora_proximoFinTrabajo = 100;
                        }
                    }



                }

                if (proximoCambio_trabajo) {


                }

                if (proximoReanudacion_trabajo) {

                    ColaVector colaProximaReanudacion = new ColaVector(filaVectorAnterior.getColaVector().getColaComun(), filaVectorAnterior.getColaVector().getColaTrabajoC(), filaVectorAnterior.getColaVector().getTrabajoCSegundoPlano(), filaVectorAnterior.getColaVector().getLugaresLibres());
                    Servidor servidorAnterior = filaVectorAnterior.getServidor();
                    if (servidorAnterior.getEstado().equals(EstadoServidor.Libre)) {

                        Equipo equipoColaAnterior = colaTrabajoC.get(0);
                        colaTrabajoC.remove(0);
                        Llegada llegada1 = new Llegada();
                        horaReanudacionTrabajoC = equipoColaAnterior.getHora_fin_atencion() - tiempo_equipoCHasta;
                        hora_reanudacion_trabajo_C = horaReanudacionTrabajoC;

                        servidorAnterior.setEstado(EstadoServidor.Libre);

                        FilaVector filaVector = new FilaVector(Evento.Cambio, reloj, llegada1, colaProximaReanudacion, contadorEquipo, horaCambioTrabajoC, horaReanudacionTrabajoC, finTrabajo, servidorAnterior, trabajos_equipos);

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
            }
            ;


        }

        return filaVectors;

    }

}
