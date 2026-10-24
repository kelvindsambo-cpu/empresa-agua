package mz.agua.model;

/**
 * Perfil aplicacional (autorização). Não confundir com funcionario.cargo (função de negócio).
 * O valor guardado em utilizador.perfil é o do ENUM SQL.
 */
public enum Perfil {
    ADMIN("ADMIN"),
    OPERADOR("OPERADOR");

    private final String valorBd;

    Perfil(String valorBd) {
        this.valorBd = valorBd;
    }

    /** Valor exacto do ENUM SQL. */
    public String getValorBd() {
        return valorBd;
    }

    /** Conversão validada: falha se o valor lido da BD não for conhecido. */
    public static Perfil deValorBd(String valor) {
        if (valor != null) {
            for (Perfil p : values()) {
                if (p.valorBd.equals(valor)) {
                    return p;
                }
            }
        }
        throw new IllegalArgumentException("Perfil desconhecido na base de dados: " + valor);
    }
}
