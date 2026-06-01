package com.api.undo_school.exceptions;

import com.api.undo_school.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ErrorMessage> bookingEx(BookingException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.CONFLICT.value(), e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmptyEmailException.class)
    public ResponseEntity<ErrorMessage> emailEx(EmptyEmailException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.NO_CONTENT.value(), e.getMessage(),System.currentTimeMillis());
        return  new ResponseEntity<>(error,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(EmptyUsernameException.class)
    public ResponseEntity<ErrorMessage> usernameEx(EmptyUsernameException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.NO_CONTENT.value(), e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error,HttpStatus.NO_CONTENT);
    }
    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<ErrorMessage> codeEx(InvalidCodeException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidOfferingIdException.class)
    public ResponseEntity<ErrorMessage> offeringEx(InvalidOfferingIdException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTimezoneException.class)
    public ResponseEntity<ErrorMessage> timezoneEx(InvalidTimezoneException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorMessage> invalidUsernameEx(InvalidUserException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SessionAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> sessionEx(SessionAlreadyExistsException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.CONFLICT.value(), e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error,HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InvalidCourseIdException.class)
    public ResponseEntity<ErrorMessage> courseEx(InvalidCourseIdException e){
        ErrorMessage error =new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

}
