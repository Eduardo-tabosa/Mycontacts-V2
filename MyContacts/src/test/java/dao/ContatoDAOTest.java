package dao;

import exceptions.PersistenciaException;
import model.Contato;
import model.ContatoComercial;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ContatoDAOTest {

    private Connection conn;
    private ContatoDAO dao;

    @BeforeEach
    void abrirBanco() throws SQLException {
        conn = Conexao.abrir(Conexao.URL_MEMORIA);
        dao = new ContatoDAO(conn);
    }

    @AfterEach
    void fecharBanco() {
        Conexao.fechar(conn);
    }

    @Test
    void inserirGeraId() {
        Contato c = dao.inserir(new Contato("Ana", "85999990000", "ana@email.com"));

        assertTrue(c.getId() > 0);
        assertEquals(1, dao.listarTodos().size());
    }

    @Test
    void buscarPorIdDevolveTipoCorreto() {
        int idPessoal = dao.inserir(new Contato("Ana", "85999990000", "ana@email.com")).getId();
        int idComercial = dao.inserir(new ContatoComercial("Bruno", "8533334444", "b@acme.com", "ACME")).getId();

        assertFalse(dao.buscarPorId(idPessoal).get() instanceof ContatoComercial);

        Contato comercial = dao.buscarPorId(idComercial).get();
        assertTrue(comercial instanceof ContatoComercial);
        assertEquals("ACME", comercial.getEmpresa());
    }

    @Test
    void buscarPorIdInexistente() {
        assertEquals(Optional.empty(), dao.buscarPorId(999));
    }

    @Test
    void atualizar() {
        Contato c = dao.inserir(new Contato("Ana", "85999990000", "ana@email.com"));
        c.setTelefone("85988887777");

        assertTrue(dao.atualizar(c));
        assertEquals("85988887777", dao.buscarPorId(c.getId()).get().getTelefone());
    }

    @Test
    void atualizarPessoalParaComercial() {
        Contato c = dao.inserir(new Contato("Ana", "85999990000", "ana@email.com"));

        dao.atualizar(new ContatoComercial(c.getId(), "Ana", "85999990000", "ana@email.com", "Loja"));

        assertTrue(dao.buscarPorId(c.getId()).get() instanceof ContatoComercial);
    }

    @Test
    void remover() {
        Contato c = dao.inserir(new Contato("Ana", "85999990000", "ana@email.com"));

        assertTrue(dao.remover(c.getId()));
        assertFalse(dao.remover(c.getId()), "Segunda remoção não acha mais nada");
        assertTrue(dao.listarTodos().isEmpty());
    }

    @Test
    void listarVemEmOrdemAlfabetica() {
        dao.inserir(new Contato("carlos", "85999990000", "c@email.com"));
        dao.inserir(new Contato("Ana", "85999990000", "a@email.com"));
        dao.inserir(new Contato("Bruno", "85999990000", "b@email.com"));

        List<Contato> lista = dao.listarTodos();

        assertEquals("Ana", lista.get(0).getNome());
        assertEquals("Bruno", lista.get(1).getNome());
        assertEquals("carlos", lista.get(2).getNome());
    }

    @Test
    void buscarPorNomeParcialSemDiferenciarMaiusculas() {
        dao.inserir(new Contato("Maria Souza", "85999990000", "m@email.com"));
        dao.inserir(new Contato("José", "85999990000", "j@email.com"));

        assertEquals(1, dao.buscarPorNome("SOUZA").size());
        assertEquals(0, dao.buscarPorNome("Pedro").size());
    }

    @Test
    void buscaProtegidaContraSqlInjection() {
        dao.inserir(new Contato("Ana", "85999990000", "a@email.com"));

        List<Contato> resultado = dao.buscarPorNome("'; DROP TABLE contatos; --");

        assertTrue(resultado.isEmpty());
        assertEquals(1, dao.listarTodos().size(), "A tabela continua intacta");
    }

    @Test
    void inserirTodosEmTransacao() {
        List<ContatoComercial> lote = List.of(
                new ContatoComercial("Carla", "8530001111", "c@x.com", "X"),
                new ContatoComercial("Davi", "8530002222", "d@y.com", "Y"));

        dao.inserirTodos(lote);

        assertEquals(2, dao.listarTodos().size());
    }

    @Test
    void inserirTodosFazRollbackSeUmFalhar() {
        Contato semNome = new Contato("Ok", "85999990000", "ok@x.com") {
            @Override
            public String getNome() {
                return null;
            }
        };
        List<Contato> lote = List.of(new Contato("Primeiro", "85999990000", "p@x.com"), semNome);

        assertThrows(PersistenciaException.class, () -> dao.inserirTodos(lote));
        assertEquals(0, dao.listarTodos().size(), "Nada deve ter sido gravado");
    }

    @Test
    void erroDeBancoViraPersistenciaException() throws SQLException {
        conn.close();
        assertThrows(PersistenciaException.class, () -> dao.listarTodos());
    }
}
