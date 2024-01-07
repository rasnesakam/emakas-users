package com.emakas.userService.shared;

import com.emakas.userService.model.LoginModel;
import com.emakas.userService.model.User;
import com.emakas.userService.model.UserDto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class AuthHelper {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String ALGORITHM = "SHA3-256";
    private static final MessageDigest MESSAGE_DIGEST;


    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateRandomPasswordSalt(){
        byte[] bytes = new byte[10];
        new Random().nextBytes(bytes);
        return new String(bytes,CHARSET);
    }
    public static String getHashedPassword(String password, String salt){
        byte[] hashed =  MESSAGE_DIGEST.digest(password.concat(salt).getBytes());
        return new String(hashed, CHARSET).intern();
    }
    public static boolean checkPassword(User user, LoginModel userDto){

        byte[] userPasswordHash = user.getPassword()
                .getBytes(Charset.defaultCharset());
        byte[] providedPasswordHash = MESSAGE_DIGEST.digest(
                userDto.getPassword().getBytes(CHARSET)
        );
        return Arrays.equals(userPasswordHash,providedPasswordHash);
    }
}
