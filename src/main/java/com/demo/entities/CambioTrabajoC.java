package com.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CambioTrabajoC {
    private double horaEvento;
    private Equipo equipo;
}
