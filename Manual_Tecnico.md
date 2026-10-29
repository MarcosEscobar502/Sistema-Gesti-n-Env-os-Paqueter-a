# Manual Técnico — PackTrack
## Sistema de Gestión de Envíos y Paquetería

**Curso:** Programación II | **Carrera:** Ingeniería en Sistemas
**Proyecto:** Fase 2 — Lógica de Negocio, Base de Datos y Arquitectura MVC

---

## Arquitectura General (MVC)

El proyecto sigue el patrón **Modelo–Vista–Controlador**:

```
src/
├── model/       ← datos y reglas de negocio (M)
├── dao/         ← acceso a base de datos      (M - capa de persistencia)
├── controller/  ← lógica que une modelo y vista (C)
└── Main.java    ← vista de consola (V) — en Fase 3 será Swing
```

---

## 1. Capa MODEL

### `EstadoPaquete.java` — Enumeración
**Por qué existe:** Un paquete solo puede estar en 4 estados definidos. Usar un `enum` evita que se asigne un valor inválido como `"entregadoo"` o `null`. Java verifica en tiempo de compilación que solo se usen valores del enum.

**Estados:**
| Valor | Significa |
|-------|-----------|
| `RECIBIDO` | El paquete llegó a la empresa, aún no sale |
| `EN_TRANSITO` | El repartidor lo lleva en ruta |
| `ENTREGADO` | Llegó al destinatario exitosamente |
| `DEVUELTO` | No se pudo entregar, regresó |

**Regla de negocio aplicada:** RN-02 (flujo secuencial) y RN-08 (ENTREGADO/DEVUELTO son estados finales — no se pueden cambiar).

---

### `RolUsuario.java` — Enumeración
**Por qué existe:** El sistema tiene dos tipos de usuario con permisos distintos. El enum garantiza que no exista un rol "SUPERADMIN" inventado.

| Valor | Permisos |
|-------|----------|
| `ADMINISTRADOR` | Todo: CRUD de repartidores, rutas, usuarios y paquetes |
| `OPERADOR` | Solo: crear órdenes, cambiar estados, ver clientes y paquetes |

---

### `Persona.java` — Clase abstracta (padre)
**Por qué existe:** `Cliente`, `Repartidor` y `Administrador` son personas. Los tres comparten los mismos datos de contacto: nombre, teléfono, email y dirección. En lugar de repetir esos 5 atributos en cada clase, se colocan aquí una sola vez. Eso es **herencia**.

**Por qué es abstracta:** Nunca se creará un objeto de tipo "Persona" solo. Siempre será un Cliente, un Repartidor o un Administrador específico. La palabra `abstract` en Java impide instanciarla directamente.

**Método clave:**
- `getDatosContacto()` → retorna nombre + teléfono + email como texto. Cada clase hija lo sobreescribe (`@Override`) para agregar su información específica.

---

### `Cliente.java` — Hereda de Persona
**Por qué existe:** Representa a la persona que contrata el servicio y paga por los envíos.

**Qué agrega sobre Persona:**
- `codigoCliente` — identificador único de negocio (ej. `CLI-001`)
- `fechaRegistro` — cuándo se dio de alta en el sistema
- `listaOrdenes` — historial de todas sus órdenes (`ArrayList<Orden>`)

**Método clave:**
- `agregarOrden(Orden)` → agrega una orden a su lista. Se llama cada vez que el cliente genera un nuevo envío.
- `getTotalEnvios()` → retorna cuántas órdenes ha hecho en total.

**Relación con BD:** Se guarda en dos tablas: `personas` (datos de contacto) y `clientes` (código y fecha). Ambas están unidas por el mismo `id` (FK).

---

### `Repartidor.java` — Hereda de Persona
**Por qué existe:** Es el empleado que transporta y entrega los paquetes. Necesita datos que un cliente normal no tiene: licencia de conducir y disponibilidad.

**Qué agrega sobre Persona:**
- `codigoRepartidor` — identificador interno (ej. `REP-001`)
- `licencia` — número de licencia de conducir (requerido por RN-03)
- `disponible` — `true` si puede recibir asignaciones, `false` si ya tiene una ruta activa
- `rutaActual` — referencia al objeto `Ruta` que tiene asignada

**Métodos clave:**
- `asignarRuta(Ruta)` → guarda la ruta y pone `disponible = false` (RN-04)
- `liberarRuta()` → borra la ruta y pone `disponible = true`
- `estaDisponible()` → getter semántico que dice si puede recibir trabajo

---

### `Usuario.java` — Clase abstracta (padre de sesión)
**Por qué existe:** Separa la identidad de negocio (ser una Persona) de la identidad de sistema (tener acceso con contraseña). Un `Operador` no necesariamente es una persona registrada en la BD de personas — es solo una cuenta del sistema.

**Qué contiene:**
- `username` / `password` — credenciales de acceso
- `rol` — el `RolUsuario` que determina los permisos
- `activo` — si la cuenta está habilitada o bloqueada

**Método clave:**
- `login(user, pass)` → compara credenciales. Retorna `true` solo si el usuario está activo Y las credenciales coinciden.
- `tienePermiso(accion)` → un `ADMINISTRADOR` siempre retorna `true`. Un `OPERADOR` solo puede hacer ciertas acciones definidas.

---

### `Administrador.java` — Hereda de Persona
**Por qué existe:** Es la única figura que puede registrar/eliminar repartidores y rutas (RN-06). Hereda de `Persona` porque también es una persona registrada con datos de contacto, y tiene además credenciales de sistema embebidas.

**Nota de diseño:** Java no soporta herencia múltiple de clases. Por eso `Administrador` hereda formalmente de `Persona` y tiene los atributos de `Usuario` replicados internamente (`username`, `password`, `rol`). En la Fase 3 esto se puede refactorizar con interfaces.

**Método clave:**
- `registrarRepartidor(Repartidor)` → valida que no sea nulo antes de delegar al controller
- `registrarRuta(Ruta)` → igual que el anterior
- `tienePermiso(accion)` → siempre `true` si está activo

---

### `Operador.java` — Hereda de Usuario
**Por qué existe:** Es el usuario del día a día: recibe paquetes, crea órdenes y actualiza estados. No puede tocar la configuración del sistema.

**Qué agrega:**
- `turno` — MAÑANA, TARDE o NOCHE (para control de horarios)

**Método clave:**
- `actualizarEstadoPaquete(Paquete, EstadoPaquete, obs)` → llama a `paquete.cambiarEstado(...)` y automáticamente registra el historial con su username.

---

### `Paquete.java` — Entidad central
**Por qué existe:** Es el objeto físico que se envía. Es la entidad más importante del sistema — todo gira alrededor de él.

**Atributos importantes:**
- `codigoPaquete` — código de rastreo único (ej. `PKG-001`)
- `peso`, `largo`, `ancho`, `alto` — para calcular costo y verificar restricciones
- `estado` — su estado actual (`EstadoPaquete`)
- `remitente` → referencia al `Cliente` que envía
- `destinatario` → referencia a la `Persona` que recibe
- `repartidor` → referencia al `Repartidor` asignado (puede ser null)
- `ruta` → referencia a la `Ruta` del envío (puede ser null)
- `historial` → `ArrayList<HistorialEstado>` con todos los cambios de estado
- `costoEnvio` → calculado automáticamente

**Métodos clave:**
- `calcularCosto()` → si tiene ruta asignada, usa `ruta.calcularCostoRuta(peso)`. Si no, aplica tarifa mínima de Q3.00/kg.
- `cambiarEstado(nuevoEstado, obs, usuario)` → primero llama a `estaFinalizado()`. Si el paquete ya está ENTREGADO o DEVUELTO, lanza `IllegalStateException` (RN-08). Si no, cambia el estado y agrega un `HistorialEstado` a la lista (RN-09).
- `getVolumen()` → retorna largo × ancho × alto (útil para cálculos de capacidad).
- `estaFinalizado()` → retorna `true` si el estado es ENTREGADO o DEVUELTO.

---

### `Orden.java` — Agrupador de paquetes
**Por qué existe:** Un cliente puede enviar varios paquetes en un mismo servicio. La `Orden` los agrupa todos bajo un mismo código y calcula el costo total.

**Relación UML:** Agregación con `Paquete` — la orden agrupa paquetes, pero si la orden se elimina, los paquetes podrían seguir existiendo.

**Métodos clave:**
- `agregarPaquete(Paquete)` → agrega a la lista y llama `calcularTotal()` automáticamente
- `calcularTotal()` → suma los `costoEnvio` de todos los paquetes
- `marcarPagado()` → cambia `pagado = true`

---

### `Ruta.java` — Trayecto de entrega
**Por qué existe:** Define el camino entre un origen y un destino, con la distancia y la tarifa. Sin rutas, no hay forma de calcular el costo del envío ni de asignar trabajo al repartidor.

**Atributos importantes:**
- `distanciaKm` — distancia del trayecto en kilómetros
- `tarifaBase` — precio por kg en esa ruta (varía según la distancia/zona)
- `activa` — si la ruta está disponible para nuevas asignaciones

**Método clave:**
- `calcularCostoRuta(peso)` → fórmula: `(peso × tarifaBase) + (distanciaKm × 0.10)`. El factor 0.10 representa el costo por kilómetro adicional.
- `desactivar()` → pone `activa = false`. Un administrador puede desactivar rutas sin eliminarlas.

---

### `HistorialEstado.java` — Registro de auditoría
**Por qué existe:** La regla RN-09 exige que cada cambio de estado quede registrado con fecha, hora y usuario responsable. Es un log inmutable — nunca se edita, solo se crea.

**Relación UML:** Composición con `Paquete` — el historial no existe sin el paquete.

**Atributos:**
- `estado` — el estado al que cambió
- `fecha` — `LocalDateTime` exacto del cambio
- `observacion` — nota del operador (ej. "No había nadie en casa")
- `usuarioResponsable` — username de quien hizo el cambio

**Método clave:**
- `getResumen()` → retorna todo en una línea: `EN_TRANSITO | 19/10/2026 07:15 | Usuario: operador1 | Obs: En camino`

---

### `Empresa.java` — Raíz del sistema
**Por qué existe:** Actúa como el contenedor principal que agrupa todas las listas en memoria. Útil para búsquedas rápidas sin ir a la BD.

**Relación UML:** Composición con `Repartidor` y `Ruta` — si la empresa desaparece, sus repartidores y rutas también.

**Métodos clave:**
- `buscarPaquete(codigo)` → recorre todas las órdenes y sus paquetes buscando el código de rastreo
- `getRepartidoresDisponibles()` → filtra la lista retornando solo los que tienen `disponible = true`
- `getEstadisticas()` → cuenta paquetes por estado y retorna un resumen en texto

---

## 2. Capa DAO (Data Access Object)

### `Conexion.java`
**Por qué existe:** Centraliza la conexión a MySQL. Si en el futuro cambia el servidor, la contraseña o el puerto, solo se cambia aquí — no en cada DAO.

**Patrón Singleton:** Mantiene una sola conexión activa (`instancia`). Si ya existe y está abierta, la reutiliza. Esto evita abrir cientos de conexiones innecesarias.

**Por qué `Class.forName(...)`:** Carga el driver JDBC en memoria. Sin esto, Java no sabe cómo hablar con MySQL.

---

### `ClienteDAO.java`
**Por qué existe:** Toda la comunicación con las tablas `personas` + `clientes` pasa por aquí. El modelo (`Cliente.java`) no sabe nada de SQL — eso es responsabilidad exclusiva del DAO.

**Por qué dos tablas:** La herencia del modelo se refleja en la BD. `personas` guarda los datos comunes; `clientes` guarda solo lo específico del cliente. Se unen con `INNER JOIN` en los `SELECT`.

**Por qué transacciones (`setAutoCommit(false)`):** Al insertar un cliente se escriben DOS tablas. Si la segunda falla, se hace `rollback()` para deshacer la primera y la BD no queda en estado inconsistente.

**Código seguro:** Todos los `?` en el SQL son `PreparedStatement`. Java los reemplaza de forma segura, evitando SQL Injection.

---

### `RutaDAO.java`
**Por qué existe:** CRUD completo para la tabla `rutas`. Incluye `listarActivas()` que filtra solo rutas con `activa = 1`, evitando asignar rutas desactivadas a repartidores.

---

### `RepartidorDAO.java`
**Por qué existe:** Igual que `ClienteDAO`, maneja dos tablas (`personas` + `repartidores`) con transacciones. `listarDisponibles()` usa `WHERE r.disponible = 1` para el filtro de asignación (RN-03).

---

### `PaqueteDAO.java`
**Por qué existe:** Es el DAO más complejo porque `Paquete` tiene más relaciones. Incluye:
- `actualizarEstado(...)` → método especial que en una transacción hace dos cosas: actualiza el estado en `paquetes` Y registra en `historial_estados`. Si alguna falla, hace `rollback`.
- `getHistorial(idPaquete)` → trae todos los registros de `historial_estados` para un paquete dado.
- `listarPorEstado(EstadoPaquete)` → permite filtrar paquetes por su estado actual.

**Validación extra:** Antes de cambiar el estado verifica en BD que el estado actual no sea ENTREGADO ni DEVUELTO (RN-08 aplicado en capa de datos).

---

### `UsuarioDAO.java`
**Por qué existe:** Maneja el acceso al sistema. El método `login(user, pass)` consulta con `PreparedStatement` en lugar de concatenar strings — esto es fundamental para la seguridad.

**Por qué no hay hash de contraseña:** Para las pruebas del proyecto se usa texto plano. En producción real se usaría `BCrypt` o `SHA-256`.

---

## 3. Capa CONTROLLER

Los controllers actúan como **intermediarios**. La vista (menú o formulario) llama al controller, el controller llama al DAO, y retorna el resultado. Nunca la vista llama al DAO directamente.

### `ClienteController.java`
- Valida que no exista ya un cliente con el mismo código antes de insertar.
- Captura `SQLException` e `IllegalArgumentException` y los convierte en mensajes legibles.

### `PaqueteController.java`
- Llama `p.calcularCosto()` antes de insertar para que el costo quede guardado en BD.
- `cambiarEstado(...)` aplica RN-08 y RN-09 a través del DAO.

### `RepartidorController.java`
- `asignarRuta(idRepartidor, idRuta)` busca ambos objetos, verifica disponibilidad (RN-04) y luego actualiza.

### `UsuarioController.java`
- Mantiene el `usuarioActual` en memoria durante la sesión.
- `tienePermiso(accion)` delega al usuario logueado — así la vista puede decidir qué mostrar según el rol.
- `logout()` limpia `usuarioActual = null`.

---

## 4. `Main.java` — Vista de Consola

**Por qué existe:** Demuestra el CRUD completo funcionando contra MySQL (requisito de Fase 2). En la Fase 3 se reemplaza por formularios Swing.

**Flujo:**
1. Solicita login → valida contra BD
2. Muestra menú según rol del usuario
3. Cada opción del menú llama al controller correspondiente
4. Al salir llama `Conexion.cerrarConexion()` para cerrar la conexión JDBC limpiamente

---

## 5. Base de Datos (`packtrack_db.sql`)

| Tabla | Propósito |
|-------|-----------|
| `personas` | Base de todos los humanos del sistema |
| `clientes` | Extiende personas con datos de cliente |
| `repartidores` | Extiende personas con licencia y disponibilidad |
| `rutas` | Trayectos de entrega con tarifas |
| `usuarios` | Cuentas de acceso al sistema |
| `ordenes` | Agrupador de envíos por cliente |
| `paquetes` | Cada objeto físico a enviar |
| `historial_estados` | Log inmutable de cambios de estado |

**Por qué CASCADE en las FK:** Si se elimina una `persona`, automáticamente se elimina su registro en `clientes` o `repartidores`. Evita registros huérfanos.

**Por qué RESTRICT en paquetes:** Si un cliente tiene paquetes, no se puede eliminar el cliente. Protege la integridad de los envíos activos.

---

*Manual generado para Fase 2 — PackTrack v1.0*
