package dao;

import model.Ruta;

import java.sql.*;
import java.util.ArrayList;

public class RutaDAO {

    // ── INSERT ─────────────────────────────────────────────────
    public boolean insertar(Ruta r) throws SQLException {
        if (r.getCodigoRuta() == null || r.getCodigoRuta().isBlank()) throw new IllegalArgumentException("Código de ruta requerido");
        if (r.getDistanciaKm() <= 0) throw new IllegalArgumentException("Distancia debe ser mayor a 0");
        if (r.getTarifaBase()  <  0) throw new IllegalArgumentException("Tarifa no puede ser negativa");

        String sql = "INSERT INTO rutas (codigo_ruta, nombre, origen, destino, distancia_km, tarifa_base, activa) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getCodigoRuta());
            ps.setString(2, r.getNombre());
            ps.setString(3, r.getOrigen());
            ps.setString(4, r.getDestino());
            ps.setDouble(5, r.getDistanciaKm());
            ps.setDouble(6, r.getTarifaBase());
            ps.setBoolean(7, r.isActiva());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
            return true;
        }
    }

    // ── UPDATE ─────────────────────────────────────────────────
    public boolean actualizar(Ruta r) throws SQLException {
        if (r.getId() <= 0) throw new IllegalArgumentException("ID inválido");
        String sql = "UPDATE rutas SET nombre=?, origen=?, destino=?, distancia_km=?, tarifa_base=?, activa=? WHERE id=?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.setString(2, r.getOrigen());
            ps.setString(3, r.getDestino());
            ps.setDouble(4, r.getDistanciaKm());
            ps.setDouble(5, r.getTarifaBase());
            ps.setBoolean(6, r.isActiva());
            ps.setInt(7, r.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ─────────────────────────────────────────────────
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        String sql = "DELETE FROM rutas WHERE id=?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── SELECT ALL ─────────────────────────────────────────────
    public ArrayList<Ruta> listarTodos() throws SQLException {
        ArrayList<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutas ORDER BY nombre";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── SELECT ACTIVAS ─────────────────────────────────────────
    public ArrayList<Ruta> listarActivas() throws SQLException {
        ArrayList<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutas WHERE activa = 1 ORDER BY nombre";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── SELECT BY ID ───────────────────────────────────────────
    public Ruta buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM rutas WHERE id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ── MAPPER ─────────────────────────────────────────────────
    private Ruta mapear(ResultSet rs) throws SQLException {
        return new Ruta(
            rs.getInt("id"),
            rs.getString("codigo_ruta"),
            rs.getString("nombre"),
            rs.getString("origen"),
            rs.getString("destino"),
            rs.getDouble("distancia_km"),
            rs.getDouble("tarifa_base"),
            rs.getBoolean("activa")
        );
    }
}
