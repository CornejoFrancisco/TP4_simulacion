package com.demo.controllers;

import com.demo.entities.FilaVector;
import com.demo.entities.SimulacionRequest;
import com.demo.services.SimulacionPractica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {

    @PostMapping("/simular")
    public ResponseEntity<List<FilaVector>> simular(@RequestBody(required = false) SimulacionRequest simulacionRequest) {
        System.out.println(simulacionRequest);

        double probTA = simulacionRequest.getProbTA();
        double probTB = simulacionRequest.getProbTB();
        double probTC = simulacionRequest.getProbTC();
        double probTD = simulacionRequest.getProbTD();
        double timeTA = simulacionRequest.getTimeTA();
        double timeTB = simulacionRequest.getTimeTB();
        double timeTC = simulacionRequest.getTimeTC();
        double timeTD = simulacionRequest.getTimeTD();
        double timeMin = simulacionRequest.getTimeMin();
        double timeMax = simulacionRequest.getTimeMax();
        double timeInitTC = simulacionRequest.getTimeInitTC();
        double timeEndTC = simulacionRequest.getTimeEndTC();
        double cantTimeSim = simulacionRequest.getCantTimeSim();
        double initTimeView = simulacionRequest.getInitTimeView();
        int cantSimIterations = simulacionRequest.getCantSimIterations();

        double[] probabilidadesOcurrencia = new double[4];
        probabilidadesOcurrencia[0] = probTA;
        probabilidadesOcurrencia[1] = probTB;
        probabilidadesOcurrencia[2] = probTC;
        probabilidadesOcurrencia[3] = probTD;

        double[] tiemposDemora = new double[4];
        tiemposDemora[0] = timeTA;
        tiemposDemora[1] = timeTB;
        tiemposDemora[2] = timeTC;
        tiemposDemora[3] = timeTD;

        SimulacionPractica simulacion = new SimulacionPractica();
        ArrayList<FilaVector> values = simulacion.cola(
                cantTimeSim, // tiempo_simulacion
                probabilidadesOcurrencia,
                tiemposDemora,
                timeMin, // limite_inferiorUniforme
                timeMax, // limite_superiorUniforme
                timeInitTC, // tiempo_equipoC
                timeEndTC // tiempo_equipoCHasta
        );
        return ResponseEntity.ok(values);
    }
}
