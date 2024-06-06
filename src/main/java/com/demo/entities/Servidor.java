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
    public int horaInicioOcupacion;
    public int horaFinOcupacion;
    public int tiempoOcupacionAcum;
    public int tiempoPermanenciaEquipoAcum;




}
