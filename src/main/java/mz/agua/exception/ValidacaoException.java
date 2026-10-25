package mz.agua.exception;

/** Dados inválidos ou regra de negócio violada. A mensagem é para o utilizador. */
public class ValidacaoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
