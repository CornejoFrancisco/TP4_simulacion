package com.demo.entities;

import com.demo.entities.Estados.Evento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FilaVector {
    public Evento evento;
    public int reloj;

    public Llegada llegada;
    public ColaVector colaVector;
    public int contadorEquipo;

    public FinTrabajo finTrabajo;

}
