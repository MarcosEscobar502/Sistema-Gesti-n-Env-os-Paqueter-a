package dao;

import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PaqueteDAO {

    // ── INSERT ─────────────────────────────────────────────────
    public boolean insertar(Paquete p) throws SQLException {
        if (p.getCodigoPaquete() == null || p.getCodigoPaquete().isBlank()) throw new IllegalArgumentException("Código de paquete requerido");
        if (p.getPeso() <= 0)  throw new IllegalArgumentException("Peso debe ser mayor a 0");
        if (p.getLargo()<= 0 || p.getAncho() <= 0 || p.getAlto() <= 0) throw new IllegalArgumentException("Dimensiones inválidas");
        if (p.getRemitente() == null) throw new IllegalArgumentException("Remitente requerido");
        if (p.getDestinatario() == null) throw new IllegalArgumentException("Destinatario requerido");

        String sql = "INSERT INTO paquetes (codigo_paquete, descripcion, peso, largo, ancho, alto, " +
                     "estado, id_remitente, id_destinatario, id_repartidor, id_ruta, id_orden, " +
                     "fecha_creacion, costo_envio) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  p.getCodigoPaquete());
            ps.setString(2,  p.getDescripcion());
            ps.setDouble(3,  p.getPeso());
            ps.setDouble(4,  p.getLargo());
            ps.setDouble(5,  p.getAncho());
            ps.setDouble(6,  p.getAlto());
            ps.setString(7,  p.getEstado().name());
            ps.setInt(8,     p.getRemitente().getId());
            ps.setInt(9,     p.getDestinatario().getId());
            if (p.getRepartidor() != null) ps.setInt(10, p.getRepartidor().getId());
            else                           ps.setNull(10, Types.INTEGER);
            if (p.getRuta() != null) ps.setInt(11, p.getRuta().getId());
            else                     ps.setNull(11, Types.INTEGER);
            ps.setNull(12, Types.INTEGER); // id_orden se asigna al crear la orden
            ps.setTimestamp(13, Timestamp.valueOf(p.getFechaCreacion() != null
                                                   ? p.getFechaCreacion() : LocalDateTime.now()));
            ps.setDouble(14, p.getCostoEnvio());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
            return true;
        }
    }

    // ── UPDATE ─────────────────────────────────────────────────
    public boolean actualizar(Paquete p) throws SQLException {
        if (p.getId() <= 0) throw new IllegalArgumentException("ID inválido");
        String sql = "UPDATE paquetes SET descripcion=?, peso=?, largo=?, ancho=?, alto=?, " +
                     "estado=?, id_repartidor=?, id_ruta=?, costo_envio=? WHERE id=?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, p.getDescripcion());
            ps.setDouble(2, p.getPeso());
            ps.setDouble(3, p.getLargo());
            ps.setDouble(4, p.getAncho());
            ps.setDouble(5, p.getAlto());
            ps.setString(6, p.getEstado().name());
            if (p.getRepartidor() != null) ps.setInt(7, p.getRepartidor().getId());
            else                           ps.setNull(7, Types.INTEGER);
            if (p.getRuta() != null) ps.setInt(8, p.getRuta().getId());
            else                     ps.setNull(8, Types.INTEGER);
            ps.setDouble(9,  p.getCostoEnvio());
            ps.setInt(10,    p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── UPDATE ESTADO (RN-02, RN-08) ───────────────────────────
    public boolean actualizarEstado(int idPaquete, EstadoPaquete nuevoEstado,
                                    String observacion, String usuarioResp) throws SQLException {
        if (idPaquete <= 0) throw new IllegalArgumentException("ID de paquete inválido");
        if (nuevoEstado == null) throw new IllegalArgumentException("Estado requerido");

        Connection conn = Conexion.getConexion();
        conn.setAutoCommit(false);
        try {
            // Verificar que no esté finalizado (RN-08)
            String sqlCheck = "SELECT estado FROM paquetes WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCheck)) {
                ps.setInt(1, idPaquete);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String estadoActual = rs.getString("estado");
                        if ("ENTREGADO".equals(estadoActual) || "DEVUELTO".equals(estadoActual)) {
                            throw new IllegalStateException("El paquete ya está en estado final: " + estadoActual);
                        }
                    }
                }
            }
            // Actualizar estado
            String sqlUpd = "UPDATE paquetes SET estado=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                ps.setString(1, nuevoEstado.name());
                ps.setInt(2, idPaquete);
                ps.executeUpdate();
            }
            // Registrar historial (RN-09)
            String sqlHist = "INSERT INTO historial_estados (id_paquete, estado, fecha, observacion, usuario_responsable) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlHist)) {
                ps.setInt(1,       idPaquete);
                ps.setString(2,    nuevoEstado.name());
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(4,    observacion);
                ps.setString(5,    usuarioResp);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            conn.rollback();
            throw new SQLException(e.getMessage(), e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ── DELETE ─────────────────────────────────────────────────
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        String sql = "DELETE FROM paquetes WHERE id=?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── SELECT ALL ─────────────────────────────────────────────
    public ArrayList<Paquete> listarTodos() throws SQLException {
        ArrayList<Paquete> lista = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "  cp.nombre AS rem_nombre, cp.telefono AS rem_tel, cp.email AS rem_email, cp.direccion AS rem_dir, " +
                     "  c.codigo_cliente, " +
                     "  dp.nombre AS dest_nombre, dp.telefono AS dest_tel, dp.email AS dest_email, dp.direccion AS dest_dir " +
                     "FROM paquetes p " +
                     "INNER JOIN clientes c ON p.id_remitente = c.id " +
                     "INNER JOIN personas cp ON c.id = cp.id " +
                     "INNER JOIN personas dp ON p.id_destinatario = dp.id " +
                     "ORDER BY p.fecha_creacion DESC";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ── SELECT BY CODIGO ───────────────────────────────────────
    public Paquete buscarPorCodigo(String codigo) throws SQLException {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Código requerido");
        String sql = "SELECT p.*, " +
                     "  cp.nombre AS rem_nombre, cp.telefono AS rem_tel, cp.email AS rem_email, cp.direccion AS rem_dir, " +
                     "  c.codigo_cliente, " +
                     "  dp.nombre AS dest_nombre, dp.telefono AS dest_tel, dp.email AS dest_email, dp.direccion AS dest_dir " +
                     "FROM paquetes p " +
                     "INNER JOIN clientes c ON p.id_remitente = c.id " +
                     "INNER JOIN personas cp ON c.id = cp.id " +
                     "INNER JOIN personas dp ON p.id_destinatario = dp.id " +
                     "WHERE p.codigo_paquete = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ── SELECT BY ESTADO ───────────────────────────────────────
    public ArrayList<Paquete> listarPorEstado(EstadoPaquete estado) throws SQLException {
        ArrayList<Paquete> lista = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "  cp.nombre AS rem_nombre, cp.telefono AS rem_tel, cp.email AS rem_email, cp.direccion AS rem_dir, " +
                     "  c.codigo_cliente, " +
                     "  dp.nombre AS dest_nombre, dp.telefono AS dest_tel, dp.email AS dest_email, dp.direccion AS dest_dir " +
                     "FROM paquetes p " +
                     "INNER JOIN clientes c ON p.id_remitente = c.id " +
                     "INNER JOIN personas cp ON c.id = cp.id " +
                     "INNER JOIN personas dp ON p.id_destinatario = dp.id " +
                     "WHERE p.estado = ? ORDER BY p.fecha_creacion DESC";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, estado.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── HISTORIAL ──────────────────────────────────────────────
    public ArrayList<model.HistorialEstado> getHistorial(int idPaquete) throws SQLException {
        ArrayList<model.HistorialEstado> lista = new ArrayList<>();
        String sql = "SELECT * FROM historial_estados WHERE id_paquete = ? ORDER BY fecha ASC";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPaquete);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new model.HistorialEstado(
                        rs.getInt("id"),
                        EstadoPaquete.valueOf(rs.getString("estado")),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getString("observacion"),
                        rs.getString("usuario_responsable")
                    ));
                }
            }
        }
        return lista;
    }

    // ── MAPPER ─────────────────────────────────────────────────
    private Paquete mapear(ResultSet rs) throws SQLException {
        Cliente remitente = new Cliente(
            rs.getInt("id_remitente"),
            rs.getString("rem_nombre"),
            rs.getString("rem_tel"),
            rs.getString("rem_email"),
            rs.getString("rem_dir"),
            rs.getString("codigo_cliente"),
            null
        );
        Persona destinatario = new Cliente(); // se instancia como Persona base
        destinatario.setId(rs.getInt("id_destinatario"));
        destinatario.setNombre(rs.getString("dest_nombre"));
        destinatario.setTelefono(rs.getString("dest_tel"));
        destinatario.setEmail(rs.getString("dest_email"));
        destinatario.setDireccion(rs.getString("dest_dir"));

        Paquete p = new Paquete(
            rs.getInt("id"),
            rs.getString("codigo_paquete"),
            rs.getString("descripcion"),
            rs.getDouble("peso"),
            rs.getDouble("largo"),
            rs.getDouble("ancho"),
            rs.getDouble("alto"),
            EstadoPaquete.valueOf(rs.getString("estado")),
            remitente,
            destinatario,
            rs.getTimestamp("fecha_creacion").toLocalDateTime(),
            rs.getDouble("costo_envio")
        );
        return p;
    }
}
