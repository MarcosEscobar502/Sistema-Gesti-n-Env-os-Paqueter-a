package dao;

import model.Cliente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * DAO de Cliente — CRUD completo con PreparedStatement.
 * Nunca usa concatenación de strings para SQL (código seguro).
 */
public class ClienteDAO {

    // ── INSERT ─────────────────────────────────────────────────
    public boolean insertar(Cliente c) throws SQLException {
        Connection conn = Conexion.getConexion();
        // Validaciones de entrada
        if (c.getNombre()  == null || c.getNombre().isBlank())  throw new IllegalArgumentException("Nombre requerido");
        if (c.getEmail()   == null || c.getEmail().isBlank())   throw new IllegalArgumentException("Email requerido");
        if (c.getTelefono()== null || c.getTelefono().isBlank())throw new IllegalArgumentException("Teléfono requerido");
        if (c.getCodigoCliente() == null || c.getCodigoCliente().isBlank()) throw new IllegalArgumentException("Código requerido");

        conn.setAutoCommit(false);
        try {
            // 1. Insertar en personas
            String sqlPersona = "INSERT INTO personas (nombre, telefono, email, direccion, tipo) VALUES (?, ?, ?, ?, 'CLIENTE')";
            try (PreparedStatement ps = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getNombre());
                ps.setString(2, c.getTelefono());
                ps.setString(3, c.getEmail());
                ps.setString(4, c.getDireccion());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) c.setId(rs.getInt(1));
                }
            }
            // 2. Insertar en clientes
            String sqlCliente = "INSERT INTO clientes (id, codigo_cliente, fecha_registro) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlCliente)) {
                ps.setInt(1,    c.getId());
                ps.setString(2, c.getCodigoCliente());
                ps.setDate(3,   Date.valueOf(c.getFechaRegistro() != null
                                             ? c.getFechaRegistro() : LocalDate.now()));
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ── UPDATE ─────────────────────────────────────────────────
    public boolean actualizar(Cliente c) throws SQLException {
        if (c.getId() <= 0) throw new IllegalArgumentException("ID inválido para actualizar");

        Connection conn = Conexion.getConexion();
        String sqlPersona = "UPDATE personas SET nombre=?, telefono=?, email=?, direccion=? WHERE id=?";
        String sqlCliente = "UPDATE clientes SET codigo_cliente=?, fecha_registro=? WHERE id=?";

        conn.setAutoCommit(false);
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlPersona)) {
                ps.setString(1, c.getNombre());
                ps.setString(2, c.getTelefono());
                ps.setString(3, c.getEmail());
                ps.setString(4, c.getDireccion());
                ps.setInt(5,    c.getId());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlCliente)) {
                ps.setString(1, c.getCodigoCliente());
                ps.setDate(2,   Date.valueOf(c.getFechaRegistro()));
                ps.setInt(3,    c.getId());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ── DELETE ─────────────────────────────────────────────────
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("ID inválido para eliminar");
        // CASCADE en FK elimina también el registro en clientes
        String sql = "DELETE FROM personas WHERE id=?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── SELECT ALL ─────────────────────────────────────────────
    public ArrayList<Cliente> listarTodos() throws SQLException {
        ArrayList<Cliente> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.telefono, p.email, p.direccion, " +
                     "       c.codigo_cliente, c.fecha_registro " +
                     "FROM personas p " +
                     "INNER JOIN clientes c ON p.id = c.id " +
                     "ORDER BY p.nombre";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── SELECT BY ID ───────────────────────────────────────────
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id, p.nombre, p.telefono, p.email, p.direccion, " +
                     "       c.codigo_cliente, c.fecha_registro " +
                     "FROM personas p INNER JOIN clientes c ON p.id = c.id " +
                     "WHERE p.id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ── SELECT BY CODIGO ───────────────────────────────────────
    public Cliente buscarPorCodigo(String codigo) throws SQLException {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Código requerido");
        String sql = "SELECT p.id, p.nombre, p.telefono, p.email, p.direccion, " +
                     "       c.codigo_cliente, c.fecha_registro " +
                     "FROM personas p INNER JOIN clientes c ON p.id = c.id " +
                     "WHERE c.codigo_cliente = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ── MAPPER ─────────────────────────────────────────────────
    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("telefono"),
            rs.getString("email"),
            rs.getString("direccion"),
            rs.getString("codigo_cliente"),
            rs.getDate("fecha_registro").toLocalDate()
        );
    }
}
