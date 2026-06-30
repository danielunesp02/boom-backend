package com.boom.auth.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaskingServiceTest {

    private final MaskingService maskingService = new MaskingService();

    @Test
    void shouldMaskDocument() {
        assertEquals("*******8901", maskingService.maskDocument("123.456.789-01"));
    }

    @Test
    void shouldMaskPhone() {
        assertEquals("+**********9999", maskingService.maskPhone("+55 19 99999-9999"));
    }
}
