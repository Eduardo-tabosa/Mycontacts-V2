package repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    public int removerSe(Predicate<? super T> filtro) {
        int antes = elementos.size();
        elementos.removeIf(filtro);
        return antes - elementos.size();
    }

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

    public static int contar(Collection<?> colecao) {
        return colecao == null ? 0 : colecao.size();
    }
}
