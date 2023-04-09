package com.example.cafebackend.utils;

import java.util.Random;

public class PasswordGenerator {

    public static String generatePassword() {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$.,/?";
        String numbers = "0123456789";
        String combination = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        int passLength = 8;
        char[] password = new char[passLength];
        Random random = new Random();

        //TO ENSURE PASSWORD CONTAINS AT LEAST 1 lowerCase, 1 upperCase, 1 specialChar and 1 number
        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 4; i< passLength ; i++) {
            password[i] = combination.charAt(random.nextInt(combination.length()));
        }

        return new String(password);
    }
}
