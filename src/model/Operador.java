package model;

public class Operador extends Usuario {

    private String turno; // MAÑANA, TARDE, NOCHE

    // ── Constructores ──────────────────────────────────────────
    public Operador() {}

    public Operador(int id, String username, String password,
                    boolean activo, String turno) {
        super(id, username, password, RolUsuario.OPERADOR, activo);
        this.turno = turno;
    }

    public Operador(String username, String password, String turno) {
        super(username, password, RolUsuario.OPERADOR);
        this.turno = turno;
    }

    // ── Métodos clave ──────────────────────────────────────────
    public void crearOrden(Orden orden) {
        if (orden == null) throw new IllegalArgumentException("Orden no puede ser nula");
        // lógica delegada al controller
    }

    public void actualizarEstadoPaquete(Paquete paquete,
                                        EstadoPaquete nuevoEstado,
                                        String observacion) {
        if (paquete == null) throw new IllegalArgumentException("Paquete nulo");
        paquete.cambiarEstado(nuevoEstado, observacion, getUsername());
    }

    @Override
    public String toString() {
        return "[OPERADOR] " + getUsername() + " | Turno: " + turno;
    }

    // ── Getters y Setters ──────────────────────────────────────
    public String getTurno()          { return turno; }
    public void   setTurno(String v)  { this.turno = v; }
}
