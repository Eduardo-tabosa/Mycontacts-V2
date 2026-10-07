package repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Repositório genérico em memória.
 * Funciona com qualquer tipo T: Contato, ContatoComercial ou outro.
 *
 * Exemplos dos curingas:
 *  - "? extends T" em adicionarTodos: aceita uma lista de subtipos
 *    (ex: List<ContatoComercial> dentro de um RepositorioGenerico<Contato>).
 *  - "? super T" em buscar/remover/copiarPara: aceita filtros e destinos
 *    de um supertipo (ex: um Predicate<Object> ou uma List<Object>).
 *  - "?" em contar: aceita qualquer coleção, sem se importar com o tipo.
 */
public class RepositorioGenerico<T> {

    private final List<T> elementos = new ArrayList<>();

    public void adicionar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("Elemento não pode ser nulo.");
        }
        elementos.add(elemento);
    }

    public void adicionarTodos(Collection<? extends T> novos) {
        for (T elemento : novos) {
            adicionar(elemento);
        }
    }

    /** Lista somente leitura com todos os elementos. */
    public List<T> listarTodos() {
        return Collections.unmodifiableList(elementos);
    }

    public List<T> buscar(Predicate<? super T> filtro) {
        List<T> resultado = new ArrayList<>();
        for (T elemento : elementos) {
            if (filtro.test(elemento)) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }

    public Optional<T> buscarPrimeiro(Predicate<? super T> filtro) {
        for (T elemento : elementos) {
            if (filtro.test(elemento)) {
                return Optional.of(elemento);
            }
        }
        return Optional.empty();
    }

    /**
     * Retorna somente os elementos de um subtipo específico.
     * Ex: repo.filtrarPorTipo(ContatoComercial.class) devolve List<ContatoComercial>.
     */
    public <S extends T> List<S> filtrarPorTipo(Class<S> tipo) {
        List<S> resultado = new ArrayList<>();
        for (T elemento : elementos) {
            if (tipo.isInstance(elemento)) {
                resultado.add(tipo.cast(elemento));
            }
        }
        return resultado;
    }

    public boolean remover(T elemento) {
        return elementos.remove(elemento);
    }

    /** Remove todos que atendem ao filtro e retorna quantos foram removidos. */
    public int removerSe(Predicate<? super T> filtro) {
        int antes = elementos.size();
        elementos.removeIf(filtro);
        return antes - elementos.size();
    }

    /** Copia os elementos para uma coleção de um supertipo. */
    public void copiarPara(Collection<? super T> destino) {
        destino.addAll(elementos);
    }

    public void substituirTodos(Collection<? extends T> novos) {
        elementos.clear();
        adicionarTodos(novos);
    }

    public int tamanho() {
        return elementos.size();
    }

    public void limpar() {
        elementos.clear();
    }

    /** Método utilitário que conta itens de qualquer coleção. */
    public static int contar(Collection<?> colecao) {
        return colecao == null ? 0 : colecao.size();
    }
}
