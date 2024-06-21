package com.softel.seaa.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Token {

    private static final String ACCESS_SECRET_KEY;

    static {
        try {
            ACCESS_SECRET_KEY = generateSecretKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateToken(String name, long id, List<String> authorities) throws NoSuchAlgorithmException {
        HashMap<String, Object> authority = new HashMap<>();
        HashMap<String, Object> data = new HashMap<>();

        for (int i = 0; i < authorities.size(); i++)
            authority.put("rol" + (i + 1), authorities.get(i));

        data.put("code", id);
        data.put("roles", authority);

        char[] charArray = name.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = (char) ~charArray[i];
        }

        return Jwts.builder()
                .setSubject(Arrays.toString(charArray))
                //.setExpiration()
                .addClaims(data)
                .signWith(getSigningKey())
                .compact();
    }

    public static UsernamePasswordAuthenticationToken getAuthorizationToken(String token) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        try {
            Claims claims = getClaims(token);

            String name = decodeClaimSub(claims.getSubject());

            HashMap<String, Object> roles = new HashMap<>(claims.get("roles", HashMap.class));

            roles.forEach((key, rol) -> authorities.add(new SimpleGrantedAuthority(rol.toString())));

            return new UsernamePasswordAuthenticationToken(name, null, authorities);

        } catch (JwtException | NoSuchAlgorithmException e) {
            System.out.println("Can't get token");
            throw new JwtException("Can't get token");
        }
    }

    public static long extractIdInsideToken(String token) {
        try {
            return Long.parseLong(getClaims(token).get("code").toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static Claims getClaims(String token) throws NoSuchAlgorithmException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static String generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    private static String decodeClaimSub(String token) {
        String s = token.substring(1, token.length()-1);
        String[] split = s.split(", "); // Divide la cadena por las comas
        char[] charArray = new char[split.length]; // Crea el array de caracteres
        for (int i = 0; i < split.length; i++) {
            charArray[i] = (char) ~split[i].charAt(0); // Convierte cada cadena a un caracter
        }

        return Arrays.toString(charArray);
    }

    private static Key getSigningKey() throws NoSuchAlgorithmException {
        return Keys.hmacShaKeyFor(ACCESS_SECRET_KEY.getBytes());
    }
}