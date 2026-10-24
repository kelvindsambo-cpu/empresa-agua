package mz.agua.exception;

/** Falha de início de sessão (credenciais inválidas, conta inactiva ou bloqueio temporário). */
public class AutenticacaoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
