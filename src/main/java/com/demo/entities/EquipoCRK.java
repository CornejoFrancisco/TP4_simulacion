package com.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoCRK {
    private Integer idEquipo;
    private Double rnd;
    private Double valorC;
    private Double valorN;
    private Double valorNEnHoras;
}
