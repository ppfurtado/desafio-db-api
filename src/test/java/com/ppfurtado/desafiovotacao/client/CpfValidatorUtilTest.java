package com.ppfurtado.desafiovotacao.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorUtilTest {

    @Test
    void sanitize_null_returnsEmpty() {
        assertEquals("", CpfValidatorUtil.sanitize(null));
    }

    @Test
    void sanitize_removesNonDigits() {
        assertEquals("12345678901", CpfValidatorUtil.sanitize("123.456.789-01"));
    }

    @Test
    void isValid_acceptsKnownValidCpf() {
        // example valid CPF
        assertTrue(CpfValidatorUtil.isValid("529.982.247-25"));
        assertTrue(CpfValidatorUtil.isValid("52998224725"));
    }

    @Test
    void isValid_rejectsInvalidCpfs() {
        assertFalse(CpfValidatorUtil.isValid("111.111.111-11")); // all digits equal
        assertFalse(CpfValidatorUtil.isValid("123456789")); // too short
        assertFalse(CpfValidatorUtil.isValid("123.456.789-10")); // invalid checksum
    }
}
