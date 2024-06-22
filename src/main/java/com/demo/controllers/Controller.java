package com.demo.controllers;

import com.demo.entities.*;
import com.demo.services.SimulacionPractica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
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
    public ResponseEntity<ResultadosSimulacion> simular(@RequestBody(required = false) Dto_request simulacionRequest) {

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
        double limInfUnifTC = simulacionRequest.getLimInfUnifTC();
        double limSupUnifTC = simulacionRequest.getLimSupUnifTC();
        double nSuma = simulacionRequest.getNSuma();
        double nExpo = simulacionRequest.getNExpo();
        double cantTimeSim = simulacionRequest.getCantTimeSim();
        double initTimeView = simulacionRequest.getInitTimeView();
        int cantSimIterations = simulacionRequest.getCantSimIterations();

        ArrayList<Double> probabilidadesOcurrencia = new ArrayList<>(Arrays.asList(probTA, probTB, probTC, probTD));


        ArrayList<Double> tiemposDemora = new ArrayList<>(Arrays.asList(timeTA, timeTB, timeTC, timeTD));

        ResultadosSimulacion values = simulacionPractica.cola(
                cantTimeSim,
                probabilidadesOcurrencia,
                tiemposDemora,
                timeMin,
                timeMax,
                limInfUnifTC,
                limSupUnifTC,
                nSuma,
                nExpo,
                initTimeView,
                cantSimIterations
        );
        return ResponseEntity.ok(values);
    }

    @GetMapping("/datos")
    public ResponseEntity<FilasPaginadas> getDatos(
            @RequestParam int page
    ) {
        System.out.println("page: " + page);
        FilasPaginadas filasPaginadas = simulacionPractica.getFilasPaginadas(page);
        return ResponseEntity.ok(filasPaginadas);
    }

}
