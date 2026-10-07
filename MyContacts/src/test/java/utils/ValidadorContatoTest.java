package utils;

import exceptions.ContatoInvalidoException;
import model.Contato;
import model.ContatoComercial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorContatoTest {

    @Test
    void contatoValidoPassaSemErro() {
        assertDoesNotThrow(() ->
                ValidadorContato.validar(new Contato("Ana", "(85) 99999-0000", "ana@email.com")));
    }

    @Test
    void nomeObrigatorio() {
        ContatoInvalidoException e = assertThrows(ContatoInvalidoException.class, () ->
                ValidadorContato.validar(new Contato("  ", "85999990000", "ana@email.com")));
        assertTrue(e.getMessage().contains("nome"));
    }

    @Test
    void telefoneInvalido() {
        assertThrows(ContatoInvalidoException.class, () ->
                ValidadorContato.validar(new Contato("Ana", "123", "ana@email.com")));
        assertThrows(ContatoInvalidoException.class, () ->
                ValidadorContato.validar(new Contato("Ana", "abc12345678", "ana@email.com")));
    }

    @Test
    void emailInvalido() {
        assertThrows(ContatoInvalidoException.class, () ->
                ValidadorContato.validar(new Contato("Ana", "85999990000", "ana.email.com")));
    }

    @Test
    void comercialSemEmpresaEhInvalido() {
        assertThrows(ContatoInvalidoException.class, () ->
                ValidadorContato.validar(new ContatoComercial("Bruno", "85999990000", "b@acme.com", "")));
    }
}
