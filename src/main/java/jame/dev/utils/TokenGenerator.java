package jame.dev.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {

    public static String genToken(){
        byte[] bytes = new byte[8];
        new SecureRandom().nextBytes(bytes);
        String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return base64.replaceAll("[^A-Za-z0-9]", "").toUpperCase().substring(0,6);
    }
}
