package com.org.MiniAspire.service;

import com.org.MiniAspire.exception.InstallmentAlreadyPaidException;
import com.org.MiniAspire.exception.InstallmentDoesNotExistException;
import com.org.MiniAspire.exception.LoanDoesNotExistException;
import com.org.MiniAspire.models.Installment;
import com.org.MiniAspire.models.Loan;
import com.org.MiniAspire.models.LoanStatus;
import com.org.MiniAspire.repository.LoanRepository;
import com.org.MiniAspire.request.CreateLoanRequest;
import com.org.MiniAspire.request.PayInstallmentRequest;
import com.org.MiniAspire.request.ProcessLoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;


@Component
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;

    private static final int TERM_DURATION = 7;

    private static final String CURRENCY_FORMAT = "#.##";

    public void requestLoan(CreateLoanRequest request, String username) {
        loanRepository.createLoan(username, request.getAmount(), request.getTerm());
    }

    public List<Loan> getLoansPendingApproval() {
        return loanRepository.getLoansPendingApproval();
    }

    public List<Loan> getLoansOfUser(String username) {
        return loanRepository.getLoansOfUser(username);
    }

    public void processLoan(ProcessLoanRequest request) throws LoanDoesNotExistException {
        if (!loanRepository.getLoans().containsKey(request.getLoanId()))
            throw new LoanDoesNotExistException();
        loanRepository.getLoans().get(request.getLoanId()).setLoanStatus(request.getLoanResponse());
        //create repayment schedule
        createAndPersistRepaymentSchedule(loanRepository.getLoans().get(request.getLoanId()));
    }
    public void createAndPersistRepaymentSchedule(Loan loan) {
        DecimalFormat df = new DecimalFormat(CURRENCY_FORMAT);
        double baseInstallmentAmount = Double.parseDouble(df.format(loan.getAmount() / loan.getTerm()));

        for (int i = 0; i < loan.getTerm() - 1; i++)
            loanRepository.createInstallment(loan.getId(),
                    Double.parseDouble(df.format(baseInstallmentAmount)),
                    LocalDate.now().plusDays(i*TERM_DURATION));

        double remainingAmount = loan.getAmount() - (loan.getTerm() - 1) * baseInstallmentAmount;

        loanRepository.createInstallment(loan.getId(),
                Double.parseDouble(df.format(remainingAmount)),
                LocalDate.now().plusDays((loan.getTerm() - 1)*TERM_DURATION));
    }

    public void payInstallmentAndUpdateLoan(PayInstallmentRequest payInstallmentRequest)
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        loanRepository.payInstallment(payInstallmentRequest.getInstallmentId());
        List<Installment> loanInstallments = loanRepository.getPendingInstallmentsOfLoan(payInstallmentRequest.getLoanId());
        if (loanInstallments.isEmpty()) {
            loanRepository.getLoans().get(payInstallmentRequest.
                    getLoanId()).setLoanStatus(LoanStatus.COMPLETED_PAYMENT);
        }
    }

    public List<Installment> getInstallmentsOfUser(String username) {
        return loanRepository.getInstallmentsOfUser(username);
    }
}
