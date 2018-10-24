package ch.post.it.evoting.verifier.common.block.tools;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.io.UnsupportedEncodingException;

public class JsonWebTokenHelper {


    public static String encodeJWT(String subject, SignatureAlgorithm signAlgo, String signPass) throws UnsupportedEncodingException {
        subject = subject == null ? "no subject" : subject;
        signAlgo = signAlgo == null ? SignatureAlgorithm.HS256 : signAlgo;
        signPass = signPass == null ? "" : signPass;
        String encoded = Jwts.builder()
                            .setSubject(subject)
                            .signWith(signAlgo, signPass.getBytes("UTF-8"))
                            .compact();

        return encoded;

        /** Encode example see https://java.jsonwebtoken.io/
         * or https://github.com/jwtk/jjwt
         *
         private String EncodeJWT(String s) throws UnsupportedEncodingException {
             String ss = Jwts.builder()
             .setSubject("1234567890")
             .setId("196a7845-51f1-4728-9f51-7b84de19694c")
             .setIssuedAt(Date.from(Instant.ofEpochSecond(1533645913)))
             .setExpiration(Date.from(Instant.ofEpochSecond(1533649513)))
             .claim("name", "John Doe")
             .claim("admin", true)
             .signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8"))
             .compact();

             return ss;
         }
         */

    }

    public static Jwt decodeJWT(String jwt) throws UnsupportedEncodingException {
        Jwt result = null;
        try {
            result = Jwts.parser()
                    .parse(jwt);
            //OK, we can trust this JWT

        } catch (SignatureException e) {
            //don't trust the JWT!
        }

        return result;
    }

}
