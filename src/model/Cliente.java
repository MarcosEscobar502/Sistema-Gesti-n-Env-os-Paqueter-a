package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Cliente extends Persona {

    private String          codigoCliente;
    private LocalDate       fechaRegistro;
    private ArrayList<Orden> listaOrdenes;

    // ── Constructores ──────────────────────────────────────────
    public Cliente() {
        this.listaOrdenes = new ArrayList<>();
        this.fechaRegistro = LocalDate.now();
    }

    public Cliente(int id, String nombre, String telefono,
                   String email, String direccion,
                   String codigoCliente, LocalDate fechaRegistro) {
        super(id, nombre, telefono, email, direccion);
        this.codigoCliente = codigoCliente;
        this.fechaRegistro = fechaRegistro;
        this.listaOrdenes  = new ArrayList<>();
    }

    public Cliente(String nombre, String telefono,
                   String email, String direccion,
                   String codigoCliente) {
        super(nombre, telefono, email, direccion);
        this.codigoCliente = codigoCliente;
        this.fechaRegistro = LocalDate.now();
        this.listaOrdenes  = new ArrayList<>();
    }

    // ── Métodos clave ──────────────────────────────────────────
    public void agregarOrden(Orden orden) {
        if (orden != null) listaOrdenes.add(orden);
    }

    public ArrayList<Orden> getOrdenes() {
        return listaOrdenes;
    }

    public int getTotalEnvios() {
        return listaOrdenes.size();
    }

    @Override
    public String getDatosContacto() {
        return "[" + codigoCliente + "] " + super.getDatosContacto();
    }

    @Override
    public String toString() {
        return getDatosContacto();
    }

    // ── Getters y Setters ──────────────────────────────────────
    public String    getCodigoCliente()          { return codigoCliente; }
    public void      setCodigoCliente(String v)  { this.codigoCliente = v; }

    public LocalDate getFechaRegistro()           { return fechaRegistro; }
    public void      setFechaRegistro(LocalDate v){ this.fechaRegistro = v; }

    public void setListaOrdenes(ArrayList<Orden> v) { this.listaOrdenes = v; }
}
