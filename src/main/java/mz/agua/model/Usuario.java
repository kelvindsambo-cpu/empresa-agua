package mz.agua.model;

/**
 * Conta de utilizador (tabela utilizador) com dados de leitura do funcionário associado.
 * Não conhece Swing nem JDBC. O hash nunca aparece em toString().
 */
public class Usuario {

    private final int idUtilizador;
    private final String username;
    private final String passwordHash;   // null depois de semPasswordHash()
    private final Perfil perfil;
    private final boolean activo;
    private final int idFuncionario;
    private final String nomeFuncionario;    // só leitura (vem de funcionario.nome)
    private final String cargoFuncionario;   // só leitura (valor do ENUM funcionario.cargo)

    public Usuario(int idUtilizador, String username, String passwordHash, Perfil perfil,
                   boolean activo, int idFuncionario, String nomeFuncionario, String cargoFuncionario) {
        this.idUtilizador = idUtilizador;
        this.username = username;
        this.passwordHash = passwordHash;
        this.perfil = perfil;
        this.activo = activo;
        this.idFuncionario = idFuncionario;
        this.nomeFuncionario = nomeFuncionario;
        this.cargoFuncionario = cargoFuncionario;
    }

    public int getIdUtilizador() { return idUtilizador; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Perfil getPerfil() { return perfil; }
    public boolean isActivo() { return activo; }
    public int getIdFuncionario() { return idFuncionario; }
    public String getNomeFuncionario() { return nomeFuncionario; }
    public String getCargoFuncionario() { return cargoFuncionario; }

    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }

    /** Cópia sem o hash: é a única forma que pode ser guardada na sessão ou mostrada na UI. */
    public Usuario semPasswordHash() {
        return new Usuario(idUtilizador, username, null, perfil, activo,
                idFuncionario, nomeFuncionario, cargoFuncionario);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + idUtilizador + ", username=" + username + ", perfil=" + perfil
                + ", activo=" + activo + ", idFuncionario=" + idFuncionario + "}";
    }
}
