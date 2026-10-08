package exceptions;

public class ContatoInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ContatoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
