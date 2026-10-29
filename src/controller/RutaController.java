package controller;

import dao.RutaDAO;
import model.Ruta;

import java.sql.SQLException;
import java.util.ArrayList;

public class RutaController {

    private final RutaDAO dao = new RutaDAO();

    public boolean registrarRuta(Ruta r) {
        try { return dao.insertar(r); }
        catch (IllegalArgumentException e) { System.err.println("Validación: " + e.getMessage()); return false; }
        catch (SQLException e)             { System.err.println("Error BD: " + e.getMessage()); return false; }
    }

    public boolean actualizarRuta(Ruta r) {
        try { return dao.actualizar(r); }
        catch (IllegalArgumentException e) { System.err.println("Validación: " + e.getMessage()); return false; }
        catch (SQLException e)             { System.err.println("Error BD: " + e.getMessage()); return false; }
    }

    public boolean eliminarRuta(int id) {
        try { return dao.eliminar(id); }
        catch (IllegalArgumentException e) { System.err.println("Validación: " + e.getMessage()); return false; }
        catch (SQLException e)             { System.err.println("Error BD: " + e.getMessage()); return false; }
    }

    public ArrayList<Ruta> obtenerTodas() {
        try { return dao.listarTodos(); }
        catch (SQLException e) { System.err.println("Error BD: " + e.getMessage()); return new ArrayList<>(); }
    }

    public ArrayList<Ruta> obtenerActivas() {
        try { return dao.listarActivas(); }
        catch (SQLException e) { System.err.println("Error BD: " + e.getMessage()); return new ArrayList<>(); }
    }

    public Ruta buscarPorId(int id) {
        try { return dao.buscarPorId(id); }
        catch (SQLException e) { System.err.println("Error BD: " + e.getMessage()); return null; }
    }
}
