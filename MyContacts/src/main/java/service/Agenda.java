package service;

import dao.ContatoDAO;
import exceptions.ContatoNaoEncontradoException;
import model.Contato;
import repository.RepositorioGenerico;
import utils.ValidadorContato;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Regras de negócio da agenda.
 *
 * O banco (ContatoDAO) é a fonte oficial dos dados. O RepositorioGenerico
 * funciona como uma cópia em memória, usada para buscas e filtros rápidos
 * na tela sem precisar consultar o banco a cada tecla digitada.
 */
public class Agenda {

    private final ContatoDAO dao;
    private final RepositorioGenerico<Contato> repositorio = new RepositorioGenerico<>();

    public Agenda(ContatoDAO dao) {
        this.dao = dao;
        recarregar();
    }

    /** Lê tudo do banco de novo e atualiza a cópia em memória. */
    public void recarregar() {
        repositorio.substituirTodos(dao.listarTodos());
    }

    public Contato adicionarContato(Contato contato) {
        ValidadorContato.validar(contato);
        dao.inserir(contato);
        recarregar();
        return contato;
    }

    /** Salva vários contatos de uma vez (tudo ou nada). */
    public void adicionarTodos(Collection<? extends Contato> contatos) {
        for (Contato c : contatos) {
            ValidadorContato.validar(c);
        }
        dao.inserirTodos(contatos);
        recarregar();
    }

    public void atualizarContato(Contato contato) throws ContatoNaoEncontradoException {
        ValidadorContato.validar(contato);
        if (!dao.atualizar(contato)) {
            throw new ContatoNaoEncontradoException("Contato com id " + contato.getId() + " não existe.");
        }
        recarregar();
    }

    public void removerContato(int id) throws ContatoNaoEncontradoException {
        if (!dao.remover(id)) {
            throw new ContatoNaoEncontradoException("Contato com id " + id + " não existe.");
        }
        recarregar();
    }

    public List<Contato> listarContatos() {
        return repositorio.listarTodos();
    }

    /** Busca por parte do nome, ignorando maiúsculas e minúsculas. */
    public List<Contato> buscarPorNome(String trecho) {
        if (trecho == null || trecho.isBlank()) {
            return listarContatos();
        }
        String termo = trecho.trim().toLowerCase(Locale.ROOT);
        return repositorio.buscar(c -> c.getNome().toLowerCase(Locale.ROOT).contains(termo));
    }

    public Contato buscarPorId(int id) throws ContatoNaoEncontradoException {
        return repositorio.buscarPrimeiro(c -> c.getId() == id)
                .orElseThrow(() -> new ContatoNaoEncontradoException("Contato com id " + id + " não existe."));
    }

    /** Ex: listarPorTipo(ContatoComercial.class) */
    public <S extends Contato> List<S> listarPorTipo(Class<S> tipo) {
        return repositorio.filtrarPorTipo(tipo);
    }

    public int totalContatos() {
        return repositorio.tamanho();
    }
}
