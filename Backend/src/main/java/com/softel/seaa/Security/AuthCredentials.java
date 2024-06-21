package com.softel.seaa.Security;

import lombok.Data;

import java.util.List;

@Data
public class AuthCredentials {
    private String phoneNumber;
    private String password;
    private List<String> authorities;
}
