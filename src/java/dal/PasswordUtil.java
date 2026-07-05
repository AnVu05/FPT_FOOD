/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.security.MessageDigest;

/**
 *
 * @author Nitro 5 Tiger
 */
public class PasswordUtil {

    public static String hashPassword(String password, String username) {
        try {

            String saltedPassword = password + username;

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = md.digest(saltedPassword.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {

                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
