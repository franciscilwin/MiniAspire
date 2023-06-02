package com.org.MiniAspire.exception;

public class InstallmentAlreadyPaidException extends Exception {
    public InstallmentAlreadyPaidException() {
        super("The installment is already paid");
    }
}
