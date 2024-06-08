package com.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_request {

    public double probTablaA;
    public double probTablaB;
    public double probTablaC;
    public double probTablaD;
    public Integer timeTA;
    public Integer timeTB;
    public Integer timeTC;
    public Integer timeTD;
    public Integer timeMin;
    public Integer timeMax;
    public Integer timeInitTC;
    public Integer timeEndTC;
    public Integer cantTimeSim;
    public Integer initTimeView;
    public Integer cantSimIterations;

}
