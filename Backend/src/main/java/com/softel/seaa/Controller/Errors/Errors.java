package com.softel.seaa.Controller.Errors;

import lombok.Builder;
import lombok.Data;
import java.util.HashMap;

@Data
@Builder
public class Errors {
    private String code;
    private String type;
    private String message;
    private String value;
    private final String ERROR = "ERROR";
    private HashMap<String, String> errors;
}
