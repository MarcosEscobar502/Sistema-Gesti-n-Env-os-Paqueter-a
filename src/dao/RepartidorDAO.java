package dao;

import model.Repartidor;

import java.sql.*;
import java.util.ArrayList;

public class RepartidorDAO {

    // ── INSERT ─────────────────────────────────────────────────
    public boolean insertar(Repartidor r) throws SQLException {
        if (r.getNombre()  == null || r.getNombre().isBlank())  throw new IllegalArgumentException("Nombre requerido");
        if (r.getLicencia()== null || r.getLicencia().isBlank())throw new IllegalArgumentException("Licencia requerida");

        Connection conn = Conexion.getConexion();
        conn.setAutoCommit(false);
        try {
            String sqlP = "INSERT INTO personas (nombre, telefono, email, direccion, tipo) VALUES (?,?,?,?,'REPARTIDOR')";
            try (PreparedStatement ps = conn.prepareStatement(sqlP, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, r.getNombre());
                ps.setString(2, r.getTelefono());
                ps.setString(3, r.getEmail());
                ps.setString(4, r.getDireccion());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) r.setId(rs.getInt(1));
                }
            }
            String sqlR = "INSERT INTO repartidores (id, codigo_repartidor, licencia, disponible) VALUES (?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlR)) {
                ps.setInt(1,     r.getId());
                ps.setString(2,  r.getCodigoRepartidor());
                ps.setString(3,  r.getLicencia());
                ps.setBoolean(4, r.isDisponible());
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
    public boolean actualizar(Repartidor r) throws SQLException {
        if (r.getId() <= 0) throw new IllegalArgumentException("ID inválido");
        Connection conn = Conexion.getConexion();
        conn.setAutoCommit(false);
        try {
            String sqlP = "UPDATE personas SET nombre=?, telefono=?, email=?, direccion=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlP)) {
                ps.setString(1, r.getNombre());
                ps.setString(2, r.getTelefono());
                ps.setString(3, r.getEmail());
                ps.setString(4, r.getDireccion());
                ps.setInt(5,    r.getId());
                ps.executeUpdate();
            }
            String sqlR = "UPDATE repartidores SET licencia=?, disponible=?, id_ruta_actual=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlR)) {
                ps.setString(1,  r.getLicencia());
                ps.setBoolean(2, r.isDisponible());
                if (r.getRutaActual() != null) ps.setInt(3, r.getRutaActual().getId());
                else                           ps.setNull(3, Types.INTEGER);
                ps.setInt(4, r.getId());
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
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        String sql = "DELETE FROM personas WHERE id=?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── SELECT ALL ─────────────────────────────────────────────
    public ArrayList<Repartidor> listarTodos() throws SQLException {
        ArrayList<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.telefono, p.email, p.direccion, " +
                     "       r.codigo_repartidor, r.licencia, r.disponible " +
                     "FROM personas p INNER JOIN repartidores r ON p.id = r.id " +
                     "ORDER BY p.nombre";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── SELECT DISPONIBLES ─────────────────────────────────────
    public ArrayList<Repartidor> listarDisponibles() throws SQLException {
        ArrayList<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.telefono, p.email, p.direccion, " +
                     "       r.codigo_repartidor, r.licencia, r.disponible " +
                     "FROM personas p INNER JOIN repartidores r ON p.id = r.id " +
                     "WHERE r.disponible = 1 ORDER BY p.nombre";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── SELECT BY ID ───────────────────────────────────────────
    public Repartidor buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id, p.nombre, p.telefono, p.email, p.direccion, " +
                     "       r.codigo_repartidor, r.licencia, r.disponible " +
                     "FROM personas p INNER JOIN repartidores r ON p.id = r.id " +
                     "WHERE p.id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ── MAPPER ─────────────────────────────────────────────────
    private Repartidor mapear(ResultSet rs) throws SQLException {
        return new Repartidor(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("telefono"),
            rs.getString("email"),
            rs.getString("direccion"),
            rs.getString("codigo_repartidor"),
            rs.getString("licencia"),
            rs.getBoolean("disponible")
        );
    }
}
