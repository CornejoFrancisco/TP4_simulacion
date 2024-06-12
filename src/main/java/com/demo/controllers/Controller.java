package com.demo.controllers;

import com.demo.entities.Dto_request;
import com.demo.entities.FilaVector;
import com.demo.services.SimulacionPractica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controller {

    private SimulacionPractica simulacionPractica;

    public Controller(SimulacionPractica simulacionPractica) {
        this.simulacionPractica = simulacionPractica;
    }

    @PostMapping("/simular")
    public ResponseEntity<List<FilaVector>> simular(@RequestBody(required = false) Dto_request simulacionRequest) {


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

        // Crea un array con las probabilidades
        double[] probabilidadesOcurrencia = new double[4];
        probabilidadesOcurrencia[0] = probTA;
        probabilidadesOcurrencia[1] = probTB;
        probabilidadesOcurrencia[2] = probTC;
        probabilidadesOcurrencia[3] = probTD;

        // Crea un array con los tiempos medio de trabajo
        double[] tiemposDemora = new double[4];
        tiemposDemora[0] = timeTA;
        tiemposDemora[1] = timeTB;
        tiemposDemora[2] = timeTC;
        tiemposDemora[3] = timeTD;

        ArrayList<FilaVector> values = simulacionPractica.cola(
                cantTimeSim, // tiempo_simulacion
                probabilidadesOcurrencia,
                tiemposDemora,
                timeMin, // limite_inferiorUniforme
                timeMax, // limite_superiorUniforme
                timeInitTC, // Tiempo desde que inicia el trabajo C hasta que queda solo
                timeEndTC, // Tiempo antes de terminar el trabajo C en el que hay que retomarlo
                initTimeView, // Tiempo desde el que empieza a guardar filas del vector para devolver
                cantSimIterations // Cantidad de iteraciones que devuelve
        );
        return ResponseEntity.ok(values);
    }
}
