package com.softel.seaa;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.IntStream;

public class prueba {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(new BCryptPasswordEncoder().encode("Admin#*01"));
    }
}
