package com.demo.services;

import com.demo.entities.*;
import com.demo.entities.Estados.EstadoEquipo;
import com.demo.entities.Estados.EstadoServidor;
import com.demo.entities.Estados.Eventos;
import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionPractica extends Simulacion {
    private ArrayList<Trabajo> trabajos = new ArrayList<>(Arrays.asList(Trabajo.values()));
    private Trabajo[] trabajosArray = trabajos.toArray(new Trabajo[0]);

    private double tiempoSimulacion;
    private double[] probabilidadesTipoTrabajo;
    private double[] tiemposMediaTrabajo;
    private double limite_inferiorUniforme;
    private double limite_superiorUniforme;
    private double tiempoDesdeInicioEquipoC;
    private double tiempoAntesFinEquipoC;
    private double tiempoInicioResultado;
    private int cantidadItercaciones;

    private ArrayList<FilaVector> filaVectors = new ArrayList<>();
    private ArrayList<Equipo> trabajos_equipos = new ArrayList<>();
    private ArrayList<Equipo> colaComun = new ArrayList<>();
    private ArrayList<Equipo> colaTrabajoC = new ArrayList<>();
    private ArrayList<FinTrabajo> colaTrabajoFinalizado = new ArrayList<>();
    private ArrayList<Evento> proximosEventos = new ArrayList<>();
    private FilaVector filaActual = null;
    private FilaVector filaAnterior = null;
    private int contadorIteraciones = 0;
    private int contadorEquipos = 0;

    private Evento proximoEvento = null;
    private double reloj = 0;

    private boolean proxima_llegada = true;
    private boolean proximoFinTrabajo = false;
    private boolean proximoCambio_trabajo = false;
    private boolean proximoReanudacion_trabajo = false;

    private void buscarProximoEvento() {
        double tiempoProximoEvento = 0;
        Evento proximoEvento = null;

        for (int i = 0; i <= proximosEventos.size() - 1; i++) {
            if (i == 0) {
                tiempoProximoEvento = proximosEventos.get(i).getHoraEvento() - reloj;
                proximoEvento = proximosEventos.get(i);
            } else {
                if (proximosEventos.get(i).getHoraEvento() - reloj < tiempoProximoEvento) {
                    tiempoProximoEvento = proximosEventos.get(i).getHoraEvento() - reloj;
                    proximoEvento = proximosEventos.get(i);
                }
            }
        }
        this.proximosEventos.remove(proximoEvento);
        this.proximoEvento = proximoEvento;
    }

    private void eventoLlegada() {
        // Copia el estado de las colas de la fila anterior
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        // Copia el estado del servidor de la fila anterior
        Servidor servidorPrimerVuelta = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().horaInicioOcupacion,
                this.filaAnterior.getServidor().horaFinOcupacion,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum,
                this.filaAnterior.getServidor().tiempoOcupacionAcum);

        this.contadorEquipos = this.filaAnterior.getContadorEquipo();

        //horaCambioTrabajoC = this.filaAnterior.getHoraCambioTrabajoC();
        //horaReanudacionTrabajoC = this.filaAnterior.horaReanudacionTrabajoC;

        Llegada llegadaEquipo = new Llegada();
        llegadaEquipo.calcularTiempoEntreLlegada(this.reloj);
        //hora_proximaLlegada = llegadaEquipo.getHoraProximaLlegada();

        this.contadorEquipos++;

        proximosEventos.add(
                new Evento(
                        Eventos.Llegada,
                        llegadaEquipo.getHoraProximaLlegada(),
                        null)
        );


        if (!servidorPrimerVuelta.getEstado().equals(EstadoServidor.Ocupado)) {

            servidorPrimerVuelta.setEstado(EstadoServidor.Ocupado);
            servidorPrimerVuelta.setHoraInicioOcupacion(reloj);
            servidorPrimerVuelta.setHoraFinOcupacion(0);
            servidorPrimerVuelta.setTiempoOcupacionAcum(0);
            servidorPrimerVuelta.setTiempoPermanenciaEquipoAcum(0);

            FinTrabajo finTrabajo = new FinTrabajo();
            finTrabajo.calcularMediaTiempo(probabilidadesTipoTrabajo, tiemposMediaTrabajo, reloj);
            finTrabajo.setIdEquipoFinTrabajo(this.contadorEquipos);


            //colaTrabajoFinalizado.add(finTrabajo1);
            //hora_proximoFinTrabajo = finTrabajo1.getHorafinTrabajo();

            Equipo equipo = new Equipo(
                    this.contadorEquipos,
                    EstadoEquipo.Atendido,
                    llegadaEquipo.getTrabajo(),
                    reloj,
                    reloj,
                    finTrabajo.getHoraEvento(),
                    0);

            proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraEvento(),
                            equipo)
            );

            llegadaEquipo.calcularTipoTrabajo(trabajosArray, probabilidadesTipoTrabajo);

            if (llegadaEquipo.getTrabajo().equals(Trabajo.C)) {
                double horaCambioTrabajoC = reloj + tiempoDesdeInicioEquipoC;

                proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                horaCambioTrabajoC,
                                equipo)
                );
            }

            trabajos_equipos.add(equipo);

            this.filaActual = new FilaVector(Eventos.Llegada,
                    reloj,
                    llegadaEquipo,
                    colasEstadoActual,
                    this.filaAnterior.getContadorEquipo() + 1,
                    this.filaAnterior.getHoraCambioTrabajoC(),
                    this.filaAnterior.getHoraReanudacionTrabajoC(),
                    finTrabajo,
                    servidorPrimerVuelta,
                    trabajos_equipos);
        }
    }

    public ArrayList<FilaVector> cola(double tiempo_simulacion,
                                      double[] probabilidadesTipoTrabajo,
                                      double[] tiemposMediaTrabajo,
                                      double limite_inferiorUniforme,
                                      double limite_superiorUniforme,
                                      double tiempoDesdeInicioEquipoC,
                                      double tiempoAntesFinEquipoC,
                                      double tiempoInicioResultado,
                                      int cantidadItercaciones) {

        this.tiempoSimulacion = tiempo_simulacion;
        this.probabilidadesTipoTrabajo = probabilidadesTipoTrabajo;
        this.tiemposMediaTrabajo = tiemposMediaTrabajo;
        this.limite_inferiorUniforme = limite_inferiorUniforme;
        this.limite_superiorUniforme = limite_superiorUniforme;
        this.tiempoDesdeInicioEquipoC = tiempoDesdeInicioEquipoC;
        this.tiempoAntesFinEquipoC = tiempoAntesFinEquipoC;
        this.tiempoInicioResultado = tiempoInicioResultado;
        this.cantidadItercaciones = cantidadItercaciones;

        this.filaVectors.clear();

        System.out.println(this.tiempoSimulacion);

        //Creamos la primer fila, con todos los campos vacios excepto la hora de la proxima llegada, es decir la
        //primer
        double reloj = this.reloj;

        Llegada llegada_primera = new Llegada();
        llegada_primera.calcularTiempoEntreLlegada(reloj);
        llegada_primera.setTrabajo(null);
        llegada_primera.setRndTipoTrabajo(0.0);

        Evento eventoLlegada = new Evento();
        eventoLlegada.setTipoEvento(Eventos.Llegada);
        eventoLlegada.setHoraEvento(llegada_primera.getHoraProximaLlegada());
        this.proximosEventos.add(eventoLlegada);

        ColaVector colaVectorInicio = new ColaVector(0, 0, 0, 9);

        int contadorEquipo = 0;


        double horaCambioTrabajoC = 0.0;
        double horaReanudacionTrabajoC = 0.0;

        double hora_proximaLlegada = llegada_primera.getHoraProximaLlegada();
        double hora_proximoFinTrabajo = 0;
        double hora_reanudacion_trabajo_C = 0;

        FinTrabajo finTrabajo = new FinTrabajo(0, 0,
                0,
                0,
                0);

        Servidor servidorInicio = new Servidor(EstadoServidor.Libre,
                0,
                0,
                0,
                0);


        // Crea la primer fila, para inciar el trabajo
        this.filaActual = new FilaVector(
                Eventos.Inicio,
                reloj,
                llegada_primera,
                colaVectorInicio,
                contadorEquipo,
                0,
                0.0,
                finTrabajo,
                servidorInicio,
                new ArrayList<>());


        double tiempo_ocupacion = 0;

        double tiempo_permanencia_equipos = 0;

        reloj = llegada_primera.getHoraProximaLlegada();

        this.contadorIteraciones++;


        while (this.reloj < this.tiempoSimulacion) {
            ;

            this.buscarProximoEvento();
            this.filaAnterior = this.filaActual;


            // Llegada del primer equipo, verifica si el evento es de llegada;
            if (this.proximoEvento.getTipoEvento().equals(Eventos.Llegada)) {
                this.eventoLlegada();
            } else {
                if (proxima_llegada) {

                    ColaVector colaVectorProximaLlegada = new ColaVector(
                            this.filaAnterior.getColaVector().getColaComun(),
                            this.filaAnterior.getColaVector().getColaTrabajoC(),
                            this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                            this.filaAnterior.getColaVector().getLugaresLibres());

                    Servidor servidorAnterior = this.filaAnterior.servidor;

                    Llegada llegada1 = new Llegada();
                    llegada1.calcularTiempoEntreLlegada(reloj);
                    llegada1.calcularTipoTrabajo(trabajosArray, probabilidadesTipoTrabajo);
                    hora_proximaLlegada = reloj + llegada1.getTiempoEntreLlegada();

                    FinTrabajo finTrabajo2 = new FinTrabajo();
                    finTrabajo2.calcularMediaTiempo(probabilidadesTipoTrabajo, tiemposMediaTrabajo, reloj);


                    colaTrabajoFinalizado.add(finTrabajo2);
                    System.out.println(colaTrabajoFinalizado.toString());

                    for (FinTrabajo fintrabajo01 : colaTrabajoFinalizado) {
                        if (fintrabajo01.getHoraEvento() < hora_proximoFinTrabajo) {
                            hora_proximoFinTrabajo = fintrabajo01.getHoraEvento();
                            System.out.println("Hora fin trabajo" + hora_proximoFinTrabajo);
                        }
                    }


                    Servidor servidorProximaLlegada = new Servidor(
                            EstadoServidor.Libre,
                            this.filaAnterior.getServidor().horaInicioOcupacion,
                            this.filaAnterior.getServidor().horaFinOcupacion,
                            this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum,
                            this.filaAnterior.getServidor().tiempoOcupacionAcum);


                    if (servidorAnterior.getEstado().equals(EstadoServidor.Ocupado)) {
                        if (this.filaAnterior.getColaVector().getLugaresLibres() >= 1) {
                            if (llegada1.getTrabajo().equals(Trabajo.C)) {
                                colaVectorProximaLlegada.sumarColaTrabajoC();
                                double hora_atencion = reloj + tiempoDesdeInicioEquipoC;
                                double hora_cambioHora = reloj + tiempoDesdeInicioEquipoC;
                                double hora_fin_Anterior = finTrabajo2.getHoraEvento();
                                Equipo equipoColaC = new Equipo(this.contadorEquipos,
                                        EstadoEquipo.EncolaC,
                                        llegada1.getTrabajo(),
                                        reloj, 0,
                                        0,
                                        0);
                                colaTrabajoC.add(equipoColaC);
                            } else {
                                colaVectorProximaLlegada.sumarColaComun();
                                Equipo equipoColaComun = new Equipo(this.contadorEquipos,
                                        EstadoEquipo.EnCola,
                                        llegada1.getTrabajo(),
                                        reloj,
                                        0,
                                        0,
                                        0);
                                colaComun.add(equipoColaComun);
                            }
                        }

                        servidorProximaLlegada.setEstado(EstadoServidor.Ocupado);

                    } else {

                        servidorProximaLlegada.setEstado(EstadoServidor.Ocupado);
                        servidorProximaLlegada.setHoraFinOcupacion(reloj);
                        llegada1.calcularTiempoEntreLlegada(reloj);
                        llegada1.calcularTipoTrabajo(trabajosArray, probabilidadesTipoTrabajo);
                        finTrabajo2.calcularMediaTiempo(probabilidadesTipoTrabajo, tiemposMediaTrabajo, reloj);

                        Equipo equipo = new Equipo();

                        if (llegada1.getTrabajo().equals(Trabajo.C)) {
                            equipo.setEquipo_estado(EstadoEquipo.EncolaC);
                            equipo.setTipo_trabajo(llegada1.getTrabajo());
                            equipo.setHora_llegada(reloj);
                            equipo.setHora_Inicio_atencion(horaCambioTrabajoC);
                            equipo.setHoraFinAtencionEstimada(finTrabajo.horaEvento);

                            horaCambioTrabajoC = reloj + tiempoDesdeInicioEquipoC;

                        } else {
                            equipo.setEquipo_estado(EstadoEquipo.Atendido);
                            equipo.setTipo_trabajo(llegada1.getTrabajo());
                            equipo.setHora_llegada(reloj);
                            equipo.setHora_Inicio_atencion(horaCambioTrabajoC);
                            equipo.setHoraFinAtencionEstimada(finTrabajo.horaEvento);
                            equipo.setHora_salida(finTrabajo.horaEvento);
                            hora_proximoFinTrabajo = finTrabajo.getHoraEvento();
                        }
                    }

                    Servidor servidor1 = new Servidor(EstadoServidor.Ocupado, reloj, 0, 0, 0);
                    FilaVector filaVectorActual2 = new FilaVector(
                            Eventos.Llegada,
                            reloj,
                            llegada1,
                            colaVectorProximaLlegada,
                            this.filaAnterior.getContadorEquipo() + 1,
                            horaCambioTrabajoC,
                            horaReanudacionTrabajoC,
                            finTrabajo2,
                            servidorProximaLlegada,
                            trabajos_equipos);


                    filaVectors.add(filaVectorActual2);

                }

                if (proximoFinTrabajo) {

                    Llegada llegadaFinTrabajo = new Llegada();

                    ColaVector colaVectorFinTrabajo = new ColaVector(this.filaAnterior.getColaVector().getColaComun(),
                            this.filaAnterior.getColaVector().getColaTrabajoC(),
                            this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                            this.filaAnterior.getColaVector().getLugaresLibres());

                    this.filaAnterior.getServidor();

                    Servidor servidorFinTrabajo = new Servidor(EstadoServidor.Libre,
                            this.filaAnterior.getServidor().horaInicioOcupacion,
                            this.filaAnterior.getServidor().horaFinOcupacion,
                            this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum,
                            this.filaAnterior.getServidor().tiempoOcupacionAcum);

                    if (colaTrabajoC.size() != 0) {
                        System.out.println(colaTrabajoC.toString());
                        Optional<Equipo> equipoConMenorLlegada = colaTrabajoC.stream()
                                .filter(e -> e.getEquipo_estado().equals(EstadoEquipo.EncolaC) && e.getHoraFinAtencionEstimada() == 0.0)
                                .min(Comparator.comparingDouble(Equipo::getHora_llegada));

                        if (equipoConMenorLlegada.isPresent()) {
                            Equipo equipoMenorC = equipoConMenorLlegada.get();
                            equipoMenorC.setEquipo_estado(EstadoEquipo.Atendido);
                            equipoMenorC.setHora_Inicio_atencion(reloj);
                            equipoMenorC.setHoraFinAtencionEstimada(reloj + finTrabajo.getHoraEvento());
                            equipoMenorC.setHora_salida(reloj + finTrabajo.getHoraEvento());
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
                            System.out.println("Cola Comun");
                            System.out.println(colaComun.toString());

                            Optional<Equipo> equipoConMenorLlegada = colaComun.stream()
                                    .filter(e -> e.getEquipo_estado().equals(EstadoEquipo.EnCola) && e.getHoraFinAtencionEstimada() == 0.0)
                                    .min(Comparator.comparingDouble(Equipo::getHora_llegada));
                            if (equipoConMenorLlegada.isPresent()) {
                                Equipo equipoMenor = equipoConMenorLlegada.get();
                                equipoMenor.setEquipo_estado(EstadoEquipo.Atendido);
                                equipoMenor.setHora_Inicio_atencion(reloj);
                                equipoMenor.setHoraFinAtencionEstimada(reloj + finTrabajo.getHoraEvento());
                                equipoMenor.setHora_salida(reloj + finTrabajo.getHoraEvento());
                                double hora_llegada = equipoMenor.getHora_llegada();
                                double hora_salida = equipoMenor.getHora_salida();
                                double tiempo_permanencia = hora_salida - hora_llegada;
                                tiempo_permanencia_equipos += tiempo_permanencia;
                                colaVectorFinTrabajo.setColaComun(colaVectorFinTrabajo.getColaComun() - 1);
                                trabajos_equipos.add(equipoMenor);
                            }
                        }

                    }

                    FilaVector filaVectorActual3 = new FilaVector(Eventos.FinTrabajo,
                            reloj,
                            llegadaFinTrabajo,
                            colaVectorFinTrabajo,
                            contadorEquipo,
                            horaCambioTrabajoC,
                            horaReanudacionTrabajoC,
                            finTrabajo,
                            servidorFinTrabajo,
                            trabajos_equipos);

                    filaVectors.add(filaVectorActual3);

                    for (FinTrabajo fintrabajo01 : colaTrabajoFinalizado) {
                        if (fintrabajo01.getHoraEvento() < hora_proximoFinTrabajo && fintrabajo01.getHoraEvento() < reloj) {
                            hora_proximoFinTrabajo = fintrabajo01.getHoraEvento();
                            System.out.println("Hora fin trabajo: " + hora_proximoFinTrabajo);
                        } else {
                            hora_proximoFinTrabajo = 100;
                        }
                    }


                }

                if (proximoCambio_trabajo) {


                }

                if (proximoReanudacion_trabajo) {

                    ColaVector colaProximaReanudacion = new ColaVector(
                            this.filaAnterior.getColaVector().getColaComun(),
                            this.filaAnterior.getColaVector().getColaTrabajoC(),
                            this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                            this.filaAnterior.getColaVector().getLugaresLibres());
                    Servidor servidorAnterior = this.filaAnterior.getServidor();
                    if (servidorAnterior.getEstado().equals(EstadoServidor.Libre)) {

                        Equipo equipoColaAnterior = colaTrabajoC.get(0);
                        colaTrabajoC.remove(0);
                        Llegada llegada1 = new Llegada();
                        horaReanudacionTrabajoC = equipoColaAnterior.getHoraFinAtencionEstimada() - tiempoAntesFinEquipoC;
                        hora_reanudacion_trabajo_C = horaReanudacionTrabajoC;

                        servidorAnterior.setEstado(EstadoServidor.Libre);

                        FilaVector filaVector = new FilaVector(Eventos.Cambio, reloj, llegada1, colaProximaReanudacion, contadorEquipo, horaCambioTrabajoC, horaReanudacionTrabajoC, finTrabajo, servidorAnterior, trabajos_equipos);

                        filaVectors.add(filaVector);
                    }


                }


            }


            if (hora_proximaLlegada < hora_proximoFinTrabajo && hora_proximaLlegada < horaCambioTrabajoC && hora_proximaLlegada < hora_reanudacion_trabajo_C) {
                System.out.println("Llegada");
                reloj = hora_proximaLlegada;
                this.contadorEquipos++;
                proxima_llegada = true;
                proximoFinTrabajo = false;
                proximoCambio_trabajo = false;
                proximoReanudacion_trabajo = false;

            }
            ;

            if (hora_proximoFinTrabajo < hora_proximaLlegada && hora_proximoFinTrabajo < horaCambioTrabajoC && hora_proximoFinTrabajo < hora_reanudacion_trabajo_C) {
                System.out.println("Fin Trabajo");
                reloj = hora_proximoFinTrabajo;
                proximoFinTrabajo = true;
                proxima_llegada = false;
                proximoCambio_trabajo = false;
                proximoReanudacion_trabajo = false;

            }
            ;

            if (horaCambioTrabajoC < hora_proximaLlegada && horaCambioTrabajoC < horaCambioTrabajoC && hora_reanudacion_trabajo_C < hora_proximoFinTrabajo) {
                System.out.println("Cambio Trabajo"
                );
                reloj = horaCambioTrabajoC;
                proximoCambio_trabajo = true;
                proximoFinTrabajo = false;
                proxima_llegada = false;
                proximoReanudacion_trabajo = false;
            }
            ;

            if (hora_reanudacion_trabajo_C < hora_proximaLlegada && hora_reanudacion_trabajo_C < hora_proximoFinTrabajo && hora_reanudacion_trabajo_C < horaCambioTrabajoC) {
                System.out.println("Reanudacion Trabajo");
                reloj = hora_reanudacion_trabajo_C;
                proximoReanudacion_trabajo = true;
                proximoCambio_trabajo = false;
                proximoFinTrabajo = false;
                proxima_llegada = false;
            }
            ;

            if (this.tiempoInicioResultado >= this.reloj && this.contadorIteraciones <= this.cantidadItercaciones) {
                filaVectors.add(filaActual);
            }
        }

        return filaVectors;

    }

}
