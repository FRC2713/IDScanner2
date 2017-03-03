package org.iraiders.idscanner2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Password {
    static String hashPassword(String password, String salt){
        password += salt;
        byte[] passHash;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));
            passHash = md.digest();
        }catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            passHash = null;
        }
        return String.format("%064x", new java.math.BigInteger(1, passHash));
    }
}
