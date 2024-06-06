package com.demo.entities;

import com.demo.entities.Estados.EstadoEquipo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class Equipo {
    public int id_equipo;
    public EstadoEquipo equipo_estado;
    public int hora_llegada;

    public int hora_atencion;
    public int hora_fin_atencion;
    public int hora_salida;
}
