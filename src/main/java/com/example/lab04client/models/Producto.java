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


    public Date getCreateAt() {
        //return this.createAt;
        return this.createAt != null ? (Date) createAt.clone() : null;
    }

    public void setCreateAt(Date createAt) {
        //this.createAt=createAt;
        this.createAt = createAt != null ? (Date) createAt.clone() : null;
    }


}
