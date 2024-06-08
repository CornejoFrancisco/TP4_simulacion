package com.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_Respuesta {
    public String evento;
    public double reloj;
    public double rndLlegada;
    public double tiempoLlegada;
    public double proximaLlegada;
    public double rndTrabajoTipo;
    public String trabajoTipo;
    public Integer colaComun;
    public Integer colaTrabajoC;
    public Integer trabajoCSegundoPlano;
    public Integer lugaresLibres;
    public Integer contadorEquipo;
    public double horaCambioTrabajoC;
    public double horaReanudacionTrabajoC;
    public double rndFinTrabajo;
    public double tiempoFinTrabajo;
    public double horaFinTrabajo;
    public String estadoServidor;
    public double inicioOcupacionServidor;
    public double finOcupacionServidor;
    public double tiempoOcupacionServidor;
    public double tiempoPermaneciaEquipo;
}
