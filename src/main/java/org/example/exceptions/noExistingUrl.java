package org.example.exceptions;

public class noExistingUrl extends RuntimeException{


    public noExistingUrl(String s) {
        super(s);
    }
}
