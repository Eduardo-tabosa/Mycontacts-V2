package exceptions;

/** Lançada quando uma busca ou remoção não encontra o contato. */
public class ContatoNaoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    public ContatoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
