package com.org.MiniAspire.controller;

import com.org.MiniAspire.exception.InstallmentAlreadyPaidException;
import com.org.MiniAspire.exception.InstallmentDoesNotExistException;
import com.org.MiniAspire.exception.UserDoesNotExistException;
import com.org.MiniAspire.models.UserType;
import com.org.MiniAspire.request.PayInstallmentRequest;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = UserController.class)
public class InstallmentControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private InstallmentController installmentController;

    @Test
    public void payInstallment_invalidCredentials_correctResponseCodeReturned()
            throws UserDoesNotExistException, InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        PayInstallmentRequest payInstallmentRequest = new PayInstallmentRequest(0,0);
        Assert.assertEquals(installmentController.payInstallment(
                request, payInstallmentRequest).getStatusCode(), HttpStatusCode.valueOf(401));
    }


    @Test
    public void payInstallment_userIsCustomer_correctResponseCodeReturned()
            throws UserDoesNotExistException, InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        when(userService.authenticateUser(request)).thenReturn(true);
        when(userService.getUserType("abc")).thenReturn(UserType.CUSTOMER);
        PayInstallmentRequest payInstallmentRequest = new PayInstallmentRequest(0,0);
        Assert.assertEquals(installmentController.payInstallment(
                request, payInstallmentRequest).getStatusCode(), HttpStatusCode.valueOf(200));
    }

    @Test
    public void payInstallment_userIsAdmin_correctResponseCodeReturned()
            throws UserDoesNotExistException, InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        when(userService.authenticateUser(request)).thenReturn(true);
        when(userService.getUserType("abc")).thenReturn(UserType.ADMIN);
        PayInstallmentRequest payInstallmentRequest = new PayInstallmentRequest(0,0);
        Assert.assertEquals(installmentController.payInstallment(
                request, payInstallmentRequest).getStatusCode(), HttpStatusCode.valueOf(401));
    }



    @Test
    public void getInstallmentsOfUser_invalidCredentials_correctResponseCodeReturned()
            throws UserDoesNotExistException, InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        Assert.assertEquals(installmentController.getInstallmentsOfUser(request).getStatusCode(),
                HttpStatusCode.valueOf(401));
    }


    @Test
    public void getInstallmentsOfUser_validCredentials_correctResponseCodeReturned()
            throws UserDoesNotExistException, InstallmentDoesNotExistException, InstallmentAlreadyPaidException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.USERNAME_HEADER, "abc");
        request.addHeader(Constants.PASSWORD_HEADER, "123");
        when(userService.authenticateUser(request)).thenReturn(true);
        Assert.assertEquals(installmentController.getInstallmentsOfUser(request).getStatusCode(),
                HttpStatusCode.valueOf(200));
    }

}
