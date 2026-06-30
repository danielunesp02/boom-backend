package com.boom.auth.security;

import org.springframework.stereotype.Service;

@Service
public class MaskingService {

    public String maskDocument(String documentNumber) {
        String digits = onlyDigits(documentNumber);
        if (digits.length() <= 4) {
            return "****";
        }
        return "*".repeat(Math.max(0, digits.length() - 4)) + digits.substring(digits.length() - 4);
    }

    public String maskPhone(String phoneNumber) {
        String digits = onlyDigits(phoneNumber);
        if (digits.length() <= 4) {
            return "****";
        }
        return "+" + "*".repeat(Math.max(0, digits.length() - 4)) + digits.substring(digits.length() - 4);
    }

    public String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D+", "");
    }
}
