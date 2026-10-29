package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Paquete {

    private int                      id;
    private String                   codigoPaquete;
    private String                   descripcion;
    private double                   peso;
    private double                   largo;
    private double                   ancho;
    private double                   alto;
    private EstadoPaquete            estado;
    private Cliente                  remitente;
    private Persona                  destinatario;
    private Repartidor               repartidor;
    private Ruta                     ruta;
    private ArrayList<HistorialEstado> historial;
    private LocalDateTime            fechaCreacion;
    private double                   costoEnvio;

    // ── Constructores ──────────────────────────────────────────
    public Paquete() {
        this.historial    = new ArrayList<>();
        this.estado       = EstadoPaquete.RECIBIDO;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Paquete(int id, String codigoPaquete, String descripcion,
                   double peso, double largo, double ancho, double alto,
                   EstadoPaquete estado, Cliente remitente,
                   Persona destinatario, LocalDateTime fechaCreacion,
                   double costoEnvio) {
        this.id            = id;
        this.codigoPaquete = codigoPaquete;
        this.descripcion   = descripcion;
        this.peso          = peso;
        this.largo         = largo;
        this.ancho         = ancho;
        this.alto          = alto;
        this.estado        = estado;
        this.remitente     = remitente;
        this.destinatario  = destinatario;
        this.fechaCreacion = fechaCreacion;
        this.costoEnvio    = costoEnvio;
        this.historial     = new ArrayList<>();
    }

    public Paquete(String codigoPaquete, String descripcion,
                   double peso, double largo, double ancho, double alto,
                   Cliente remitente, Persona destinatario) {
        this.codigoPaquete = codigoPaquete;
        this.descripcion   = descripcion;
        this.peso          = peso;
        this.largo         = largo;
        this.ancho         = ancho;
        this.alto          = alto;
        this.remitente     = remitente;
        this.destinatario  = destinatario;
        this.estado        = EstadoPaquete.RECIBIDO;
        this.fechaCreacion = LocalDateTime.now();
        this.historial     = new ArrayList<>();
    }

    // ── Métodos clave ──────────────────────────────────────────
    /**
     * Calcula costo usando la ruta asignada.
     * Si no hay ruta, aplica tarifa mínima por peso.
     */
    public double calcularCosto() {
        if (ruta != null) {
            costoEnvio = ruta.calcularCostoRuta(peso);
        } else {
            costoEnvio = peso * 3.00; // tarifa mínima sin ruta
        }
        return costoEnvio;
    }

    /**
     * Cambia estado si la transición es válida (RN-02, RN-08).
     * Registra automáticamente en el historial (RN-09).
     */
    public void cambiarEstado(EstadoPaquete nuevoEstado,
                              String observacion,
                              String usuarioResponsable) {
        if (estaFinalizado()) {
            throw new IllegalStateException(
                "El paquete ya está en estado final: " + this.estado);
        }
        this.estado = nuevoEstado;
        historial.add(new HistorialEstado(nuevoEstado, observacion, usuarioResponsable));
    }

    public double getVolumen() { return largo * ancho * alto; }

    public boolean estaFinalizado() {
        return estado == EstadoPaquete.ENTREGADO ||
               estado == EstadoPaquete.DEVUELTO;
    }

    public HistorialEstado getUltimoEstado() {
        if (historial.isEmpty()) return null;
        return historial.get(historial.size() - 1);
    }

    @Override
    public String toString() {
        return "[" + codigoPaquete + "] " + descripcion +
               " | " + peso + "kg | Estado: " + estado;
    }

    // ── Getters y Setters ──────────────────────────────────────
    public int    getId()                     { return id; }
    public void   setId(int v)                { this.id = v; }

    public String getCodigoPaquete()          { return codigoPaquete; }
    public void   setCodigoPaquete(String v)  { this.codigoPaquete = v; }

    public String getDescripcion()            { return descripcion; }
    public void   setDescripcion(String v)    { this.descripcion = v; }

    public double getPeso()                   { return peso; }
    public void   setPeso(double v)           { this.peso = v; }

    public double getLargo()                  { return largo; }
    public void   setLargo(double v)          { this.largo = v; }

    public double getAncho()                  { return ancho; }
    public void   setAncho(double v)          { this.ancho = v; }

    public double getAlto()                   { return alto; }
    public void   setAlto(double v)           { this.alto = v; }

    public EstadoPaquete getEstado()          { return estado; }
    public void          setEstado(EstadoPaquete v) { this.estado = v; }

    public Cliente    getRemitente()          { return remitente; }
    public void       setRemitente(Cliente v) { this.remitente = v; }

    public Persona    getDestinatario()         { return destinatario; }
    public void       setDestinatario(Persona v){ this.destinatario = v; }

    public Repartidor getRepartidor()           { return repartidor; }
    public void       setRepartidor(Repartidor v){ this.repartidor = v; }

    public Ruta   getRuta()                   { return ruta; }
    public void   setRuta(Ruta v)             { this.ruta = v; }

    public ArrayList<HistorialEstado> getHistorial()  { return historial; }
    public void setHistorial(ArrayList<HistorialEstado> v) { this.historial = v; }

    public LocalDateTime getFechaCreacion()          { return fechaCreacion; }
    public void          setFechaCreacion(LocalDateTime v){ this.fechaCreacion = v; }

    public double getCostoEnvio()             { return costoEnvio; }
    public void   setCostoEnvio(double v)     { this.costoEnvio = v; }
}
