package com.org.MiniAspire.repository;

import com.org.MiniAspire.exception.InstallmentAlreadyPaidException;
import com.org.MiniAspire.exception.InstallmentDoesNotExistException;
import com.org.MiniAspire.models.Installment;
import com.org.MiniAspire.models.InstallmentStatus;
import com.org.MiniAspire.models.Loan;
import com.org.MiniAspire.models.LoanStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@Getter
@Setter
public class LoanRepository {

    @Getter @Setter
    private static Map<Integer, Installment> installments = new HashMap<>();

    @Getter @Setter
    private static Map<Integer, Loan> loans = new HashMap<>();

    public void createLoan(String username, Double amount, int term) {
        loans.put(loans.size(), new Loan(loans.size(), username, amount, term, LoanStatus.PENDING_APPROVAL));
    }

    public void createInstallment(int loanId, Double installmentAmount, LocalDate dueDate) {
        installments.put(installments.size(),
                new Installment(installments.size(), loanId, installmentAmount, dueDate, InstallmentStatus.PENDING));
    }

    public List<Loan> getLoansPendingApproval() {
        return loans.values().stream()
                .filter(loan -> loan.getLoanStatus().equals(LoanStatus.PENDING_APPROVAL))
                .collect(Collectors.toList());
    }

    public List<Loan> getLoansOfUser(String username) {
        return loans.values().stream()
                .filter(loan -> loan.getUsername().equals(username))
                .collect(Collectors.toList());
    }

    public List<Installment> getPendingInstallmentsOfLoan(int loanId) {
        return installments.values().stream()
                .filter(installment -> installment.getLoanId() == loanId &&
                        installment.getStatus() == InstallmentStatus.PENDING)
                .collect(Collectors.toList());
    }

    public List<Installment> getInstallmentsOfUser(String username) {
        return installments.values().stream()
                .filter(installment -> loans.get(installment.getLoanId()).getUsername().equals(username))
                .collect(Collectors.toList());
    }

    public void payInstallment(int installmentId)
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        Installment installment = installments.get(installmentId);
        if (installment == null)
            throw new InstallmentDoesNotExistException();
        if (installment.getStatus().equals(InstallmentStatus.COMPLETE))
            throw new InstallmentAlreadyPaidException();
        installment.setStatus(InstallmentStatus.COMPLETE);
    }
}
