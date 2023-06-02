package com.org.MiniAspire.exception;

public class InstallmentDoesNotExistException extends Exception {
    public InstallmentDoesNotExistException() {
        super("The provided installment does not exist");
    }
}
