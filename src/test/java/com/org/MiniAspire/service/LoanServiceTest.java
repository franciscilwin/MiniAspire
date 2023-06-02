package com.org.MiniAspire.service;


import com.org.MiniAspire.exception.InstallmentAlreadyPaidException;
import com.org.MiniAspire.exception.InstallmentDoesNotExistException;
import com.org.MiniAspire.exception.LoanDoesNotExistException;
import com.org.MiniAspire.models.Installment;
import com.org.MiniAspire.models.InstallmentStatus;
import com.org.MiniAspire.models.Loan;
import com.org.MiniAspire.models.LoanStatus;
import com.org.MiniAspire.repository.LoanRepository;
import com.org.MiniAspire.request.CreateLoanRequest;
import com.org.MiniAspire.request.PayInstallmentRequest;
import com.org.MiniAspire.request.ProcessLoanRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = UserService.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoanServiceTest {
    @InjectMocks
    private LoanService loanService;
    @Mock
    private LoanRepository loanRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void requestLoan_newLoanParametersPassed_LoanRepositoryCalledWithCorrectArguments() {
        CreateLoanRequest mockRequest = new CreateLoanRequest(100.0, 3);
        loanService.requestLoan(mockRequest, "abc");
        verify(loanRepository).createLoan("abc", mockRequest.getAmount(), mockRequest.getTerm());
    }


    @Test
    public void getLoansPendingApproval_gettingAllLoansPendingApproval_LoanRepositoryCalledWithCorrectArguments() {
        CreateLoanRequest mockRequest = new CreateLoanRequest(100.0, 3);
        loanService.getLoansPendingApproval();
        verify(loanRepository).getLoansPendingApproval();
    }



    @Test
    public void getLoansOfUser_givingExistingUsername_LoanRepositoryCalledWithCorrectArguments() {
        CreateLoanRequest mockRequest = new CreateLoanRequest(100.0, 3);
        loanService.getLoansOfUser("abc");
        verify(loanRepository).getLoansOfUser("abc");
    }



    @Test
    public void processLoan_loanPresent_updatesStatusAndCallsMethod() {
        try (MockedStatic<LoanRepository> mockedStatic = mockStatic(LoanRepository.class)) {
            Map<Integer, Loan> mockLoans = new TreeMap<>();
            Loan mockLoan = new Loan(0,"abc", 100.0, 3, LoanStatus.PENDING_APPROVAL);
            mockLoans.put(0, mockLoan);
            ProcessLoanRequest mockRequest = new ProcessLoanRequest(0, LoanStatus.APPROVED);
            LoanService mockLoanService = mock(LoanService.class);
            doCallRealMethod().when(mockLoanService).processLoan(mockRequest);
            when(loanRepository.getLoans()).thenReturn(mockLoans);
            mockLoanService.processLoan(mockRequest);
            Assert.assertEquals(new Loan(0,"abc", 100.0, 3, LoanStatus.APPROVED),
                    mockLoans.get(0));
            verify(mockLoanService).createAndPersistRepaymentSchedule(mockLoan);
        } catch (LoanDoesNotExistException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void processLoan_loanNotPresent_throwsException() {
        try (MockedStatic<LoanRepository> mockedStatic = mockStatic(LoanRepository.class)) {
            Map<Integer, Loan> mockLoans = new TreeMap<>();
            Loan mockLoan = new Loan(0, "abc", 100.0, 3, LoanStatus.PENDING_APPROVAL);
            mockLoans.put(0, mockLoan);
            when(loanRepository.getLoans()).thenReturn(mockLoans);
            ProcessLoanRequest mockRequest = new ProcessLoanRequest(5, LoanStatus.APPROVED);
            Assert.assertThrows(LoanDoesNotExistException.class, () -> {
                loanService.processLoan(mockRequest);
            });
        }
    }



    @Test
    public void createAndPersistRepaymentSchedule__throwsException() {
        Loan mockLoan = new Loan(0, "abc", 100.0, 3, LoanStatus.PENDING_APPROVAL);
        loanService.createAndPersistRepaymentSchedule(mockLoan);
        verify(loanRepository).
                createInstallment(0,33.33, LocalDate.now());
        verify(loanRepository).
                createInstallment(0,33.33, LocalDate.now().plusDays(7));
        verify(loanRepository).
                createInstallment(0,33.34, LocalDate.now().plusDays(14));
    }

    @Test
    public void payInstallmentAndUpdateLoan_installmentPending_callsLoanRepositoryWithCorrectArguments()
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        List<Installment> mockInstallments = new ArrayList<>();
        Installment mockInstallment1 = new Installment(0,0,100.0,LocalDate.now(),InstallmentStatus.PENDING);
        mockInstallments.add(mockInstallment1);
        when(loanRepository.getPendingInstallmentsOfLoan(0)).thenReturn(mockInstallments);
        PayInstallmentRequest mockRequest = new PayInstallmentRequest(0,0);
        loanService.payInstallmentAndUpdateLoan(mockRequest);
        verify(loanRepository).payInstallment(0);
    }


    @Test
    public void payInstallmentAndUpdateLoan_installmentNotPending_updatesStatus()
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        try (MockedStatic<LoanRepository> mockedStatic = mockStatic(LoanRepository.class)) {
            when(loanRepository.getPendingInstallmentsOfLoan(0)).thenReturn(new ArrayList<>());
            Map<Integer, Loan> mockLoans = new TreeMap<>();
            Loan mockLoan = new Loan(0, "abc", 100.0, 3, LoanStatus.PENDING_APPROVAL);
            mockLoans.put(0, mockLoan);
            when(loanRepository.getLoans()).thenReturn(mockLoans);
            PayInstallmentRequest mockRequest = new PayInstallmentRequest(0, 0);
            loanService.payInstallmentAndUpdateLoan(mockRequest);
            Assert.assertEquals(mockLoans.get(0).getLoanStatus(), LoanStatus.COMPLETED_PAYMENT);
        }
    }
    @Test
    public void getInstallmentsOfUser_usernameGiven_callsLoanRepositoryWithCorrectArguments() {
        loanService.getInstallmentsOfUser("abc");
        verify(loanRepository).getInstallmentsOfUser("abc");
    }


}
