package dao;

import exceptions.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexao {

    public static final String URL_PADRAO = "jdbc:sqlite:mycontacts.db";
    public static final String URL_MEMORIA = "jdbc:sqlite::memory:";

    private static final Logger LOG = Logger.getLogger(Conexao.class.getName());

    private static final String SQL_CRIAR_TABELA =
            "CREATE TABLE IF NOT EXISTS contatos ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " nome VARCHAR(100) NOT NULL,"
                    + " telefone VARCHAR(20),"
                    + " email VARCHAR(100),"
                    + " empresa VARCHAR(100)"   // pode ser nulo
                    + ")";

    private static Connection conexaoApp;

    private Conexao() {
    }

    public static synchronized Connection getConexao() {
        try {
            if (conexaoApp == null || conexaoApp.isClosed()) {
                conexaoApp = abrir(URL_PADRAO);
            }
            return conexaoApp;
        } catch (SQLException e) {
            throw new PersistenciaException("Não foi possível conectar ao banco.", e);
        }
    }

    public static Connection abrir(String url) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        criarTabela(conn);
        LOG.fine("Conectado em " + url);
        return conn;
    }

    public static void criarTabela(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute(SQL_CRIAR_TABELA);
        }
    }

    public static void fechar(Connection conn) {
        if (conn == null) return;
        try {
            conn.close();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Erro ao fechar conexão", e);
        }
    }

    public static synchronized void fecharConexaoApp() {
        fechar(conexaoApp);
        conexaoApp = null;
    }
}
