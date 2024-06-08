package com.demo.entities.Estados;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor

public enum Evento {
    Inicio,
    Fin,
    Llegada,
    FinTrabajo,
    Cambio,
    Reanudacion
}
