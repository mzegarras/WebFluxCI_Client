package com.example.lab04client.exceptions;

import org.apache.logging.log4j.util.Strings;

public class ProductNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6922159999398000261L;

    private String id = Strings.EMPTY;

    public ProductNotFoundException(String id) {
        super("Product not found:" + id);
        this.id=id;
    }

    public String getId() {
        return id;
    }

}
