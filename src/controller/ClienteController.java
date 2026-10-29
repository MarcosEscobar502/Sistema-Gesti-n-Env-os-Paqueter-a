package controller;

import dao.ClienteDAO;
import model.Cliente;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller de Cliente — une la vista con el DAO.
 * Maneja errores con try-catch y retorna mensajes claros.
 */
public class ClienteController {

    private final ClienteDAO dao = new ClienteDAO();

    public boolean registrarCliente(Cliente c) {
        try {
            if (dao.buscarPorCodigo(c.getCodigoCliente()) != null) {
                System.err.println("Error: ya existe un cliente con ese código.");
                return false;
            }
            return dao.insertar(c);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al registrar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarCliente(Cliente c) {
        try {
            return dao.actualizar(c);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCliente(int id) {
        try {
            return dao.eliminar(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Validación: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error BD al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Cliente> obtenerTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException e) {
            System.err.println("Error BD al listar clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Cliente buscarPorCodigo(String codigo) {
        try {
            return dao.buscarPorCodigo(codigo);
        } catch (IllegalArgumentException | SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
            return null;
        }
    }

    public Cliente buscarPorId(int id) {
        try {
            return dao.buscarPorId(id);
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
            return null;
        }
    }
}
