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
    public double hora_llegada;

    public double hora_atencion;
    public double hora_fin_atencion;
    public double hora_salida;
}
