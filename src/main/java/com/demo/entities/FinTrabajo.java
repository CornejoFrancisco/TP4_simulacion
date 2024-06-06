package com.demo.entities;

import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class FinTrabajo {
    public int rndFinTrabajo;

    public double mediaTiempoAtencion;
    public int tiempoAtencion;

    public double horafinTrabajo;



    public void calcularMediaTiempo(double rndLlegada, ArrayList<Double> lista_probabilidad, List<Double> tiempo_trabajo, double reloj) {

        double media_tiempo = 0;
        for (int i = 0; i < lista_probabilidad.size(); i++) {
            if (rndLlegada < lista_probabilidad.get(i)) {

                media_tiempo = tiempo_trabajo.get(i);
                break;
            }
        }
        this.mediaTiempoAtencion = (media_tiempo- 0.083)+rndLlegada*((media_tiempo + 0.083) - (media_tiempo - 0.083));
        this.horafinTrabajo = reloj + this.mediaTiempoAtencion;
    }

}
