package com.demo.entities;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
@NoArgsConstructor
public class RungeKutta {

    public EquipoCRK calculo_rungeKutta(Double nSuma, Double nExpo, Double A, Double B, Integer idEquipo) {

        Random random = new Random();
        double rnd = random.nextDouble();
        double t = 0;
        double corte = A + rnd * (B - A);

        double h = 0.1;
        double k1, k2, k3, k4 ;
        double c = 0;
        while( c <= corte){

            k1 = h * (0.1 + Math.exp(nExpo * c));

            double y1 = c + (0.5 * k1);
            k2 = h * (nSuma + (Math.exp(nExpo * y1)));

            double y2 = c + (0.5 * k2);
            k3 = h * (nSuma + (Math.exp(nExpo * y2)));


            double y3 = c + k3;
            k4 = h * (nSuma + (Math.exp(nExpo * y3)));


            c = c + (1.0 / 6.0) * (k1 + 2 * k2 + 2 * k3 + k4);
            t = t + h;
        }
        return new EquipoCRK(idEquipo, rnd, corte, t, t/60);
    }
}
