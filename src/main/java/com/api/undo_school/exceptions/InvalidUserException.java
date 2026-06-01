package com.api.undo_school.exceptions;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException(String s){
        super(s);
    }
}
