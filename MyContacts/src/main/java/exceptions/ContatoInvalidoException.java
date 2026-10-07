package exceptions;

/** Lançada quando os dados de um contato não passam na validação. */
public class ContatoInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ContatoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
