package mz.agua.exception;

/**
 * Erro técnico de base de dados ou de ligação. A mensagem é adequada para mostrar ao
 * utilizador; o detalhe técnico fica na causa (para registo em log, nunca com credenciais).
 */
public class PersistenciaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
