package com.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RungeKutta {

    private double tiempo;
    private Integer c;
    private double rnd;

    public void calculo_rungeKutta(double numero_ingresado, double numero_ingresado_elevado, double A, double B) {

        Random random = new Random();
        double rnd = random.nextDouble();
        double t = 0;
        double corte = A + rnd * (B - A);

        double h = 0.1;
        double k1, k2, k3, k4 ;
        double c = 0;
        while( c <= corte){

            k1 = h * (0.1 + Math.exp(numero_ingresado_elevado * c));

            double y1 = c + (0.5 * k1);
            k2 = h * (numero_ingresado + (Math.exp(numero_ingresado_elevado * y1)));

            double y2 = c + (0.5 * k2);
            k3 = h * (numero_ingresado + (Math.exp(numero_ingresado_elevado * y2)));


            double y3 = c + k3;
            k4 = h * (numero_ingresado + (Math.exp(numero_ingresado_elevado * y3)));


            c = c + (1.0 / 6.0) * (k1 + 2 * k2 + 2 * k3 + k4);
            t = t + h;



        }
        this.tiempo = t;


    }

}
