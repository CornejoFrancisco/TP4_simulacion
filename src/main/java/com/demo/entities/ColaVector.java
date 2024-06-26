package com.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ColaVector {

    public int colaComun;
    public int colaTrabajoC;
    public int trabajoCSegundoPlano;
    public int lugaresLibres;



    public void sumarColaComun() {
        this.colaComun++;
        this.lugaresLibres--;
    }

    public void sumarColaTrabajoC() {
        this.colaTrabajoC++;
        this.lugaresLibres--;
    }

    public void sumarTrabajoCSegundoPlano() {
        this.trabajoCSegundoPlano++;
        this.lugaresLibres--;
    }
}
