package com.api.undo_school.exceptions;

public class InvalidCourseIdException extends RuntimeException{
    public InvalidCourseIdException(String m){
      super(m);
    }
}
