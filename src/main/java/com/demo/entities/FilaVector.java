package com.demo.entities;

import com.demo.entities.Estados.Evento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FilaVector {
    public Evento evento;
    public int reloj;

    public Llegada llegada;
    public ColaVector colaVector;
    public int contadorEquipo;
    public double horaCambioTrabajoC;
    public double horaReanudacionTrabajoC;
    public FinTrabajo finTrabajo;
    public Servidor servidor;
    public ArrayList<Equipo> equipoList;

}
