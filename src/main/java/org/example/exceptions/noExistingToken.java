package org.example.exceptions;

public class noExistingToken extends RuntimeException{

    public noExistingToken(String message) {
        super(message);
    }

}
