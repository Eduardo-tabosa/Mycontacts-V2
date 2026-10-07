package service;

import dao.Conexao;
import dao.ContatoDAO;
import exceptions.ContatoInvalidoException;
import exceptions.ContatoNaoEncontradoException;
import model.Contato;
import model.ContatoComercial;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgendaTest {

    private Connection conn;
    private Agenda agenda;

    @BeforeEach
    void preparar() throws SQLException {
        conn = Conexao.abrir(Conexao.URL_MEMORIA);
        agenda = new Agenda(new ContatoDAO(conn));
    }

    @AfterEach
    void encerrar() {
        Conexao.fechar(conn);
    }

    @Test
    void adicionarContato() {
        agenda.adicionarContato(new Contato("Ana", "85999990000", "ana@email.com"));

        assertEquals(1, agenda.totalContatos());
    }

    @Test
    void naoAdicionaContatoInvalido() {
        assertThrows(ContatoInvalidoException.class, () ->
                agenda.adicionarContato(new Contato("Ana", "85999990000", "email-errado")));
        assertEquals(0, agenda.totalContatos());
    }

    @Test
    void buscarPorNome() {
        agenda.adicionarContato(new Contato("Maria Souza", "85999990000", "m@email.com"));
        agenda.adicionarContato(new Contato("Mário Lima", "85999990000", "ml@email.com"));
        agenda.adicionarContato(new Contato("José", "85999990000", "j@email.com"));

        assertEquals(1, agenda.buscarPorNome("maria").size());
        assertEquals(3, agenda.buscarPorNome("").size(), "Busca vazia lista todos");
        assertTrue(agenda.buscarPorNome("Pedro").isEmpty());
    }

    @Test
    void removerContato() throws ContatoNaoEncontradoException {
        Contato c = agenda.adicionarContato(new Contato("Ana", "85999990000", "ana@email.com"));

        agenda.removerContato(c.getId());

        assertEquals(0, agenda.totalContatos());
    }

    @Test
    void removerInexistenteLancaExcecao() {
        assertThrows(ContatoNaoEncontradoException.class, () -> agenda.removerContato(42));
    }

    @Test
    void atualizarContato() throws ContatoNaoEncontradoException {
        Contato c = agenda.adicionarContato(new Contato("Ana", "85999990000", "ana@email.com"));
        c.setNome("Ana Paula");

        agenda.atualizarContato(c);

        assertEquals("Ana Paula", agenda.buscarPorId(c.getId()).getNome());
    }

    @Test
    void buscarPorIdInexistenteLancaExcecao() {
        assertThrows(ContatoNaoEncontradoException.class, () -> agenda.buscarPorId(1));
    }

    @Test
    void listarPorTipo() {
        agenda.adicionarContato(new Contato("Ana", "85999990000", "ana@email.com"));
        agenda.adicionarContato(new ContatoComercial("Bruno", "8533334444", "b@acme.com", "ACME"));

        List<ContatoComercial> comerciais = agenda.listarPorTipo(ContatoComercial.class);

        assertEquals(1, comerciais.size());
        assertEquals("Bruno", comerciais.get(0).getNome());
    }

    @Test
    void dadosContinuamNoBancoAoRecriarAgenda() {
        agenda.adicionarContato(new Contato("Ana", "85999990000", "ana@email.com"));

        Agenda outra = new Agenda(new ContatoDAO(conn));

        assertEquals(1, outra.totalContatos());
    }
}
