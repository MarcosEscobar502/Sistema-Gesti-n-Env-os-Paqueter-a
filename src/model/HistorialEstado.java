package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistorialEstado {

    private int            id;
    private EstadoPaquete  estado;
    private LocalDateTime  fecha;
    private String         observacion;
    private String         usuarioResponsable;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Constructores ──────────────────────────────────────────
    public HistorialEstado() {}

    public HistorialEstado(int id, EstadoPaquete estado, LocalDateTime fecha,
                           String observacion, String usuarioResponsable) {
        this.id                 = id;
        this.estado             = estado;
        this.fecha              = fecha;
        this.observacion        = observacion;
        this.usuarioResponsable = usuarioResponsable;
    }

    public HistorialEstado(EstadoPaquete estado, String observacion,
                           String usuarioResponsable) {
        this.estado             = estado;
        this.observacion        = observacion;
        this.usuarioResponsable = usuarioResponsable;
        this.fecha              = LocalDateTime.now();
    }

    // ── Métodos clave ──────────────────────────────────────────
    public String getResumen() {
        return estado + " | " + fecha.format(FMT) +
               " | Usuario: " + usuarioResponsable +
               (observacion != null && !observacion.isEmpty()
                       ? " | Obs: " + observacion : "");
    }

    @Override
    public String toString() { return getResumen(); }

    // ── Getters y Setters ──────────────────────────────────────
    public int           getId()                      { return id; }
    public void          setId(int v)                 { this.id = v; }

    public EstadoPaquete getEstado()                  { return estado; }
    public void          setEstado(EstadoPaquete v)   { this.estado = v; }

    public LocalDateTime getFecha()                   { return fecha; }
    public void          setFecha(LocalDateTime v)    { this.fecha = v; }

    public String        getObservacion()             { return observacion; }
    public void          setObservacion(String v)     { this.observacion = v; }

    public String        getUsuarioResponsable()      { return usuarioResponsable; }
    public void          setUsuarioResponsable(String v){ this.usuarioResponsable = v; }
}
