package repository;

import model.Contato;
import model.ContatoComercial;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioGenericoTest {

    private RepositorioGenerico<Contato> repo;

    @BeforeEach
    void preparar() {
        repo = new RepositorioGenerico<>();
        repo.adicionar(new Contato(1, "Ana", "85999990000", "ana@email.com"));
        repo.adicionar(new ContatoComercial(2, "Bruno", "8533334444", "bruno@acme.com", "ACME"));
    }

    @AfterEach
    void limpar() {
        repo.limpar();
    }

    @Test
    void adicionarEListar() {
        assertEquals(2, repo.tamanho());
        assertEquals("Ana", repo.listarTodos().get(0).getNome());
    }

    @Test
    void listaRetornadaNaoPodeSerAlterada() {
        assertThrows(UnsupportedOperationException.class, () ->
                repo.listarTodos().add(new Contato("X", "85999990000", "x@x.com")));
    }

    @Test
    void naoAceitaNulo() {
        assertThrows(IllegalArgumentException.class, () -> repo.adicionar(null));
    }

    @Test
    void adicionarTodosAceitaListaDeSubtipo() {
        // ? extends T: uma List<ContatoComercial> entra num repositório de Contato
        List<ContatoComercial> comerciais = List.of(
                new ContatoComercial(3, "Carla", "8530001111", "carla@x.com", "X"),
                new ContatoComercial(4, "Davi", "8530002222", "davi@y.com", "Y"));

        repo.adicionarTodos(comerciais);

        assertEquals(4, repo.tamanho());
    }

    @Test
    void buscarAceitaFiltroDeSupertipo() {
        // ? super T: um Predicate<Object> serve para filtrar Contato
        Predicate<Object> temNomeComA = o -> o.toString().startsWith("A");

        List<Contato> achados = repo.buscar(temNomeComA);

        assertEquals(1, achados.size());
        assertEquals("Ana", achados.get(0).getNome());
    }

    @Test
    void filtrarPorTipoRetornaSubtipo() {
        List<ContatoComercial> comerciais = repo.filtrarPorTipo(ContatoComercial.class);

        assertEquals(1, comerciais.size());
        assertEquals("ACME", comerciais.get(0).getEmpresa());
    }

    @Test
    void removerERemoverSe() {
        assertTrue(repo.remover(new Contato(1, "Ana", "", "")));
        assertEquals(1, repo.tamanho());

        int removidos = repo.removerSe(c -> c instanceof ContatoComercial);
        assertEquals(1, removidos);
        assertEquals(0, repo.tamanho());
    }

    @Test
    void copiarParaColecaoDeSupertipo() {
        List<Object> destino = new ArrayList<>();
        repo.copiarPara(destino);
        assertEquals(2, destino.size());
    }

    @Test
    void contarQualquerColecao() {
        assertEquals(3, RepositorioGenerico.contar(List.of("a", "b", "c")));
        assertEquals(0, RepositorioGenerico.contar(null));
    }

    @Test
    void funcionaComOutrosTipos() {
        RepositorioGenerico<String> nomes = new RepositorioGenerico<>();
        nomes.adicionar("Ana");
        nomes.adicionar("Bruno");
        assertTrue(nomes.buscarPrimeiro(n -> n.startsWith("B")).isPresent());
    }
}
