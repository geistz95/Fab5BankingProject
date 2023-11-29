package com.fab5.bankingapp.handler;

import com.fab5.bankingapp.errors.ErrorDetail;
import com.fab5.bankingapp.errors.ValidationError;
import com.fab5.bankingapp.exceptions.InsufficientFundsException;
import com.fab5.bankingapp.exceptions.InvalidDepositAmount;
import com.fab5.bankingapp.exceptions.NotFoundExceptions.DataNotFoundException;
import com.fab5.bankingapp.exceptions.NotFoundExceptions.NoSuchElementFoundException;
import com.fab5.bankingapp.utility.ExceptionTypeExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(NoSuchElementFoundException.class)
    public ResponseEntity<?> handleNoSuchElementFoundException(NoSuchElementFoundException
                                                                       ex, WebRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.NOT_FOUND.value());
        errorDetail.setTitle(ex.getSimplifiedNameOfExceptionOfNotFound2() + " Not Found");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());
        errorDetail.setPath(request.getDescription(false));
        return new ResponseEntity<>(errorDetail, null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?> handleDataNotFoundException(DataNotFoundException
                                                                       ex, WebRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setStatus(HttpStatus.NOT_FOUND.value());
        errorDetail.setTitle(ex.extractExceptionType());
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());
        errorDetail.setPath(request.getDescription(false));
        return new ResponseEntity<>(errorDetail, null, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setStatus(status.value());
        errorDetail.setTitle("Message Not Readable");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());
        errorDetail.setPath(request.getDescription(false));
        return handleExceptionInternal(ex, errorDetail, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTitle("Validation Failed");
        errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());
//        String requestPath = (String) request.getAttribute("javax.servlet.error.request_uri");
//        if(requestPath == null) {
//            requestPath = request.getRequestURI();
//        } what is this doing here? need an explanation
        errorDetail.setDetail("Input validation failed");
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setDeveloperMessage(ex.getClass().getName());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors(); //retrieve information from suchException to use for our body (field errors include things such as notEmpty and size (2,6)
        for (FieldError fe : fieldErrors) {
            List<ValidationError> validationErrorsList = errorDetail.getErrors().get(fe.getField()); //check if this error is within our collection
            if (validationErrorsList == null) { //if not there, create a new key value for it in the collection. String -> List<ValidationError)
                validationErrorsList = new ArrayList<>();
                errorDetail.getErrors().put(fe.getField(), validationErrorsList);
            }
            ValidationError validationError = new ValidationError();
            validationError.setCode(fe.getCode()); //"code": "NotEmpty",
            validationError.setMessage(fe.getDefaultMessage()); //"message": "must not be empty"  *build the validation error from the current fieldError*
            validationErrorsList.add(validationError); //add it to the arrayList, which is stored in the hashmap errorDetail.errors
        }
        return handleExceptionInternal(ex, errorDetail, headers, status, request); //what is handleExceptionInternal? Seems like a method used to return a body for any exception handling?
    }

    @ExceptionHandler({InvalidDepositAmount.class, InsufficientFundsException.class})
    public ResponseEntity<Object> handleDepositExceptions(RuntimeException ex, WebRequest request) {
        ErrorDetail errorDetail = generateBasicErrorDetailEndingInException(ex, HttpStatus.BAD_REQUEST, request);
        return new ResponseEntity<>(errorDetail, null, HttpStatus.BAD_REQUEST);
    }

    private ErrorDetail generateBasicErrorDetailEndingInException(RuntimeException ex, HttpStatus status, WebRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimeStamp(new Date().getTime());
        errorDetail.setStatus(status.value());
        errorDetail.setTitle(((ExceptionTypeExtractor)ex).extractExceptionType());
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());
        errorDetail.setPath(request.getDescription(false));
        return errorDetail;
    }
}
