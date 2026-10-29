package model;

public abstract class Persona {

    private int    id;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;

    // ── Constructores ──────────────────────────────────────────
    public Persona() {}

    public Persona(int id, String nombre, String telefono,
                   String email, String direccion) {
        this.id        = id;
        this.nombre    = nombre;
        this.telefono  = telefono;
        this.email     = email;
        this.direccion = direccion;
    }

    public Persona(String nombre, String telefono,
                   String email, String direccion) {
        this.nombre    = nombre;
        this.telefono  = telefono;
        this.email     = email;
        this.direccion = direccion;
    }

    // ── Método clave ───────────────────────────────────────────
    public String getDatosContacto() {
        return nombre + " | Tel: " + telefono + " | Email: " + email;
    }

    @Override
    public String toString() {
        return getDatosContacto();
    }

    // ── Getters y Setters ──────────────────────────────────────
    public int    getId()        { return id; }
    public void   setId(int id)  { this.id = id; }

    public String getNombre()             { return nombre; }
    public void   setNombre(String v)     { this.nombre = v; }

    public String getTelefono()           { return telefono; }
    public void   setTelefono(String v)   { this.telefono = v; }

    public String getEmail()              { return email; }
    public void   setEmail(String v)      { this.email = v; }

    public String getDireccion()          { return direccion; }
    public void   setDireccion(String v)  { this.direccion = v; }
}
