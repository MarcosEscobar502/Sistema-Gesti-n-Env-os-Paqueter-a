package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para obtener la conexión a MySQL.
 * Patrón Singleton — una sola conexión activa por sesión.
 */
public class Conexion {

    private static final String URL      = "jdbc:mysql://localhost:3306/packtrack_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "patito123"; //  contraseña MySQL

    private static Connection instancia = null;

    // Constructor privado — no se instancia directamente
    private Conexion() {}

    /**
     * Retorna la conexión activa. Si no existe, la crea.
     */
    public static Connection getConexion() throws SQLException {
        try {
            if (instancia == null || instancia.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instancia = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado. Agrega mysql-connector-j al proyecto.", e);
        }
        return instancia;
    }

    /**
     * Cierra la conexión activa.
     */
    public static void cerrarConexion() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
