package xyz.shurlin.sprigserver.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptEncoder {
    public static void main(String[] args) {
        String raw = "jibanxianling627";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(raw));
    }
}
