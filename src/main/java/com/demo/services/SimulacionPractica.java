package com.demo.services;


import com.demo.entities.FilaVector;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class SimulacionPractica extends Simulacion{


    // En todos los casos el tiempo indicado es la media de una distribución uniforme con 5 minutos de
    //amplitud del intervalo en más y 5 minutos en menos
    // 0.5 + rndLlegada * (1.5 - 0.5) seria asi?
    public ArrayList<FilaVector> cola(double tiempo_simulacion,
                                      ArrayList<Double> probabilidadesOcurrencia,
                                        ArrayList<Double> tiempo_Demora,
                                      double limite_inferiorUniforme,
                                        double limite_superiorUniforme,
                                      double tiempo_equipoC,
                                      double tiempo_equipoCHasta){

        if(probabilidadesOcurrencia != null){
            setProbabilidadesOcurrencia(new ArrayList<>(probabilidadesOcurrencia));
        } else {
            setProbabilidadesOcurrencia(new ArrayList<>(Arrays.asList(0.3, 0.25, 0.25, 0.2)));
        }

        if(tiempo_Demora != null){
            setTiempo_Demora(new ArrayList<>(tiempo_Demora));
        } else {
            setTiempo_Demora(new ArrayList<>(Arrays.asList(2.0,1.0,3.0,1.0)));
        }
        if(tiempo_simulacion == 0){
            tiempo_simulacion = 100;
        }
        if(limite_inferiorUniforme == 0){
            limite_inferiorUniforme = 0.5;
        }
        if(limite_superiorUniforme == 0){
            limite_superiorUniforme = 1.5;
        }

        if(tiempo_equipoC == 0){
            tiempo_equipoC = 0.083;
        }
        if(tiempo_equipoCHasta == 0){
            tiempo_equipoCHasta = 0.083;
        }

        double reloj = 0;

        while (tiempo_simulacion > reloj){


        }
    }

}
