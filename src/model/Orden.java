package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Orden {

    private int               id;
    private String            codigoOrden;
    private LocalDateTime     fecha;
    private Cliente           cliente;
    private ArrayList<Paquete> listaPaquetes;
    private double            totalCosto;
    private boolean           pagado;

    // ── Constructores ──────────────────────────────────────────
    public Orden() {
        this.listaPaquetes = new ArrayList<>();
        this.fecha         = LocalDateTime.now();
        this.pagado        = false;
    }

    public Orden(int id, String codigoOrden, LocalDateTime fecha,
                 Cliente cliente, boolean pagado) {
        this.id            = id;
        this.codigoOrden   = codigoOrden;
        this.fecha         = fecha;
        this.cliente       = cliente;
        this.pagado        = pagado;
        this.listaPaquetes = new ArrayList<>();
    }

    public Orden(String codigoOrden, Cliente cliente) {
        this.codigoOrden   = codigoOrden;
        this.cliente       = cliente;
        this.fecha         = LocalDateTime.now();
        this.listaPaquetes = new ArrayList<>();
        this.pagado        = false;
    }

    // ── Métodos clave ──────────────────────────────────────────
    public void agregarPaquete(Paquete paquete) {
        if (paquete != null) {
            listaPaquetes.add(paquete);
            calcularTotal();
        }
    }

    public double calcularTotal() {
        totalCosto = 0;
        for (Paquete p : listaPaquetes) {
            totalCosto += p.getCostoEnvio();
        }
        return totalCosto;
    }

    public int getCantidadPaquetes() { return listaPaquetes.size(); }

    public void marcarPagado() { this.pagado = true; }

    @Override
    public String toString() {
        return "[" + codigoOrden + "] Cliente: " +
               (cliente != null ? cliente.getNombre() : "N/A") +
               " | Paquetes: " + getCantidadPaquetes() +
               " | Total: Q" + String.format("%.2f", totalCosto) +
               " | Pagado: " + (pagado ? "Sí" : "No");
    }

    // ── Getters y Setters ──────────────────────────────────────
    public int    getId()                   { return id; }
    public void   setId(int v)              { this.id = v; }

    public String getCodigoOrden()          { return codigoOrden; }
    public void   setCodigoOrden(String v)  { this.codigoOrden = v; }

    public LocalDateTime getFecha()         { return fecha; }
    public void          setFecha(LocalDateTime v){ this.fecha = v; }

    public Cliente getCliente()             { return cliente; }
    public void    setCliente(Cliente v)    { this.cliente = v; }

    public ArrayList<Paquete> getListaPaquetes()         { return listaPaquetes; }
    public void setListaPaquetes(ArrayList<Paquete> v)   { this.listaPaquetes = v; }

    public double  getTotalCosto()          { return totalCosto; }
    public void    setTotalCosto(double v)  { this.totalCosto = v; }

    public boolean isPagado()               { return pagado; }
    public void    setPagado(boolean v)     { this.pagado = v; }
}
