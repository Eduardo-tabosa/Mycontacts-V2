package dao;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de persistência (CRUD) para qualquer entidade T.
 */
public interface DAO<T> {

    T inserir(T entidade);

    boolean atualizar(T entidade);

    boolean remover(int id);

    Optional<T> buscarPorId(int id);

    List<T> listarTodos();
}
