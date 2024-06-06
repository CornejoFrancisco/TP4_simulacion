package com.demo.entities;


import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Llegada {

    public double rndLlegada;
    public double tiempoEntreLlegada;
    public double tiempoHoraProximaLlegada;

    public int rndTipoTrabajo;
    public Trabajo trabajo;


    public void calcularEntreLlegada(Integer rndLlegada, double reloj, double A, double B) {
        double tiempo_entre_llegada = A + rndLlegada * (B - A);
        this.tiempoHoraProximaLlegada = tiempo_entre_llegada + reloj;
    }
}
