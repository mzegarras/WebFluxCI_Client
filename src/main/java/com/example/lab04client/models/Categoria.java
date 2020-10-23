package com.example.lab04client.models;

import lombok.Data;


import java.io.Serializable;

@Data
public class Categoria  implements Serializable {

    private static final long serialVersionUID = 701759972594570968L;

    private String id;
    private String nombre;
}
