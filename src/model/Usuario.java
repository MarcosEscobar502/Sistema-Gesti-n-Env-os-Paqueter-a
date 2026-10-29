package model;

public abstract class Usuario {

    private int        id;
    private String     username;
    private String     password;
    private RolUsuario rol;
    private boolean    activo;

    // ── Constructores ──────────────────────────────────────────
    public Usuario() {}

    public Usuario(int id, String username, String password,
                   RolUsuario rol, boolean activo) {
        this.id       = id;
        this.username = username;
        this.password = password;
        this.rol      = rol;
        this.activo   = activo;
    }

    public Usuario(String username, String password, RolUsuario rol) {
        this.username = username;
        this.password = password;
        this.rol      = rol;
        this.activo   = true;
    }

    // ── Métodos clave ──────────────────────────────────────────
    public boolean login(String username, String password) {
        return this.activo &&
               this.username.equals(username) &&
               this.password.equals(password);
    }

    public void logout() {
        // lógica de cierre de sesión (manejada en controller)
    }

    public boolean tienePermiso(String accion) {
        if (!activo) return false;
        // ADMINISTRADOR tiene todos los permisos
        if (rol == RolUsuario.ADMINISTRADOR) return true;
        // OPERADOR solo puede crear órdenes y actualizar estados
        return accion.equals("CREAR_ORDEN") ||
               accion.equals("ACTUALIZAR_ESTADO") ||
               accion.equals("VER_PAQUETES") ||
               accion.equals("VER_CLIENTES");
    }

    @Override
    public String toString() {
        return "[" + rol + "] " + username + (activo ? "" : " (inactivo)");
    }

    // ── Getters y Setters ──────────────────────────────────────
    public int    getId()              { return id; }
    public void   setId(int v)         { this.id = v; }

    public String getUsername()        { return username; }
    public void   setUsername(String v){ this.username = v; }

    public String getPassword()        { return password; }
    public void   setPassword(String v){ this.password = v; }

    public RolUsuario getRol()         { return rol; }
    public void       setRol(RolUsuario v){ this.rol = v; }

    public boolean isActivo()          { return activo; }
    public void    setActivo(boolean v){ this.activo = v; }
}
