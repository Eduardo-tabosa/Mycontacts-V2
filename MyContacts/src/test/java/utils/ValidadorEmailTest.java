package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorEmailTest {

    @Test
    void aceitaEmailsValidos() {
        assertTrue(ValidadorEmail.validar("ana@email.com"));
        assertTrue(ValidadorEmail.validar("joao.silva+trabalho@empresa.com.br"));
        assertTrue(ValidadorEmail.validar("  maria@site.org  "), "Espaços nas pontas são ignorados");
    }

    @Test
    void recusaEmailsInvalidos() {
        assertFalse(ValidadorEmail.validar(null));
        assertFalse(ValidadorEmail.validar(""));
        assertFalse(ValidadorEmail.validar("semarroba.com"));
        assertFalse(ValidadorEmail.validar("ana@"));
        assertFalse(ValidadorEmail.validar("ana@email"));
        assertFalse(ValidadorEmail.validar("@email.com"));
        assertFalse(ValidadorEmail.validar("ana @email.com"));
        // a versão antiga aceitava isso, porque só checava "@" e "."
        assertFalse(ValidadorEmail.validar(".@"));
    }
}
