/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.common.block.tools;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Class JsonWebTokenHelperTest.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class JsonWebTokenHelperTest {

    @Test
    public void JsonWebTokenHelperEncodeMethodTest() throws UnsupportedEncodingException {
        String encodedJWT = JsonWebTokenHelper.encodeJWT("sujet de test", null, "passPhrase");
        Assert.assertNotNull(encodedJWT);
        Assert.assertTrue(encodedJWT.getBytes().length > 0);
    }

    @Test
    public void encodeAndDecodeJWT() throws ParseException, UnsupportedEncodingException {

        // Given
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateExpiration = simpleDateFormat.parse("22/06/2099");
        String subjectValue = "Test de JsonWebTokenHelper";
        String nameValue = "John Doe";
        boolean adminValue = true;

        // When
        String jwtString = null;
        try {
            jwtString = Jwts.builder()
                    .setSubject(subjectValue)
                    .setId("196a7845-51f1-4728-9f51-7b84de19694c")
                    .setIssuedAt(new Date())
                    .setExpiration(dateExpiration)
                    .claim("name", nameValue)
                    .claim("admin", adminValue)
                    .signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8"))
                    .compact();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Then
        try {
            Jwt jwt = Jwts.parser()
                    .setSigningKey("secret".getBytes("UTF-8"))
                    .parse(jwtString);

            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey("secret".getBytes("UTF-8"))
                    .parseClaimsJws(jwtString);

            final String name = (String) claimsJws.getBody().get("name");
            final boolean admin = (boolean) claimsJws.getBody().get("admin");

            //OK, we can trust this JWT
            DefaultClaims body = (DefaultClaims) jwt.getBody();
            Assert.assertEquals(subjectValue, body.getSubject());
            Assert.assertEquals(nameValue, name);
            Assert.assertEquals(adminValue, admin);

        } catch (SignatureException e) {
            //don't trust the JWT!
        }
    }




}