package com.sportscenter.nutritionist.exceptions;

public class AppointmentOverlapException extends RuntimeException  {
    public AppointmentOverlapException(String message) {
        super(message);
    }
}
