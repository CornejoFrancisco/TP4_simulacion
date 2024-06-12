package com.demo.entities;

import com.demo.entities.Estados.EstadoEquipo;
import com.demo.entities.Estados.Evento;
import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class Equipo {
    public int id_equipo;
    public EstadoEquipo equipo_estado;
    public Trabajo tipo_trabajo;

    public double hora_llegada;

    public double hora_Inicio_atencion;
    public double hora_fin_atencion;
    public double hora_salida;



}
