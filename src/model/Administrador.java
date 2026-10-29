package model;

public class Administrador extends Persona {

    private String cargo;
    // Credenciales de usuario embebidas (herencia múltiple simulada)
    private String     username;
    private String     password;
    private RolUsuario rol = RolUsuario.ADMINISTRADOR;
    private boolean    activo = true;

    // ── Constructores ──────────────────────────────────────────
    public Administrador() {}

    public Administrador(int id, String nombre, String telefono,
                         String email, String direccion,
                         String cargo, String username, String password) {
        super(id, nombre, telefono, email, direccion);
        this.cargo    = cargo;
        this.username = username;
        this.password = password;
        this.activo   = true;
        this.rol      = RolUsuario.ADMINISTRADOR;
    }

    public Administrador(String nombre, String telefono,
                         String email, String direccion,
                         String cargo, String username, String password) {
        super(nombre, telefono, email, direccion);
        this.cargo    = cargo;
        this.username = username;
        this.password = password;
    }

    // ── Métodos clave ──────────────────────────────────────────
    public boolean login(String user, String pass) {
        return activo && this.username.equals(user) && this.password.equals(pass);
    }

    public boolean tienePermiso(String accion) { return activo; }

    public void registrarRepartidor(Repartidor r) {
        // lógica delegada al controller/empresa
        if (r == null) throw new IllegalArgumentException("Repartidor no puede ser nulo");
    }

    public boolean eliminarRepartidor(String codigo) {
        return codigo != null && !codigo.isBlank();
    }

    public void registrarRuta(Ruta r) {
        if (r == null) throw new IllegalArgumentException("Ruta no puede ser nula");
    }

    public void gestionarUsuario(Usuario u) {
        if (u == null) throw new IllegalArgumentException("Usuario no puede ser nulo");
    }

    @Override
    public String getDatosContacto() {
        return "[ADMIN] " + super.getDatosContacto() + " | Cargo: " + cargo;
    }

    @Override
    public String toString() { return getDatosContacto(); }

    // ── Getters y Setters ──────────────────────────────────────
    public String     getCargo()             { return cargo; }
    public void       setCargo(String v)     { this.cargo = v; }

    public String     getUsername()          { return username; }
    public void       setUsername(String v)  { this.username = v; }

    public String     getPassword()          { return password; }
    public void       setPassword(String v)  { this.password = v; }

    public RolUsuario getRol()               { return rol; }
    public boolean    isActivo()             { return activo; }
    public void       setActivo(boolean v)   { this.activo = v; }
}
