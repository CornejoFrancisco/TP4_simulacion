package com.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_Respuesta {
    private ArrayList<FilaVector> filas = new ArrayList<>();
    private Double promedioPermanencia = null;
    private Double porcentajeOcupacionServidor = null;

    public void calcularPromedioPermanencia(Integer cantidadEquipos, Double tiempoPermanenciaEquipoAcum){
        this.promedioPermanencia = tiempoPermanenciaEquipoAcum / cantidadEquipos;
    }

    public void calcularPorcentajeOcupacion(Double tiempoSimulacion, Double tiempoOcupacionAcum){
        this.porcentajeOcupacionServidor = (tiempoOcupacionAcum / tiempoSimulacion) * 100;
    }

}

