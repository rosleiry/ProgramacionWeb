package org.example.exceptions;

public class noExistingUser extends RuntimeException{

    public noExistingUser(String message) {
        super(message);
    }

}
