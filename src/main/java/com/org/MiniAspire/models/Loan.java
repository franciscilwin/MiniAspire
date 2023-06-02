package com.org.MiniAspire.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Loan {

    private int id;
    private String username;
    private Double amount;
    private Integer term;
    private LoanStatus loanStatus;

    public Loan(int id, String username, Double amount, Integer term, LoanStatus loanStatus) {
        this.id = id;
        this.amount = amount;
        this.term = term;
        this.username = username;
        this.loanStatus = loanStatus;
    }
}
