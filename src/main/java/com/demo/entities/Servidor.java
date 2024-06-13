package com.demo.entities;

import com.demo.entities.Estados.EstadoServidor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Servidor {
    public EstadoServidor estado;
    public double horaInicioOcupacion;
    public double horaFinOcupacion;
    public double tiempoOcupacionAcum;
    public double tiempoPermanenciaEquipoAcum;

    public void acumularTiempoOcupacion() {
        this.tiempoOcupacionAcum = this.tiempoOcupacionAcum + this.horaFinOcupacion - this.horaInicioOcupacion;
    }

    public void acumTiempoPermanenciaEquipoAcum(double tiempoPermanenciaEquipoAcum) {
        this.tiempoPermanenciaEquipoAcum = this.tiempoPermanenciaEquipoAcum + tiempoPermanenciaEquipoAcum;
    }
}
