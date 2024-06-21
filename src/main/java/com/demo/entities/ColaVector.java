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

    public void calcularLugaresLibres(
            Integer colaComun,
            Integer trabajoCSegundoPlano,
            Integer colaTrabajoC
    ) {
        this.colaComun = colaComun;
        this.trabajoCSegundoPlano = trabajoCSegundoPlano;
        this.colaTrabajoC = colaTrabajoC;
        this.lugaresLibres = 9 - (this.colaComun + this.trabajoCSegundoPlano + this.colaTrabajoC);
    }
}
