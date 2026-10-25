package mz.agua.exception;

/** O utilizador autenticado não tem permissão para a operação (ou não há sessão). */
public class AcessoNegadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
