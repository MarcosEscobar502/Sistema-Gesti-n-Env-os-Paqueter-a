package model;

public class Repartidor extends Persona {

    private String  codigoRepartidor;
    private String  licencia;
    private boolean disponible;
    private Ruta    rutaActual;

    // ── Constructores ──────────────────────────────────────────
    public Repartidor() {
        this.disponible = true;
    }

    public Repartidor(int id, String nombre, String telefono,
                      String email, String direccion,
                      String codigoRepartidor, String licencia, boolean disponible) {
        super(id, nombre, telefono, email, direccion);
        this.codigoRepartidor = codigoRepartidor;
        this.licencia         = licencia;
        this.disponible       = disponible;
    }

    public Repartidor(String nombre, String telefono,
                      String email, String direccion,
                      String codigoRepartidor, String licencia) {
        super(nombre, telefono, email, direccion);
        this.codigoRepartidor = codigoRepartidor;
        this.licencia         = licencia;
        this.disponible       = true;
    }

    // ── Métodos clave ──────────────────────────────────────────
    public void asignarRuta(Ruta ruta) {
        this.rutaActual = ruta;
        this.disponible = false;
    }

    public void liberarRuta() {
        this.rutaActual = null;
        this.disponible = true;
    }

    public boolean estaDisponible() { return disponible; }

    @Override
    public String getDatosContacto() {
        return "[" + codigoRepartidor + "] " + super.getDatosContacto() +
               " | Disponible: " + (disponible ? "Sí" : "No");
    }

    @Override
    public String toString() {
        return getDatosContacto();
    }

    // ── Getters y Setters ──────────────────────────────────────
    public String getCodigoRepartidor()           { return codigoRepartidor; }
    public void   setCodigoRepartidor(String v)   { this.codigoRepartidor = v; }

    public String getLicencia()                   { return licencia; }
    public void   setLicencia(String v)           { this.licencia = v; }

    public boolean isDisponible()                 { return disponible; }
    public void    setDisponible(boolean v)       { this.disponible = v; }

    public Ruta   getRutaActual()                 { return rutaActual; }
    public void   setRutaActual(Ruta v)           { this.rutaActual = v; }
}
