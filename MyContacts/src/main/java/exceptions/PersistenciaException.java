package exceptions;

/**
 * Embrulha erros de banco (SQLException) para que a camada de tela
 * não precise conhecer detalhes do JDBC.
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
