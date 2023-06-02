package com.org.MiniAspire.controller;

import com.org.MiniAspire.exception.InstallmentAlreadyPaidException;
import com.org.MiniAspire.exception.InstallmentDoesNotExistException;
import com.org.MiniAspire.exception.UserDoesNotExistException;
import com.org.MiniAspire.models.Installment;
import com.org.MiniAspire.models.UserType;
import com.org.MiniAspire.request.PayInstallmentRequest;
import com.org.MiniAspire.service.LoanService;
import com.org.MiniAspire.service.UserService;
import com.org.MiniAspire.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aspire/installment")
public class InstallmentController {
    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;
    @PutMapping
    public ResponseEntity<String> payInstallment(HttpServletRequest request,
                                                 @RequestBody PayInstallmentRequest payInstallmentRequest)
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException, UserDoesNotExistException {
        if (!userService.authenticateUser(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String username = request.getHeader(Constants.USERNAME_HEADER);
        if (userService.getUserType(username).equals(UserType.CUSTOMER)) {
            loanService.payInstallmentAndUpdateLoan(payInstallmentRequest);
            return ResponseEntity.ok("Installment processed");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Installment>> getInstallmentsOfUser(HttpServletRequest request)
            throws InstallmentDoesNotExistException, InstallmentAlreadyPaidException, UserDoesNotExistException {
        if (!userService.authenticateUser(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String username = request.getHeader(Constants.USERNAME_HEADER);
        return ResponseEntity.ok(loanService.getInstallmentsOfUser(username));
    }
}


