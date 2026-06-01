package com.api.undo_school.exceptions;

public class SessionAlreadyExistsException extends  RuntimeException{
    public SessionAlreadyExistsException(String message){
        super(message);
    }
}
