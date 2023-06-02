package com.org.MiniAspire.request;

import com.org.MiniAspire.models.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class ProcessLoanRequest {
    private int loanId;

    private LoanStatus loanResponse;
}
