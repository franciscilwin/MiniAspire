package com.org.MiniAspire.repository;


import com.org.MiniAspire.exception.InstallmentAlreadyPaidException;
import com.org.MiniAspire.exception.InstallmentDoesNotExistException;
import com.org.MiniAspire.models.Installment;
import com.org.MiniAspire.models.InstallmentStatus;
import com.org.MiniAspire.models.Loan;
import com.org.MiniAspire.models.LoanStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = LoanRepository.class)
public class LoanRepositoryTest {
    @Autowired
    private LoanRepository loanRepository;

    @Before
    public void setUp() {
        loanRepository.setLoans(new TreeMap<Integer, Loan>());
        loanRepository.setInstallments(new TreeMap<Integer, Installment>());
    }


    @Test
    public void createLoan_newLoanCreated_successfullyPersists() {
        List<Loan> expectedLoans = new ArrayList<>();
        expectedLoans.add(new Loan(0,"abc",100.0,3, LoanStatus.PENDING_APPROVAL));
        loanRepository.createLoan("abc", 100.0,3);
        Assert.assertEquals(expectedLoans, loanRepository.getLoans().values().stream().toList());
    }

    @Test
    public void createInstallment_newInstallmentCreated_successfullyPersists(){
        List<Installment> expectedInstallments = new ArrayList<>();
        expectedInstallments.add(new Installment(0, 0, 100.0, LocalDate.now(), InstallmentStatus.PENDING));
        loanRepository.createInstallment(0,100.0,LocalDate.now());
        Assert.assertEquals(expectedInstallments, loanRepository.getInstallments().values().stream().toList());
    }

    @Test
    public void getInstallmentsOfUser_installmentsPresent_returnsCorrectValue(){
        Map<Integer, Installment> mockInstallments = new TreeMap<>();
        Map<Integer, Loan> mockLoans = new TreeMap<>();
        mockInstallments.put(0, new Installment(0, 0, 100.0, LocalDate.now(), InstallmentStatus.PENDING));
        mockLoans.put(0, new Loan(0,"abc", 100.0, 3, LoanStatus.PENDING_APPROVAL));
        loanRepository.setLoans(mockLoans);
        loanRepository.setInstallments(mockInstallments);
        List<Installment> expectedInstallments = new ArrayList<>();
        expectedInstallments.add(new Installment(0, 0, 100.0, LocalDate.now(), InstallmentStatus.PENDING));
        Assert.assertEquals(loanRepository.getInstallmentsOfUser("abc"),
                expectedInstallments);
    }

    @Test
    public void getLoansPendingApproval_loansFiltered_filtersCorrectly() {
        Map<Integer, Loan> mockLoans = new TreeMap<>();
        mockLoans.put(0, new Loan(0,"abc",100.0,3, LoanStatus.PENDING_APPROVAL));
        mockLoans.put(1, new Loan(1,"abc",100.0,3, LoanStatus.COMPLETED_PAYMENT));
        loanRepository.setLoans(mockLoans);
        List<Loan> expectedLoans = new ArrayList<>();
        expectedLoans.add(new Loan(0, "abc", 100.0, 3, LoanStatus.PENDING_APPROVAL));
        Assert.assertEquals(loanRepository.getLoansPendingApproval(), expectedLoans);
    }


    @Test
    public void getLoansOfUser_loansFiltered_filtersCorrectly() {
        Map<Integer, Loan> mockLoans = new TreeMap<>();
        mockLoans.put(0, new Loan(0,"abc",100.0,3, LoanStatus.PENDING_APPROVAL));
        mockLoans.put(1, new Loan(1,"def",100.0,3, LoanStatus.COMPLETED_PAYMENT));
        loanRepository.setLoans(mockLoans);
        List<Loan> expectedLoans = new ArrayList<>();
        expectedLoans.add(new Loan(0, "abc", 100.0, 3, LoanStatus.PENDING_APPROVAL));
        Assert.assertEquals(loanRepository.getLoansOfUser("abc"), expectedLoans);
    }




    @Test
    public void getPendingInstallmentsOfLoan_installmentsMade_filtersCorrectly() {
        Map<Integer, Installment> mockInstallments = new TreeMap<>();
        mockInstallments.put(0,
                new Installment(0,0,100.0,LocalDate.now(), InstallmentStatus.COMPLETE));
        mockInstallments.put(1,
                new Installment(1,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        mockInstallments.put(2,
                new Installment(2,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        loanRepository.setInstallments(mockInstallments);
        List<Installment> expectedInstallments = new ArrayList<>();
        expectedInstallments.add(new Installment(1,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        expectedInstallments.add(new Installment(2,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        Assert.assertEquals(loanRepository.getPendingInstallmentsOfLoan(0), expectedInstallments);
    }

    @Test
    public void payInstallment_installmentPaid_statusUpdated()
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        Map<Integer, Installment> mockInstallments = new TreeMap<>();
        mockInstallments.put(0,
                new Installment(0,0,100.0,LocalDate.now(), InstallmentStatus.COMPLETE));
        mockInstallments.put(1,
                new Installment(1,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        mockInstallments.put(2,
                new Installment(2,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        loanRepository.setInstallments(mockInstallments);
        loanRepository.payInstallment(1);
        Assert.assertEquals(loanRepository.getInstallments().get(1).getStatus(), InstallmentStatus.COMPLETE);
    }



    @Test
    public void payInstallment_nonexistentInstallment_exceptionThrown()
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        Map<Integer, Installment> mockInstallments = new TreeMap<>();
        mockInstallments.put(0,
                new Installment(0,0,100.0,LocalDate.now(), InstallmentStatus.COMPLETE));
        mockInstallments.put(1,
                new Installment(1,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        mockInstallments.put(2,
                new Installment(2,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        loanRepository.setInstallments(mockInstallments);
        Assert.assertThrows(InstallmentDoesNotExistException.class, () -> {
            loanRepository.payInstallment(5);
        });
    }

    @Test
    public void payInstallment_paidInstallment_exceptionThrown()
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        Map<Integer, Installment> mockInstallments = new TreeMap<>();
        mockInstallments.put(0,
                new Installment(0,0,100.0,LocalDate.now(), InstallmentStatus.COMPLETE));
        mockInstallments.put(1,
                new Installment(1,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        mockInstallments.put(2,
                new Installment(2,0,100.0,LocalDate.now(), InstallmentStatus.PENDING));
        loanRepository.setInstallments(mockInstallments);
        Assert.assertThrows(InstallmentAlreadyPaidException.class, () -> {
            loanRepository.payInstallment(0);
        });
    }

}
