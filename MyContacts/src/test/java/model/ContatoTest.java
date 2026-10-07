package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContatoTest {

    @Test
    void deveCriarContatoPessoal() {
        Contato c = new Contato("Ana", "85999990000", "ana@email.com");

        assertEquals("Ana", c.getNome());
        assertEquals("Pessoal", c.getTipo());
        assertNull(c.getEmpresa());
        assertEquals(0, c.getId(), "Contato novo ainda não tem id do banco");
    }

    @Test
    void deveCriarContatoComercialComEmpresa() {
        ContatoComercial c = new ContatoComercial("Bruno", "8533334444", "bruno@acme.com", "ACME");

        assertEquals("Comercial", c.getTipo());
        assertEquals("ACME", c.getEmpresa());
        assertTrue(c.apresentar().contains("Empresa: ACME"));
    }

    @Test
    void contatosComMesmoIdSaoIguais() {
        Contato a = new Contato(7, "Ana", "85999990000", "ana@email.com");
        Contato b = new Contato(7, "Ana Maria", "85999990000", "ana@email.com");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
