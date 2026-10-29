import controller.*;
import dao.Conexion;
import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * PackTrack — Punto de entrada principal.
 * Menú de consola que demuestra el CRUD completo contra MySQL.
 * La vista gráfica (Swing) se integra en la Fase 3.
 */
public class Main {

    static final Scanner sc = new Scanner(System.in);
    static final UsuarioController    usuarioCtrl    = new UsuarioController();
    static final ClienteController    clienteCtrl    = new ClienteController();
    static final RepartidorController repartidorCtrl = new RepartidorController();
    static final RutaController       rutaCtrl       = new RutaController();
    static final PaqueteController    paqueteCtrl    = new PaqueteController();

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   PackTrack — Gestión de Envíos v1.0     ");
        System.out.println("===========================================");

        // ── LOGIN ──────────────────────────────────────────────
        Usuario u = null;
        while (u == null) {
            System.out.print("Usuario: ");
            String user = sc.nextLine().trim();
            System.out.print("Password: ");
            String pass = sc.nextLine().trim();
            u = usuarioCtrl.login(user, pass);
            if (u == null) System.out.println("Credenciales incorrectas. Intente de nuevo.\n");
        }
        System.out.println("Bienvenido: " + u.getUsername() + " [" + u.getRol() + "]\n");

        // ── MENÚ PRINCIPAL ─────────────────────────────────────
        boolean salir = false;
        while (!salir) {
            System.out.println("\n──────── MENÚ PRINCIPAL ────────");
            System.out.println("1. Gestión de Clientes");
            System.out.println("2. Gestión de Paquetes / Envíos");
            System.out.println("3. Gestión de Rutas");
            if (usuarioCtrl.tienePermiso("GESTIONAR_REPARTIDORES"))
                System.out.println("4. Gestión de Repartidores  [ADMIN]");
            System.out.println("0. Cerrar sesión y salir");
            System.out.print("Opción: ");

            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> menuClientes();
                case "2" -> menuPaquetes();
                case "3" -> menuRutas();
                case "4" -> {
                    if (usuarioCtrl.tienePermiso("GESTIONAR_REPARTIDORES")) menuRepartidores();
                    else System.out.println("Sin permiso.");
                }
                case "0" -> { salir = true; usuarioCtrl.logout(); }
                default  -> System.out.println("Opción inválida.");
            }
        }
        Conexion.cerrarConexion();
        System.out.println("Sesión cerrada. ¡Hasta pronto!");
    }

    // ══ MENÚ CLIENTES ══════════════════════════════════════════
    static void menuClientes() {
        System.out.println("\n── Clientes ──");
        System.out.println("1. Listar todos  2. Buscar  3. Registrar  4. Actualizar  5. Eliminar  0. Volver");
        String op = sc.nextLine().trim();
        switch (op) {
            case "1" -> {
                ArrayList<Cliente> lista = clienteCtrl.obtenerTodos();
                if (lista.isEmpty()) System.out.println("No hay clientes registrados.");
                else lista.forEach(c -> System.out.println("  " + c));
            }
            case "2" -> {
                System.out.print("Código de cliente: ");
                Cliente c = clienteCtrl.buscarPorCodigo(sc.nextLine().trim());
                System.out.println(c != null ? "  " + c : "No encontrado.");
            }
            case "3" -> {
                System.out.print("Nombre: ");    String nombre = sc.nextLine().trim();
                System.out.print("Teléfono: ");  String tel    = sc.nextLine().trim();
                System.out.print("Email: ");     String email  = sc.nextLine().trim();
                System.out.print("Dirección: "); String dir    = sc.nextLine().trim();
                System.out.print("Código (ej. CLI-003): "); String cod = sc.nextLine().trim();
                Cliente c = new Cliente(nombre, tel, email, dir, cod);
                System.out.println(clienteCtrl.registrarCliente(c) ? "✔ Cliente registrado." : "✘ Error al registrar.");
            }
            case "4" -> {
                System.out.print("ID del cliente a actualizar: ");
                try {
                    int id = Integer.parseInt(sc.nextLine().trim());
                    Cliente c = clienteCtrl.buscarPorId(id);
                    if (c == null) { System.out.println("No encontrado."); break; }
                    System.out.print("Nuevo teléfono [" + c.getTelefono() + "]: "); String tel = sc.nextLine().trim();
                    System.out.print("Nueva dirección [" + c.getDireccion() + "]: "); String dir = sc.nextLine().trim();
                    if (!tel.isBlank()) c.setTelefono(tel);
                    if (!dir.isBlank()) c.setDireccion(dir);
                    System.out.println(clienteCtrl.actualizarCliente(c) ? "✔ Actualizado." : "✘ Error.");
                } catch (NumberFormatException e) { System.out.println("ID inválido."); }
            }
            case "5" -> {
                System.out.print("ID del cliente a eliminar: ");
                try {
                    int id = Integer.parseInt(sc.nextLine().trim());
                    System.out.println(clienteCtrl.eliminarCliente(id) ? "✔ Eliminado." : "✘ Error.");
                } catch (NumberFormatException e) { System.out.println("ID inválido."); }
            }
        }
    }

    // ══ MENÚ PAQUETES ══════════════════════════════════════════
    static void menuPaquetes() {
        System.out.println("\n── Paquetes ──");
        System.out.println("1. Listar todos  2. Buscar por código  3. Registrar  4. Cambiar estado  5. Ver historial  6. Listar por estado  0. Volver");
        String op = sc.nextLine().trim();
        switch (op) {
            case "1" -> {
                ArrayList<Paquete> lista = paqueteCtrl.obtenerTodos();
                if (lista.isEmpty()) System.out.println("No hay paquetes.");
                else lista.forEach(p -> System.out.println("  " + p));
            }
            case "2" -> {
                System.out.print("Código de paquete: ");
                Paquete p = paqueteCtrl.buscarPorCodigo(sc.nextLine().trim());
                System.out.println(p != null ? "  " + p : "No encontrado.");
            }
            case "3" -> {
                System.out.print("Código paquete (ej. PKG-001): "); String cod  = sc.nextLine().trim();
                System.out.print("Descripción: ");                  String desc = sc.nextLine().trim();
                System.out.print("Peso (kg): ");
                try {
                    double peso = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Largo cm: "); double largo = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Ancho cm: "); double ancho = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Alto  cm: "); double alto  = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Código de cliente remitente: ");
                    Cliente rem = clienteCtrl.buscarPorCodigo(sc.nextLine().trim());
                    if (rem == null) { System.out.println("Remitente no encontrado."); return; }
                    // Destinatario simple (Persona)
                    System.out.print("Nombre destinatario: "); String dNom = sc.nextLine().trim();
                    System.out.print("Tel destinatario: ");    String dTel = sc.nextLine().trim();
                    System.out.print("Email destinatario: ");  String dEml = sc.nextLine().trim();
                    System.out.print("Dir destinatario: ");    String dDir = sc.nextLine().trim();
                    // Creamos destinatario como Cliente temporal sin BD (se guardará con id_destinatario real en producción)
                    Cliente dest = new Cliente(dNom, dTel, dEml, dDir, "DEST-TMP");
                    dest.setId(rem.getId()); // placeholder; en Fase 3 el form buscará/creará el destinatario
                    Paquete p = new Paquete(cod, desc, peso, largo, ancho, alto, rem, dest);
                    System.out.println(paqueteCtrl.registrarPaquete(p)
                            ? "✔ Paquete registrado. Costo: Q" + String.format("%.2f", p.getCostoEnvio())
                            : "✘ Error al registrar.");
                } catch (NumberFormatException e) { System.out.println("Valor numérico inválido."); }
            }
            case "4" -> {
                System.out.print("ID del paquete: ");
                try {
                    int id = Integer.parseInt(sc.nextLine().trim());
                    System.out.println("Estados: 1=EN_TRANSITO  2=ENTREGADO  3=DEVUELTO");
                    System.out.print("Nuevo estado: ");
                    EstadoPaquete nuevo = switch (sc.nextLine().trim()) {
                        case "1" -> EstadoPaquete.EN_TRANSITO;
                        case "2" -> EstadoPaquete.ENTREGADO;
                        case "3" -> EstadoPaquete.DEVUELTO;
                        default  -> null;
                    };
                    if (nuevo == null) { System.out.println("Estado inválido."); return; }
                    System.out.print("Observación: "); String obs = sc.nextLine().trim();
                    System.out.println(paqueteCtrl.cambiarEstado(id, nuevo, obs,
                            usuarioCtrl.getUsuarioActual().getUsername())
                            ? "✔ Estado actualizado." : "✘ Error.");
                } catch (NumberFormatException e) { System.out.println("ID inválido."); }
            }
            case "5" -> {
                System.out.print("ID del paquete: ");
                try {
                    int id = Integer.parseInt(sc.nextLine().trim());
                    ArrayList<HistorialEstado> hist = paqueteCtrl.getHistorial(id);
                    if (hist.isEmpty()) System.out.println("Sin historial.");
                    else hist.forEach(h -> System.out.println("  " + h));
                } catch (NumberFormatException e) { System.out.println("ID inválido."); }
            }
            case "6" -> {
                System.out.println("1=RECIBIDO  2=EN_TRANSITO  3=ENTREGADO  4=DEVUELTO");
                System.out.print("Estado: ");
                EstadoPaquete est = switch (sc.nextLine().trim()) {
                    case "1" -> EstadoPaquete.RECIBIDO;
                    case "2" -> EstadoPaquete.EN_TRANSITO;
                    case "3" -> EstadoPaquete.ENTREGADO;
                    case "4" -> EstadoPaquete.DEVUELTO;
                    default  -> null;
                };
                if (est == null) { System.out.println("Estado inválido."); return; }
                paqueteCtrl.listarPorEstado(est).forEach(p -> System.out.println("  " + p));
            }
        }
    }

    // ══ MENÚ RUTAS ═════════════════════════════════════════════
    static void menuRutas() {
        System.out.println("\n── Rutas ──");
        System.out.println("1. Listar activas  2. Registrar  3. Desactivar  0. Volver");
        String op = sc.nextLine().trim();
        switch (op) {
            case "1" -> rutaCtrl.obtenerActivas().forEach(r -> System.out.println("  " + r));
            case "2" -> {
                System.out.print("Código (ej. RUT-003): "); String cod  = sc.nextLine().trim();
                System.out.print("Nombre: ");               String nom  = sc.nextLine().trim();
                System.out.print("Origen: ");               String orig = sc.nextLine().trim();
                System.out.print("Destino: ");              String dest = sc.nextLine().trim();
                try {
                    System.out.print("Distancia km: ");  double dist  = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Tarifa base Q: "); double tarifa = Double.parseDouble(sc.nextLine().trim());
                    Ruta r = new Ruta(cod, nom, orig, dest, dist, tarifa);
                    System.out.println(rutaCtrl.registrarRuta(r) ? "✔ Ruta registrada." : "✘ Error.");
                } catch (NumberFormatException e) { System.out.println("Valor numérico inválido."); }
            }
            case "3" -> {
                System.out.print("ID de la ruta a desactivar: ");
                try {
                    int id = Integer.parseInt(sc.nextLine().trim());
                    Ruta r = rutaCtrl.buscarPorId(id);
                    if (r == null) { System.out.println("No encontrada."); return; }
                    r.desactivar();
                    System.out.println(rutaCtrl.actualizarRuta(r) ? "✔ Ruta desactivada." : "✘ Error.");
                } catch (NumberFormatException e) { System.out.println("ID inválido."); }
            }
        }
    }

    // ══ MENÚ REPARTIDORES ══════════════════════════════════════
    static void menuRepartidores() {
        System.out.println("\n── Repartidores ──");
        System.out.println("1. Listar todos  2. Disponibles  3. Registrar  4. Asignar ruta  5. Eliminar  0. Volver");
        String op = sc.nextLine().trim();
        switch (op) {
            case "1" -> repartidorCtrl.obtenerTodos().forEach(r -> System.out.println("  " + r));
            case "2" -> repartidorCtrl.obtenerDisponibles().forEach(r -> System.out.println("  " + r));
            case "3" -> {
                System.out.print("Nombre: ");              String nom  = sc.nextLine().trim();
                System.out.print("Teléfono: ");            String tel  = sc.nextLine().trim();
                System.out.print("Email: ");               String eml  = sc.nextLine().trim();
                System.out.print("Dirección: ");           String dir  = sc.nextLine().trim();
                System.out.print("Código (ej. REP-002): ");String cod  = sc.nextLine().trim();
                System.out.print("Licencia: ");            String lic  = sc.nextLine().trim();
                Repartidor r = new Repartidor(nom, tel, eml, dir, cod, lic);
                System.out.println(repartidorCtrl.registrarRepartidor(r) ? "✔ Repartidor registrado." : "✘ Error.");
            }
            case "4" -> {
                System.out.print("ID repartidor: ");
                try {
                    int idRep  = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("ID ruta: ");
                    int idRuta = Integer.parseInt(sc.nextLine().trim());
                    System.out.println(repartidorCtrl.asignarRuta(idRep, idRuta) ? "✔ Ruta asignada." : "✘ Error.");
                } catch (NumberFormatException e) { System.out.println("ID inválido."); }
            }
            case "5" -> {
                System.out.print("ID del repartidor a eliminar: ");
                try {
                    int id = Integer.parseInt(sc.nextLine().trim());
                    System.out.println(repartidorCtrl.eliminarRepartidor(id) ? "✔ Eliminado." : "✘ Error.");
                } catch (NumberFormatException e) { System.out.println("ID inválido."); }
            }
        }
    }
}
