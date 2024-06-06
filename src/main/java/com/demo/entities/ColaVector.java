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


}
