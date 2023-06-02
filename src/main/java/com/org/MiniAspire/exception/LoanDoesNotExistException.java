package com.org.MiniAspire.exception;

public class LoanDoesNotExistException extends Exception{
    public LoanDoesNotExistException() {
        super("The given loan ID does not exist.");
    }
}
