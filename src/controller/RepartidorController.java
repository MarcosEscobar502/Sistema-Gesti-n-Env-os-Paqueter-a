package controller;

import dao.RepartidorDAO;
import dao.RutaDAO;
import model.Repartidor;
import model.Ruta;

import java.sql.SQLException;
import java.util.ArrayList;

public class RepartidorController {

    private final RepartidorDAO dao    = new RepartidorDAO();
    private final RutaDAO       rutaDao = new RutaDAO();

    public boolean registrarRepartidor(Repartidor r) {
        try {
            return dao.insertar(r);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al registrar repartidor: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarRepartidor(Repartidor r) {
        try {
            return dao.actualizar(r);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al actualizar repartidor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarRepartidor(int id) {
        try {
            return dao.eliminar(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al eliminar repartidor: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Repartidor> obtenerTodos() {
        try { return dao.listarTodos(); }
        catch (SQLException e) { System.err.println("Error: " + e.getMessage()); return new ArrayList<>(); }
    }

    public ArrayList<Repartidor> obtenerDisponibles() {
        try { return dao.listarDisponibles(); }
        catch (SQLException e) { System.err.println("Error: " + e.getMessage()); return new ArrayList<>(); }
    }

    /** Asigna ruta a repartidor y lo marca no disponible (RN-04) */
    public boolean asignarRuta(int idRepartidor, int idRuta) {
        try {
            Repartidor rep  = dao.buscarPorId(idRepartidor);
            Ruta       ruta = rutaDao.buscarPorId(idRuta);
            if (rep == null)  { System.err.println("Repartidor no encontrado"); return false; }
            if (ruta == null) { System.err.println("Ruta no encontrada"); return false; }
            if (!rep.estaDisponible()) { System.err.println("El repartidor no está disponible"); return false; }
            rep.asignarRuta(ruta);
            return dao.actualizar(rep);
        } catch (SQLException e) {
            System.err.println("Error BD al asignar ruta: " + e.getMessage());
            return false;
        }
    }
}
