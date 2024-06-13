package com.demo.services;

import com.demo.entities.*;
import com.demo.entities.Estados.EstadoEquipo;
import com.demo.entities.Estados.EstadoServidor;
import com.demo.entities.Estados.Eventos;
import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionPractica extends Simulacion {
    private ArrayList<Trabajo> trabajos = new ArrayList<>(Arrays.asList(Trabajo.values()));
    private Trabajo[] trabajosArray = trabajos.toArray(new Trabajo[0]);
    private double tiempoSimulacion;
    private ArrayList<Double> probabilidadesTipoTrabajo;
    private ArrayList<Double> tiemposMediaTrabajo;
    private double limite_inferiorUniforme;
    private double limite_superiorUniforme;
    private double tiempoDesdeInicioEquipoC;
    private double tiempoAntesFinEquipoC;
    private double tiempoInicioResultado;
    private int cantidadItercaciones;

    private ArrayList<FilaVector> vectorDeEstados = new ArrayList<>();
    private ArrayList<Equipo> trabajos_equipos = new ArrayList<>();
    private ArrayList<Equipo> colaComun = new ArrayList<>();
    private ArrayList<Equipo> colaTrabajoC = new ArrayList<>();
    private ArrayList<Equipo> trabajoCSegundoPlano = new ArrayList<>();
    private ArrayList<FinTrabajo> colaTrabajoFinalizado = new ArrayList<>();
    private ArrayList<Evento> proximosEventos = new ArrayList<>();
    private FilaVector filaActual = null;
    private FilaVector filaAnterior = null;
    private int contadorIteraciones = 0;
    private int contadorIteracionesResultado = 1;

    private double reloj = 0;
    private Evento proximoEvento = null;
    private int contadorEquipos = 0;


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


    public Dto_Respuesta cola(double tiempo_simulacion,
                                      ArrayList<Double> probabilidadesTipoTrabajo,
                                      ArrayList<Double> tiemposMediaTrabajo,
                                      double limite_inferiorUniforme,
                                      double limite_superiorUniforme,
                                      double tiempoDesdeInicioEquipoC,
                                      double tiempoAntesFinEquipoC,
                                      double tiempoInicioResultado,
                                      int cantidadItercaciones) {

        this.tiempoSimulacion = tiempo_simulacion;
        this.probabilidadesTipoTrabajo = probabilidadesTipoTrabajo;
        this.tiemposMediaTrabajo = tiemposMediaTrabajo;
        this.limite_inferiorUniforme = limite_inferiorUniforme / 60;
        this.limite_superiorUniforme = limite_superiorUniforme / 60;
        this.tiempoDesdeInicioEquipoC = tiempoDesdeInicioEquipoC / 60;
        this.tiempoAntesFinEquipoC = tiempoAntesFinEquipoC / 60;
        this.tiempoInicioResultado = tiempoInicioResultado;
        this.cantidadItercaciones = cantidadItercaciones;

        this.vectorDeEstados.clear();
        this.trabajos_equipos.clear();
        this.colaComun.clear();
        this.colaTrabajoC.clear();
        this.trabajoCSegundoPlano.clear();
        this.colaTrabajoFinalizado.clear();
        this.proximosEventos.clear();

        this.filaActual = null;
        this.filaAnterior = null;
        this.contadorIteraciones = 0;
        this.contadorIteracionesResultado = 0;

        this.reloj = 0;
        this.proximoEvento = null;
        this.contadorEquipos = 0;
        this.vectorDeEstados.clear();

        //Creamos la primer fila, con todos los campos vacios excepto la hora de la proxima llegada, es decir la
        //primer
        double reloj = this.reloj;

        Llegada llegada_primera = new Llegada();
        llegada_primera.calcularTiempoEntreLlegada(reloj);
        llegada_primera.setTrabajo(null);
        llegada_primera.setRndTipoTrabajo(0.0);

        this.proximosEventos.add(new Evento(
                Eventos.Llegada,
                llegada_primera.getHoraProximaLlegada(),
                null
        ));

        ColaVector colaVectorInicio = new ColaVector(
                this.colaComun.size(),
                this.colaTrabajoC.size(),
                this.trabajoCSegundoPlano.size(),
                9);

        int contadorEquipos = this.contadorEquipos;

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
                this.reloj,
                llegada_primera,
                colaVectorInicio,
                contadorEquipos,
                0,
                0,
                finTrabajo,
                servidorInicio);


        if (this.reloj >= this.tiempoInicioResultado && this.contadorIteracionesResultado <= this.cantidadItercaciones){
            this.vectorDeEstados.add(this.filaActual);
            this.contadorIteracionesResultado++;
        }
        this.contadorIteraciones++;

        while (this.reloj < this.tiempoSimulacion && this.contadorIteraciones <= 100000) {
            this.buscarProximoEvento();
            this.filaAnterior = this.filaActual;
            this.reloj = this.proximoEvento.getHoraEvento();

            // Evento llegada;
            if (this.proximoEvento.getTipoEvento().equals(Eventos.Llegada)) {
                this.eventoLlegada();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.Cambio)) {
                this.eventoCambioTrabajo();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.Reanudacion)) {
                this.eventoReanudacionTrabajo();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.FinTrabajo)) {
                this.eventoFinTrabajo();
            }

            if (this.reloj >= this.tiempoInicioResultado && this.contadorIteracionesResultado <= this.cantidadItercaciones) {
                this.contadorIteracionesResultado++;
                this.vectorDeEstados.add(this.filaActual);
            }
            this.contadorIteraciones++;

        }

        if (this.vectorDeEstados.getLast() != this.filaActual) {
            this.vectorDeEstados.add(this.filaActual);
        }

        this.filaActual.servidor.setHoraFinOcupacion(this.reloj);
        this.filaActual.servidor.acumularTiempoOcupacion();

        Dto_Respuesta resultados = new Dto_Respuesta();
        resultados.setFilas(vectorDeEstados);
        resultados.setEquipos(trabajos_equipos);
        resultados.calcularPromedioPermanencia(this.contadorEquipos, this.filaActual.servidor.getTiempoPermanenciaEquipoAcum());
        resultados.calcularPorcentajeOcupacion(this.reloj, this.filaActual.servidor.getTiempoOcupacionAcum());

        return resultados;
    }

    private void eventoFinTrabajo() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().horaInicioOcupacion,
                this.filaAnterior.getServidor().horaFinOcupacion,
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum);

        Equipo equipoFinalizacion = this.proximoEvento.getEquipo();
        FinTrabajo finTrabajo = new FinTrabajo();
        Double horaCambioTrabajoC = 0.00;

        if (colasEstadoActual.getColaTrabajoC() > 0) {

            Equipo equipoEnColaC = this.colaTrabajoC.getFirst();
            this.colaTrabajoC.remove(equipoEnColaC);
            equipoEnColaC.setEquipo_estado(EstadoEquipo.Atendido);
            colasEstadoActual.restarColaC();

            finTrabajo.setIdEquipoFinTrabajo(equipoEnColaC.getId_equipo());
            finTrabajo.setTiempoAtencion(this.tiempoAntesFinEquipoC);
            finTrabajo.setHoraFinTrabajo(this.reloj + this.tiempoAntesFinEquipoC);

            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            this.reloj + this.tiempoAntesFinEquipoC,
                            equipoEnColaC
                    )
            );

        } else if (colasEstadoActual.getColaComun() > 0) {

            colasEstadoActual.restarColaComun();
            Equipo equipoEnColaComun = this.colaComun.getFirst();
            this.colaComun.remove(equipoEnColaComun);
            equipoEnColaComun.setEquipo_estado(EstadoEquipo.Atendido);
            equipoEnColaComun.setHora_Inicio_atencion(this.reloj);


            finTrabajo.calcularHoraFinTrabajo(
                    equipoEnColaComun.getTipo_trabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme
            );

            equipoEnColaComun.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());

            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipoEnColaComun
                    )
            );

            if (equipoEnColaComun.getTipo_trabajo().equals(Trabajo.C)) {
                horaCambioTrabajoC = this.reloj + tiempoDesdeInicioEquipoC;
                this.proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                this.reloj + this.tiempoDesdeInicioEquipoC,
                                equipoEnColaComun
                        )
                );
            }

        } else {
            servidorActual.setEstado(EstadoServidor.Libre);
            servidorActual.setHoraFinOcupacion(this.reloj);
            servidorActual.acumularTiempoOcupacion();
        }

        equipoFinalizacion.setEquipo_estado(EstadoEquipo.Finalizado);
        equipoFinalizacion.setHora_salida(this.reloj);
        servidorActual.acumTiempoPermanenciaEquipoAcum(
                equipoFinalizacion.getHora_salida() - equipoFinalizacion.getHora_llegada());
        this.filaActual = new FilaVector(
                Eventos.FinTrabajo,
                this.reloj,
                new Llegada(),
                colasEstadoActual,
                this.contadorEquipos,
                horaCambioTrabajoC,
                0,
                finTrabajo,
                servidorActual
        );

    }

    private void eventoReanudacionTrabajo() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().horaInicioOcupacion,
                this.filaAnterior.getServidor().horaFinOcupacion,
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum);

        Equipo equipoReanudacion = this.proximoEvento.getEquipo();
        if (servidorActual.getEstado().equals(EstadoServidor.Ocupado)) {
            equipoReanudacion.setEquipo_estado(EstadoEquipo.EncolaC);
            colasEstadoActual.sumarColaTrabajoC();
            this.colaTrabajoC.add(equipoReanudacion);
            this.anularFinTrabajoC(equipoReanudacion.getId_equipo());
        } else {
            equipoReanudacion.setEquipo_estado(EstadoEquipo.Atendido);
            servidorActual.setEstado(EstadoServidor.Ocupado);
            servidorActual.setHoraInicioOcupacion(this.reloj);
            colasEstadoActual.restarTrabajoCSegundoPlano();
        }

        this.filaActual = new FilaVector(
                Eventos.Reanudacion,
                this.reloj,
                new Llegada(),
                colasEstadoActual,
                this.contadorEquipos,
                0,
                0,
                new FinTrabajo(),
                servidorActual
        );

    }

    private void anularFinTrabajoC(Integer idEquipo) {
        for (Evento evento : this.proximosEventos) {
            if (evento.getTipoEvento().equals(Eventos.FinTrabajo) && evento.getEquipo().getId_equipo() == idEquipo) {
                this.proximosEventos.remove(evento);
                break;
            }
        }
    }


    private void eventoCambioTrabajo() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().horaInicioOcupacion,
                this.filaAnterior.getServidor().horaFinOcupacion,
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum
        );

        this.proximoEvento.getEquipo().setEquipo_estado(EstadoEquipo.At2doplano);
        colasEstadoActual.sumarTrabajoCSegundoPlano();

        this.proximosEventos.add(
                new Evento(
                        Eventos.Reanudacion,
                        this.proximoEvento.getEquipo().horaFinAtencionEstimada - this.tiempoAntesFinEquipoC,
                        this.proximoEvento.getEquipo()
                )
        );


        Double horaReanudacionTrabajoC =
                this.proximoEvento.getEquipo().horaFinAtencionEstimada - this.tiempoAntesFinEquipoC;
        Double horaCambioTrabajoC = 0.00;
        FinTrabajo finTrabajo = new FinTrabajo();

        if (colasEstadoActual.getColaTrabajoC() > 0) {
            Equipo equipoEnColaC = this.colaTrabajoC.getFirst();
            equipoEnColaC.setEquipo_estado(EstadoEquipo.Atendido);
            colasEstadoActual.restarColaC();


            finTrabajo.setTiempoAtencion(this.tiempoAntesFinEquipoC);
            finTrabajo.setHoraFinTrabajo(this.reloj + this.tiempoAntesFinEquipoC);

            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            this.reloj + this.tiempoAntesFinEquipoC,
                            equipoEnColaC
                    )
            );

        } else if (colasEstadoActual.getColaComun() > 0) {

            Equipo equipoEnColaComun = this.colaComun.getFirst();
            this.colaComun.remove(equipoEnColaComun);
            colasEstadoActual.restarColaComun();

            finTrabajo.calcularHoraFinTrabajo(
                    equipoEnColaComun.getTipo_trabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme
            );
            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipoEnColaComun
                    )
            );
            equipoEnColaComun.setEquipo_estado(EstadoEquipo.Atendido);
            equipoEnColaComun.setHora_Inicio_atencion(this.reloj);
            equipoEnColaComun.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());
            if (equipoEnColaComun.getTipo_trabajo().equals(Trabajo.C)) {
                horaCambioTrabajoC = this.reloj + this.tiempoDesdeInicioEquipoC;
                this.proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                horaCambioTrabajoC,
                                equipoEnColaComun
                        )
                );
            }
        } else {
            servidorActual.setEstado(EstadoServidor.Libre);
            servidorActual.setHoraFinOcupacion(this.reloj);
            servidorActual.acumularTiempoOcupacion();
        }
        this.filaActual = new FilaVector(
                Eventos.Cambio,
                this.reloj,
                new Llegada(),
                colasEstadoActual,
                this.contadorEquipos,
                horaCambioTrabajoC,
                horaReanudacionTrabajoC,
                finTrabajo,
                servidorActual
        );
    }


    private void eventoLlegada() {
        // Copia el estado de las colas de la fila anterior
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        // Copia el estado del servidor de la fila anterior
        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().getHoraInicioOcupacion(),
                0,
                this.filaAnterior.getServidor().getTiempoOcupacionAcum(),
                this.filaAnterior.getServidor().getTiempoPermanenciaEquipoAcum());


        double horaCambioTrabajoC = this.filaAnterior.getHoraCambioTrabajoC();

        Llegada llegadaEquipo = new Llegada();
        llegadaEquipo.calcularTiempoEntreLlegada(this.reloj);

        this.proximosEventos.add(
                new Evento(
                        Eventos.Llegada,
                        llegadaEquipo.getHoraProximaLlegada(),
                        null)
        );

        FinTrabajo finTrabajo = new FinTrabajo();
        Equipo equipo = new Equipo();

        if (servidorActual.getEstado().equals(EstadoServidor.Ocupado)) {
            if (colasEstadoActual.getLugaresLibres() > 0) {
                this.contadorEquipos++;
                llegadaEquipo.calcularTipoTrabajo(trabajos, probabilidadesTipoTrabajo);
                colasEstadoActual.sumarColaComun();
                this.colaComun.add(equipo);

                equipo.setId_equipo(this.contadorEquipos);
                equipo.setEquipo_estado(EstadoEquipo.EnCola);
                equipo.setHora_llegada(reloj);
                equipo.setTipo_trabajo(llegadaEquipo.getTrabajo());
                trabajos_equipos.add(equipo);
            }
        } else {
            this.contadorEquipos++;
            servidorActual.setEstado(EstadoServidor.Ocupado);
            servidorActual.setHoraInicioOcupacion(reloj);

            llegadaEquipo.calcularTipoTrabajo(trabajos, probabilidadesTipoTrabajo);

            finTrabajo.calcularHoraFinTrabajo(
                    llegadaEquipo.getTrabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme);

            finTrabajo.setIdEquipoFinTrabajo(this.contadorEquipos);

            equipo.setId_equipo(this.contadorEquipos);
            equipo.setEquipo_estado(EstadoEquipo.Atendido);
            equipo.setTipo_trabajo(llegadaEquipo.getTrabajo());
            equipo.setHora_llegada(reloj);
            equipo.setHora_Inicio_atencion(reloj);
            equipo.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());

            proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipo)
            );

            if (llegadaEquipo.getTrabajo().equals(Trabajo.C)) {
                horaCambioTrabajoC = this.reloj + tiempoDesdeInicioEquipoC;
                proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                horaCambioTrabajoC,
                                equipo)
                );
            }
            trabajos_equipos.add(equipo);
        }

        this.filaActual = new FilaVector(
                Eventos.Llegada,
                this.reloj,
                llegadaEquipo,
                colasEstadoActual,
                this.contadorEquipos,
                horaCambioTrabajoC,
                0,
                finTrabajo,
                servidorActual);
    }
}

