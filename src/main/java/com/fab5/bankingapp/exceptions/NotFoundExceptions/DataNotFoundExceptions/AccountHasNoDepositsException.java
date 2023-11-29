package com.fab5.bankingapp.exceptions.NotFoundExceptions.DataNotFoundExceptions;

import com.fab5.bankingapp.exceptions.NotFoundExceptions.DataNotFoundException;
import com.fab5.bankingapp.exceptions.NotFoundExceptions.NoSuchElementFoundException;

public class AccountHasNoDepositsException extends DataNotFoundException {
    public AccountHasNoDepositsException(Long accountID) {
        super("Account with ID " + accountID + " has no deposits.");
        throwLogError(accountID);
    }
}
