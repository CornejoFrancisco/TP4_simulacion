package com.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimulacionRequest {
    private double probTA;
    private double probTB;
    private double probTC;
    private double probTD;
    private double timeTA;
    private double timeTB;
    private double timeTC;
    private double timeTD;
    private double timeMin;
    private double timeMax;
    private double timeInitTC;
    private double timeEndTC;
    private double cantTimeSim;
    private double initTimeView;
    private int cantSimIterations;
}
