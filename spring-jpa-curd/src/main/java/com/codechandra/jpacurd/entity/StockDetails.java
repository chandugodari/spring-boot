package com.codechandra.jpacurd.entity;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Generated;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "stock")
public class StockDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String stockName;

    private Double stockPrice;


}
