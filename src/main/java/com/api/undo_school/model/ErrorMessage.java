package com.api.undo_school.model;

import java.time.LocalDateTime;

public class ErrorMessage {

   private int httpCode;
    private long timestamp;
    private String message;

    public ErrorMessage(int httpCode,String message,long timestamp){
        this.httpCode=httpCode;
        this.timestamp=timestamp;
        this.message=message;

    }

    public int getHttpCode() {
        return httpCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
