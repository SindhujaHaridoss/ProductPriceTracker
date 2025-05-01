package com.nhsbsa.productpricealerttracker.model;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String errorCode;
    private Map<String, String> errors;
}
