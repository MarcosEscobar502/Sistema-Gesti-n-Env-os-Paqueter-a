package model;

import java.util.ArrayList;

public class Empresa {

    private String              nombre;
    private String              ruc;
    private ArrayList<Repartidor> listaRepartidores;
    private ArrayList<Ruta>       listaRutas;
    private ArrayList<Cliente>    listaClientes;
    private ArrayList<Orden>      listaOrdenes;
    private ArrayList<Usuario>    listaUsuarios;

    // ── Constructor ────────────────────────────────────────────
    public Empresa(String nombre, String ruc) {
        this.nombre             = nombre;
        this.ruc                = ruc;
        this.listaRepartidores  = new ArrayList<>();
        this.listaRutas         = new ArrayList<>();
        this.listaClientes      = new ArrayList<>();
        this.listaOrdenes       = new ArrayList<>();
        this.listaUsuarios      = new ArrayList<>();
    }

    // ── Métodos clave ──────────────────────────────────────────
    public Paquete buscarPaquete(String codigo) {
        for (Orden o : listaOrdenes) {
            for (Paquete p : o.getListaPaquetes()) {
                if (p.getCodigoPaquete().equalsIgnoreCase(codigo)) return p;
            }
        }
        return null;
    }

    public Cliente buscarCliente(String codigoOEmail) {
        for (Cliente c : listaClientes) {
            if (c.getCodigoCliente().equalsIgnoreCase(codigoOEmail) ||
                c.getEmail().equalsIgnoreCase(codigoOEmail)) return c;
        }
        return null;
    }

    public Repartidor buscarRepartidor(String codigo) {
        for (Repartidor r : listaRepartidores) {
            if (r.getCodigoRepartidor().equalsIgnoreCase(codigo)) return r;
        }
        return null;
    }

    public ArrayList<Repartidor> getRepartidoresDisponibles() {
        ArrayList<Repartidor> disponibles = new ArrayList<>();
        for (Repartidor r : listaRepartidores) {
            if (r.estaDisponible()) disponibles.add(r);
        }
        return disponibles;
    }

    public String getEstadisticas() {
        int recibido = 0, transito = 0, entregado = 0, devuelto = 0;
        for (Orden o : listaOrdenes) {
            for (Paquete p : o.getListaPaquetes()) {
                switch (p.getEstado()) {
                    case RECIBIDO    -> recibido++;
                    case EN_TRANSITO -> transito++;
                    case ENTREGADO   -> entregado++;
                    case DEVUELTO    -> devuelto++;
                }
            }
        }
        return "RECIBIDO=" + recibido + " | EN_TRANSITO=" + transito +
               " | ENTREGADO=" + entregado + " | DEVUELTO=" + devuelto;
    }

    // ── Getters y Setters ──────────────────────────────────────
    public String getNombre()  { return nombre; }
    public String getRuc()     { return ruc; }

    public ArrayList<Repartidor> getListaRepartidores()          { return listaRepartidores; }
    public void setListaRepartidores(ArrayList<Repartidor> v)    { this.listaRepartidores = v; }

    public ArrayList<Ruta> getListaRutas()                       { return listaRutas; }
    public void setListaRutas(ArrayList<Ruta> v)                 { this.listaRutas = v; }

    public ArrayList<Cliente> getListaClientes()                 { return listaClientes; }
    public void setListaClientes(ArrayList<Cliente> v)           { this.listaClientes = v; }

    public ArrayList<Orden> getListaOrdenes()                    { return listaOrdenes; }
    public void setListaOrdenes(ArrayList<Orden> v)              { this.listaOrdenes = v; }

    public ArrayList<Usuario> getListaUsuarios()                 { return listaUsuarios; }
    public void setListaUsuarios(ArrayList<Usuario> v)           { this.listaUsuarios = v; }
}
