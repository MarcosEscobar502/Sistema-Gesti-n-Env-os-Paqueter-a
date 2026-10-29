package dao;

import model.Operador;
import model.RolUsuario;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO {

    // ── INSERT ─────────────────────────────────────────────────
    public boolean insertar(Usuario u) throws SQLException {
        if (u.getUsername() == null || u.getUsername().isBlank()) throw new IllegalArgumentException("Username requerido");
        if (u.getPassword() == null || u.getPassword().isBlank()) throw new IllegalArgumentException("Password requerido");

        String sql = "INSERT INTO usuarios (username, password, rol, activo) VALUES (?,?,?,?)";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  u.getUsername());
            ps.setString(2,  u.getPassword());
            ps.setString(3,  u.getRol().name());
            ps.setBoolean(4, u.isActivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
            return true;
        }
    }

    // ── UPDATE ─────────────────────────────────────────────────
    public boolean actualizar(Usuario u) throws SQLException {
        if (u.getId() <= 0) throw new IllegalArgumentException("ID inválido");
        String sql = "UPDATE usuarios SET username=?, password=?, rol=?, activo=? WHERE id=?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1,  u.getUsername());
            ps.setString(2,  u.getPassword());
            ps.setString(3,  u.getRol().name());
            ps.setBoolean(4, u.isActivo());
            ps.setInt(5,     u.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ─────────────────────────────────────────────────
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── LOGIN ──────────────────────────────────────────────────
    public Usuario login(String username, String password) throws SQLException {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username requerido");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password requerido");

        String sql = "SELECT * FROM usuarios WHERE username=? AND password=? AND activo=1";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null; // credenciales inválidas
    }

    // ── SELECT ALL ─────────────────────────────────────────────
    public ArrayList<Usuario> listarTodos() throws SQLException {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY username";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── MAPPER ─────────────────────────────────────────────────
    private Usuario mapear(ResultSet rs) throws SQLException {
        RolUsuario rol = RolUsuario.valueOf(rs.getString("rol"));
        if (rol == RolUsuario.OPERADOR) {
            return new Operador(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getBoolean("activo"),
                "MAÑANA"
            );
        } else {
            // Administrador como Operador con rol ADMIN (para simplificar el login)
            return new Operador(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getBoolean("activo"),
                "ADMINISTRADOR"
            ) {
                @Override public RolUsuario getRol() { return RolUsuario.ADMINISTRADOR; }
                @Override public boolean tienePermiso(String a) { return isActivo(); }
            };
        }
    }
}
