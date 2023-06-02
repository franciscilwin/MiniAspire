package com.org.MiniAspire.models;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Installment {

    private int installmentId;
    private int loanId;

    private Double amount;

    private LocalDate dueDate;

    private InstallmentStatus status;

    public Installment(int installmentId, int loanId, Double amount,
                       LocalDate dueDate, InstallmentStatus installmentStatus) {
        this.installmentId = installmentId;
        this.loanId = loanId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = installmentStatus;
    }
}
