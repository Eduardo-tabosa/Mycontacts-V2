package dao;

import exceptions.PersistenciaException;
import model.Contato;
import model.ContatoComercial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Persistência dos contatos na tabela "contatos".
 *
 * Todas as consultas usam PreparedStatement com parâmetros (?),
 * o que impede SQL Injection: o valor digitado pelo usuário nunca
 * é concatenado direto no comando SQL.
 */
public class ContatoDAO implements DAO<Contato> {

    private static final Logger LOG = Logger.getLogger(ContatoDAO.class.getName());

    private static final String INSERT =
            "INSERT INTO contatos (nome, telefone, email, empresa) VALUES (?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE contatos SET nome = ?, telefone = ?, email = ?, empresa = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM contatos WHERE id = ?";
    private static final String SELECT_POR_ID =
            "SELECT id, nome, telefone, email, empresa FROM contatos WHERE id = ?";
    private static final String SELECT_TODOS =
            "SELECT id, nome, telefone, email, empresa FROM contatos ORDER BY nome COLLATE NOCASE";
    private static final String SELECT_POR_NOME =
            "SELECT id, nome, telefone, email, empresa FROM contatos "
                    + "WHERE nome LIKE ? COLLATE NOCASE ORDER BY nome COLLATE NOCASE";

    private final Connection conn;

    public ContatoDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Contato inserir(Contato contato) {
        try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
            preencher(ps, contato);
            ps.executeUpdate();
            contato.setId(ultimoIdGerado());
            return contato;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao inserir contato", e);
            throw new PersistenciaException("Erro ao salvar o contato.", e);
        }
    }

    /**
     * Insere vários contatos em uma única transação.
     * Se um deles falhar, nenhum é gravado (rollback).
     * O curinga "? extends Contato" permite passar List<ContatoComercial>, por exemplo.
     */
    public void inserirTodos(Collection<? extends Contato> contatos) {
        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                for (Contato c : contatos) {
                    preencher(ps, c);
                    ps.executeUpdate();
                    c.setId(ultimoIdGerado());
                }
            }
            conn.commit();
        } catch (SQLException | RuntimeException e) {
            desfazer();
            LOG.log(Level.SEVERE, "Erro ao salvar lote, transação desfeita", e);
            throw new PersistenciaException("Erro ao salvar os contatos. Nada foi gravado.", e);
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal);
            } catch (SQLException e) {
                LOG.log(Level.WARNING, "Não foi possível restaurar o autocommit", e);
            }
        }
    }

    @Override
    public boolean atualizar(Contato contato) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            preencher(ps, contato);
            ps.setInt(5, contato.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao atualizar contato", e);
            throw new PersistenciaException("Erro ao atualizar o contato.", e);
        }
    }

    @Override
    public boolean remover(int id) {
        try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao remover contato", e);
            throw new PersistenciaException("Erro ao remover o contato.", e);
        }
    }

    @Override
    public Optional<Contato> buscarPorId(int id) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_POR_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao buscar contato", e);
            throw new PersistenciaException("Erro ao buscar o contato.", e);
        }
    }

    @Override
    public List<Contato> listarTodos() {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_TODOS);
             ResultSet rs = ps.executeQuery()) {
            return montarLista(rs);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao listar contatos", e);
            throw new PersistenciaException("Erro ao listar os contatos.", e);
        }
    }

    /** Busca por parte do nome, sem diferenciar maiúsculas e minúsculas. */
    public List<Contato> buscarPorNome(String trecho) {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_POR_NOME)) {
            ps.setString(1, "%" + trecho + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return montarLista(rs);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao buscar por nome", e);
            throw new PersistenciaException("Erro ao buscar contatos.", e);
        }
    }

    // ---------- auxiliares ----------

    private void preencher(PreparedStatement ps, Contato c) throws SQLException {
        ps.setString(1, limpar(c.getNome()));
        ps.setString(2, limpar(c.getTelefone()));
        ps.setString(3, limpar(c.getEmail()));
        ps.setString(4, c.getEmpresa() == null || c.getEmpresa().isBlank() ? null : c.getEmpresa().trim());
    }

    /** Id criado pelo AUTOINCREMENT no último INSERT desta conexão. */
    private int ultimoIdGerado() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private String limpar(String texto) {
        return texto == null ? null : texto.trim();
    }

    /** Se a coluna empresa estiver preenchida, o contato é comercial. */
    private Contato montar(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String telefone = rs.getString("telefone");
        String email = rs.getString("email");
        String empresa = rs.getString("empresa");

        if (empresa != null && !empresa.isBlank()) {
            return new ContatoComercial(id, nome, telefone, email, empresa);
        }
        return new Contato(id, nome, telefone, email);
    }

    private List<Contato> montarLista(ResultSet rs) throws SQLException {
        List<Contato> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(montar(rs));
        }
        return lista;
    }

    private void desfazer() {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Falha no rollback", ex);
        }
    }
}
