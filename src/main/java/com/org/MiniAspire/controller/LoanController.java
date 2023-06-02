package com.org.MiniAspire.controller;

import com.org.MiniAspire.exception.LoanDoesNotExistException;
import com.org.MiniAspire.exception.UserDoesNotExistException;
import com.org.MiniAspire.models.Loan;
import com.org.MiniAspire.models.UserType;
import com.org.MiniAspire.request.CreateLoanRequest;
import com.org.MiniAspire.request.ProcessLoanRequest;
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
@RequestMapping("/api/aspire/loan")
public class LoanController {
    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;
    @PostMapping
    public ResponseEntity<String> createLoan(HttpServletRequest request,
                                             @RequestBody CreateLoanRequest createLoanRequest)
            throws UserDoesNotExistException {
        if (!userService.authenticateUser(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String username = request.getHeader(Constants.USERNAME_HEADER);
        if (userService.getUserType(username).equals(UserType.CUSTOMER)) {
            loanService.requestLoan(createLoanRequest, username);
            return ResponseEntity.ok("Loan Sent for Approval");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    public ResponseEntity<String> processLoan(HttpServletRequest request,
                                              @RequestBody ProcessLoanRequest processLoanRequest)
            throws LoanDoesNotExistException, UserDoesNotExistException {
        if (!userService.authenticateUser(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String username = request.getHeader(Constants.USERNAME_HEADER);
        if (userService.getUserType(username).equals(UserType.ADMIN)) {
            loanService.processLoan(processLoanRequest);
            return ResponseEntity.ok("Loan processed");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<Loan>> getLoans(HttpServletRequest request) throws UserDoesNotExistException {
        if (!userService.authenticateUser(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String username = request.getHeader(Constants.USERNAME_HEADER);
        if (userService.getUserType(username).equals(UserType.ADMIN))
            return ResponseEntity.ok(loanService.getLoansPendingApproval());
        else
            return ResponseEntity.ok(loanService.getLoansOfUser(username));
    }
}


