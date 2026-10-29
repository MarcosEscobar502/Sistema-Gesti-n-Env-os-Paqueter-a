package model;

public class Ruta {

    private int    id;
    private String codigoRuta;
    private String nombre;
    private String origen;
    private String destino;
    private double distanciaKm;
    private double tarifaBase;
    private boolean activa;

    // ── Constructores ──────────────────────────────────────────
    public Ruta() { this.activa = true; }

    public Ruta(int id, String codigoRuta, String nombre,
                String origen, String destino,
                double distanciaKm, double tarifaBase, boolean activa) {
        this.id           = id;
        this.codigoRuta   = codigoRuta;
        this.nombre       = nombre;
        this.origen       = origen;
        this.destino      = destino;
        this.distanciaKm  = distanciaKm;
        this.tarifaBase   = tarifaBase;
        this.activa       = activa;
    }

    public Ruta(String codigoRuta, String nombre,
                String origen, String destino,
                double distanciaKm, double tarifaBase) {
        this.codigoRuta  = codigoRuta;
        this.nombre      = nombre;
        this.origen      = origen;
        this.destino     = destino;
        this.distanciaKm = distanciaKm;
        this.tarifaBase  = tarifaBase;
        this.activa      = true;
    }

    // ── Métodos clave ──────────────────────────────────────────
    /**
     * Costo = (peso * tarifaBase) + (distanciaKm * 0.10)
     */
    public double calcularCostoRuta(double peso) {
        return (peso * tarifaBase) + (distanciaKm * 0.10);
    }

    public boolean estaActiva() { return activa; }

    public void desactivar() { this.activa = false; }

    @Override
    public String toString() {
        return "[" + codigoRuta + "] " + nombre +
               " (" + origen + " → " + destino +
               " | " + distanciaKm + " km)";
    }

    // ── Getters y Setters ──────────────────────────────────────
    public int     getId()                { return id; }
    public void    setId(int v)           { this.id = v; }

    public String  getCodigoRuta()        { return codigoRuta; }
    public void    setCodigoRuta(String v){ this.codigoRuta = v; }

    public String  getNombre()            { return nombre; }
    public void    setNombre(String v)    { this.nombre = v; }

    public String  getOrigen()            { return origen; }
    public void    setOrigen(String v)    { this.origen = v; }

    public String  getDestino()           { return destino; }
    public void    setDestino(String v)   { this.destino = v; }

    public double  getDistanciaKm()       { return distanciaKm; }
    public void    setDistanciaKm(double v){ this.distanciaKm = v; }

    public double  getTarifaBase()        { return tarifaBase; }
    public void    setTarifaBase(double v){ this.tarifaBase = v; }

    public boolean isActiva()             { return activa; }
    public void    setActiva(boolean v)   { this.activa = v; }
}
