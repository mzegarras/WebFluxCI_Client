package com.example.lab04client.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Producto implements Serializable {

    private static final long serialVersionUID = -2996544527670058181L;

    private String id;
    private String nombre;
    private Double precio;
    private Date createAt;
    private Categoria categoria;




}
