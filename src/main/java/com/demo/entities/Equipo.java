package com.demo.entities;

import com.demo.entities.Estados.EstadoEquipo;
import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipo {
    private int id_equipo;
    private EstadoEquipo equipo_estado;
    private Trabajo tipo_trabajo;
    private double hora_llegada;
    private double hora_Inicio_atencion;
    private double horaReanudacionTrabajoC;
    private double horaFinAtencionEstimada;
    private double hora_salida;
    private boolean yaTermino;
}
