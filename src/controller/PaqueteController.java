package controller;

import dao.PaqueteDAO;
import model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class PaqueteController {

    private final PaqueteDAO dao = new PaqueteDAO();

    public boolean registrarPaquete(Paquete p) {
        try {
            p.calcularCosto();
            return dao.insertar(p);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al registrar paquete: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPaquete(Paquete p) {
        try {
            return dao.actualizar(p);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al actualizar paquete: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int idPaquete, EstadoPaquete nuevoEstado,
                                 String observacion, String usuarioResp) {
        try {
            return dao.actualizarEstado(idPaquete, nuevoEstado, observacion, usuarioResp);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPaquete(int id) {
        try {
            return dao.eliminar(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al eliminar paquete: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Paquete> obtenerTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error BD al listar paquetes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Paquete buscarPorCodigo(String codigo) {
        try {
            return dao.buscarPorCodigo(codigo);
        } catch (IllegalArgumentException | SQLException e) {
            System.err.println("Error al buscar paquete: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Paquete> listarPorEstado(EstadoPaquete estado) {
        try {
            return dao.listarPorEstado(estado);
        } catch (SQLException e) {
            System.err.println("Error BD al listar por estado: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public ArrayList<HistorialEstado> getHistorial(int idPaquete) {
        try {
            return dao.getHistorial(idPaquete);
        } catch (SQLException e) {
            System.err.println("Error BD al obtener historial: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
