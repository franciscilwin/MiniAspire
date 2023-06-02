package com.org.MiniAspire.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PayInstallmentRequest {
    private int installmentId;

    private int loanId;
}
