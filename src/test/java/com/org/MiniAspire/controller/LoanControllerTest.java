package com.org.MiniAspire.controller;

import com.org.MiniAspire.exception.LoanDoesNotExistException;
import com.org.MiniAspire.exception.UserDoesNotExistException;
import com.org.MiniAspire.models.Loan;
import com.org.MiniAspire.models.LoanStatus;
import com.org.MiniAspire.models.UserType;
import com.org.MiniAspire.request.CreateLoanRequest;
import com.org.MiniAspire.request.ProcessLoanRequest;
import com.org.MiniAspire.service.LoanService;
import com.org.MiniAspire.service.UserService;
import com.org.MiniAspire.util.Constants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = UserController.class)
public class LoanControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    @Test
    public void createLoan_invalidCredentials_correctResponseCodeReturned() throws UserDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        CreateLoanRequest createLoanRequest = new CreateLoanRequest(100.0, 3);
        Assert.assertEquals(loanController.createLoan(
                request, createLoanRequest).getStatusCode(), HttpStatusCode.valueOf(401));
    }


    @Test
    public void createLoan_userIsCustomer_correctResponseCodeReturned() throws UserDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        CreateLoanRequest createLoanRequest = new CreateLoanRequest(100.0, 3);
        when(userService.authenticateUser(request)).thenReturn(true);
        when(userService.getUserType("abc")).thenReturn(UserType.CUSTOMER);
        ResponseEntity<String> response = loanController.createLoan(
                request, createLoanRequest);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
    }




    @Test
    public void createLoan_userIsAdmin_correctResponseCodeReturned() throws UserDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        CreateLoanRequest createLoanRequest = new CreateLoanRequest(100.0, 3);
        when(userService.authenticateUser(request)).thenReturn(true);
        when(userService.getUserType("abc")).thenReturn(UserType.ADMIN);
        ResponseEntity<String> response = loanController.createLoan(
                request, createLoanRequest);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(401));
    }

    @Test
    public void processLoan_invalidCredentials_correctResponseCodeReturned()
            throws LoanDoesNotExistException, UserDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        ProcessLoanRequest processLoanRequest = new ProcessLoanRequest(0, LoanStatus.APPROVED);
        Assert.assertEquals(loanController.processLoan(
                request, processLoanRequest).getStatusCode(), HttpStatusCode.valueOf(401));
    }

    @Test
    public void processLoan_userIsCustomer_correctResponseCodeReturned()
            throws UserDoesNotExistException, LoanDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        ProcessLoanRequest processLoanRequest = new ProcessLoanRequest(0, LoanStatus.APPROVED);
        when(userService.authenticateUser(request)).thenReturn(true);
        when(userService.getUserType("abc")).thenReturn(UserType.CUSTOMER);
        ResponseEntity<String> response = loanController.processLoan(
                request, processLoanRequest);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(401));
    }

    @Test
    public void processLoan_userIsAdmin_correctResponseCodeReturned()
            throws UserDoesNotExistException, LoanDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        ProcessLoanRequest processLoanRequest = new ProcessLoanRequest(0, LoanStatus.APPROVED);
        when(userService.authenticateUser(request)).thenReturn(true);
        when(userService.getUserType("abc")).thenReturn(UserType.ADMIN);
        ResponseEntity<String> response = loanController.processLoan(
                request, processLoanRequest);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
    }



    @Test
    public void getLoans_nonExistentUser_correctResponseCodeReturned()
            throws UserDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        ResponseEntity<List<Loan>> response = loanController.getLoans(request);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(401));
    }

    @Test
    public void getLoans_userIsAdmin_correctContentInResponseBody()
            throws UserDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        when(userService.authenticateUser(request)).thenReturn(true);
        when(userService.getUserType("abc")).thenReturn(UserType.ADMIN);
        List<Loan> mockLoans = new ArrayList<>();
        mockLoans.add(new Loan(0,"abc",100.0,3,LoanStatus.PENDING_APPROVAL));
        mockLoans.add(new Loan(1,"abc",100.0,3,LoanStatus.PENDING_APPROVAL));
        when(loanService.getLoansPendingApproval()).thenReturn(mockLoans);
        ResponseEntity<List<Loan>> response = loanController.getLoans(request);
        Assert.assertEquals(response.getBody(), mockLoans);
    }



    @Test
    public void getLoans_userIsCustomer_correctContentInResponseBody()
            throws UserDoesNotExistException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        when(userService.authenticateUser(request)).thenReturn(true);
        when(userService.getUserType("abc")).thenReturn(UserType.CUSTOMER);
        List<Loan> mockLoans = new ArrayList<>();
        mockLoans.add(new Loan(0,"abc",100.0,3,LoanStatus.PENDING_APPROVAL));
        mockLoans.add(new Loan(1,"abc",100.0,3,LoanStatus.PENDING_APPROVAL));
        when(loanService.getLoansOfUser("abc")).thenReturn(mockLoans);
        ResponseEntity<List<Loan>> response = loanController.getLoans(request);
        Assert.assertEquals(response.getBody(), mockLoans);
    }
}
