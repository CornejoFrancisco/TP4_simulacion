package com.demo.entities;

import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipoCola {
    public Trabajo trabajo;
    public double hora_atencion;
    public double horaCambioTrabjo;
    public double horaReanudacionTrabajo;
}
