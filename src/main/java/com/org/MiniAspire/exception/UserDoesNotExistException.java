package com.org.MiniAspire.exception;

public class UserDoesNotExistException extends Exception{
    public UserDoesNotExistException() {
        super("The username provided does not exist");
    }
}
