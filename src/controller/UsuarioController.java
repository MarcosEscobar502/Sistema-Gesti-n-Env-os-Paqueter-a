package controller;

import dao.UsuarioDAO;
import model.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller de sesión y usuarios.
 * Mantiene el usuario actualmente logueado (RN-07).
 */
public class UsuarioController {

    private final UsuarioDAO dao = new UsuarioDAO();
    private Usuario usuarioActual = null;

    // ── LOGIN (RN-07) ──────────────────────────────────────────
    public Usuario login(String username, String password) {
        try {
            if (username == null || username.isBlank()) {
                System.err.println("Validación: Username requerido");
                return null;
            }
            if (password == null || password.isBlank()) {
                System.err.println("Validación: Password requerido");
                return null;
            }
            usuarioActual = dao.login(username, password);
            if (usuarioActual == null) {
                System.err.println("Credenciales incorrectas o usuario inactivo.");
            }
            return usuarioActual;
        } catch (SQLException e) {
            System.err.println("Error BD en login: " + e.getMessage());
            return null;
        }
    }

    public void logout() { usuarioActual = null; }

    public Usuario getUsuarioActual() { return usuarioActual; }

    public boolean estaLogueado() { return usuarioActual != null; }

    public boolean tienePermiso(String accion) {
        return usuarioActual != null && usuarioActual.tienePermiso(accion);
    }

    // ── CRUD USUARIOS ──────────────────────────────────────────
    public boolean registrarUsuario(Usuario u) {
        try { return dao.insertar(u); }
        catch (IllegalArgumentException e) { System.err.println("Validación: " + e.getMessage()); return false; }
        catch (SQLException e)             { System.err.println("Error BD: " + e.getMessage()); return false; }
    }

    public boolean actualizarUsuario(Usuario u) {
        try { return dao.actualizar(u); }
        catch (IllegalArgumentException e) { System.err.println("Validación: " + e.getMessage()); return false; }
        catch (SQLException e)             { System.err.println("Error BD: " + e.getMessage()); return false; }
    }

    public boolean eliminarUsuario(int id) {
        try { return dao.eliminar(id); }
        catch (IllegalArgumentException e) { System.err.println("Validación: " + e.getMessage()); return false; }
        catch (SQLException e)             { System.err.println("Error BD: " + e.getMessage()); return false; }
    }

    public ArrayList<Usuario> obtenerTodos() {
        try { return dao.listarTodos(); }
        catch (SQLException e) { System.err.println("Error BD: " + e.getMessage()); return new ArrayList<>(); }
    }
}
