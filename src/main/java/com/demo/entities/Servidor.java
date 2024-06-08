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

    public void setHoraFinOcupacion(double horaFinOcupacion) {
        this.horaFinOcupacion = horaFinOcupacion;
        this.tiempoOcupacionAcum = this.tiempoOcupacionAcum + this.horaFinOcupacion - this.horaInicioOcupacion;
    }

    public void setTiempoPermanenciaEquipoAcum(double tiempoPermanenciaEquipoAcum) {
        this.tiempoPermanenciaEquipoAcum = this.tiempoPermanenciaEquipoAcum + tiempoPermanenciaEquipoAcum;
    }
}
