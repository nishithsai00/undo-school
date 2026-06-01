package com.api.undo_school.exceptions;

public class InvalidOfferingIdException extends RuntimeException{
    public InvalidOfferingIdException (String message){
        super(message);
    }
}
