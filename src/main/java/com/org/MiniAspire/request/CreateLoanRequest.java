package com.org.MiniAspire.request;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class CreateLoanRequest {
    private Double amount;
    private Integer term;
}
