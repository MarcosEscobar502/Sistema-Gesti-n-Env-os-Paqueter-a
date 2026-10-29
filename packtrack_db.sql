-- ============================================================
-- PackTrack - Sistema de Gestión de Envíos y Paquetería
-- Script de creación de base de datos
-- MySQL 8.x
-- ============================================================

DROP DATABASE IF EXISTS packtrack_db;
CREATE DATABASE packtrack_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE packtrack_db;

-- ------------------------------------------------------------
-- TABLA: personas (base para clientes y repartidores)
-- ------------------------------------------------------------
CREATE TABLE personas (
    id            INT          NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(100) NOT NULL,
    telefono      VARCHAR(20)  NOT NULL,
    email         VARCHAR(100) NOT NULL,
    direccion     VARCHAR(200) NOT NULL,
    tipo          ENUM('CLIENTE','REPARTIDOR','ADMINISTRADOR','DESTINATARIO') NOT NULL,
    CONSTRAINT pk_personas PRIMARY KEY (id),
    CONSTRAINT uq_personas_email UNIQUE (email)
);

-- ------------------------------------------------------------
-- TABLA: clientes (extiende personas)
-- ------------------------------------------------------------
CREATE TABLE clientes (
    id               INT         NOT NULL,
    codigo_cliente   VARCHAR(20) NOT NULL,
    fecha_registro   DATE        NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT pk_clientes     PRIMARY KEY (id),
    CONSTRAINT fk_clientes_persona FOREIGN KEY (id) REFERENCES personas(id) ON DELETE CASCADE,
    CONSTRAINT uq_clientes_codigo  UNIQUE (codigo_cliente)
);

-- ------------------------------------------------------------
-- TABLA: rutas
-- ------------------------------------------------------------
CREATE TABLE rutas (
    id             INT            NOT NULL AUTO_INCREMENT,
    codigo_ruta    VARCHAR(20)    NOT NULL,
    nombre         VARCHAR(100)   NOT NULL,
    origen         VARCHAR(100)   NOT NULL,
    destino        VARCHAR(100)   NOT NULL,
    distancia_km   DECIMAL(10,2)  NOT NULL CHECK (distancia_km > 0),
    tarifa_base    DECIMAL(10,2)  NOT NULL CHECK (tarifa_base >= 0),
    activa         TINYINT(1)     NOT NULL DEFAULT 1,
    CONSTRAINT pk_rutas     PRIMARY KEY (id),
    CONSTRAINT uq_rutas_codigo UNIQUE (codigo_ruta)
);

-- ------------------------------------------------------------
-- TABLA: repartidores (extiende personas)
-- ------------------------------------------------------------
CREATE TABLE repartidores (
    id                  INT         NOT NULL,
    codigo_repartidor   VARCHAR(20) NOT NULL,
    licencia            VARCHAR(30) NOT NULL,
    disponible          TINYINT(1)  NOT NULL DEFAULT 1,
    id_ruta_actual      INT         NULL,
    CONSTRAINT pk_repartidores        PRIMARY KEY (id),
    CONSTRAINT fk_repartidores_persona FOREIGN KEY (id)             REFERENCES personas(id)  ON DELETE CASCADE,
    CONSTRAINT fk_repartidores_ruta    FOREIGN KEY (id_ruta_actual) REFERENCES rutas(id)     ON DELETE SET NULL,
    CONSTRAINT uq_repartidores_codigo  UNIQUE (codigo_repartidor),
    CONSTRAINT uq_repartidores_licencia UNIQUE (licencia)
);

-- ------------------------------------------------------------
-- TABLA: usuarios
-- ------------------------------------------------------------
CREATE TABLE usuarios (
    id          INT          NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    rol         ENUM('ADMINISTRADOR','OPERADOR') NOT NULL,
    activo      TINYINT(1)   NOT NULL DEFAULT 1,
    id_persona  INT          NULL,
    CONSTRAINT pk_usuarios        PRIMARY KEY (id),
    CONSTRAINT fk_usuarios_persona FOREIGN KEY (id_persona) REFERENCES personas(id) ON DELETE SET NULL,
    CONSTRAINT uq_usuarios_username UNIQUE (username)
);

-- ------------------------------------------------------------
-- TABLA: ordenes
-- ------------------------------------------------------------
CREATE TABLE ordenes (
    id             INT            NOT NULL AUTO_INCREMENT,
    codigo_orden   VARCHAR(20)    NOT NULL,
    fecha          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_cliente     INT            NOT NULL,
    total_costo    DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    pagado         TINYINT(1)     NOT NULL DEFAULT 0,
    CONSTRAINT pk_ordenes       PRIMARY KEY (id),
    CONSTRAINT fk_ordenes_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE RESTRICT,
    CONSTRAINT uq_ordenes_codigo  UNIQUE (codigo_orden)
);

-- ------------------------------------------------------------
-- TABLA: paquetes
-- ------------------------------------------------------------
CREATE TABLE paquetes (
    id               INT            NOT NULL AUTO_INCREMENT,
    codigo_paquete   VARCHAR(30)    NOT NULL,
    descripcion      VARCHAR(200)   NOT NULL,
    peso             DECIMAL(8,3)   NOT NULL CHECK (peso > 0),
    largo            DECIMAL(8,2)   NOT NULL CHECK (largo > 0),
    ancho            DECIMAL(8,2)   NOT NULL CHECK (ancho > 0),
    alto             DECIMAL(8,2)   NOT NULL CHECK (alto > 0),
    estado           ENUM('RECIBIDO','EN_TRANSITO','ENTREGADO','DEVUELTO') NOT NULL DEFAULT 'RECIBIDO',
    id_remitente     INT            NOT NULL,
    id_destinatario  INT            NOT NULL,
    id_repartidor    INT            NULL,
    id_ruta          INT            NULL,
    id_orden         INT            NULL,
    fecha_creacion   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    costo_envio      DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    CONSTRAINT pk_paquetes           PRIMARY KEY (id),
    CONSTRAINT fk_paquetes_remitente  FOREIGN KEY (id_remitente)   REFERENCES clientes(id)     ON DELETE RESTRICT,
    CONSTRAINT fk_paquetes_destinatario FOREIGN KEY (id_destinatario) REFERENCES personas(id)  ON DELETE RESTRICT,
    CONSTRAINT fk_paquetes_repartidor FOREIGN KEY (id_repartidor)  REFERENCES repartidores(id) ON DELETE SET NULL,
    CONSTRAINT fk_paquetes_ruta       FOREIGN KEY (id_ruta)        REFERENCES rutas(id)        ON DELETE SET NULL,
    CONSTRAINT fk_paquetes_orden      FOREIGN KEY (id_orden)       REFERENCES ordenes(id)      ON DELETE SET NULL,
    CONSTRAINT uq_paquetes_codigo     UNIQUE (codigo_paquete)
);

-- ------------------------------------------------------------
-- TABLA: historial_estados
-- ------------------------------------------------------------
CREATE TABLE historial_estados (
    id                    INT          NOT NULL AUTO_INCREMENT,
    id_paquete            INT          NOT NULL,
    estado                ENUM('RECIBIDO','EN_TRANSITO','ENTREGADO','DEVUELTO') NOT NULL,
    fecha                 DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion           VARCHAR(300) NULL,
    usuario_responsable   VARCHAR(50)  NOT NULL,
    CONSTRAINT pk_historial          PRIMARY KEY (id),
    CONSTRAINT fk_historial_paquete  FOREIGN KEY (id_paquete) REFERENCES paquetes(id) ON DELETE CASCADE
);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- Personas base
INSERT INTO personas (nombre, telefono, email, direccion, tipo) VALUES
('Carlos Méndez',    '7700-1111', 'carlos@mail.com',   'Calle 1, Ciudad', 'CLIENTE'),
('Ana López',        '7700-2222', 'ana@mail.com',       'Calle 2, Ciudad', 'CLIENTE'),
('Juan Repartidor',  '7700-3333', 'juan@mail.com',      'Calle 3, Ciudad', 'REPARTIDOR'),
('María Admin',      '7700-4444', 'maria@mail.com',     'Calle 4, Ciudad', 'ADMINISTRADOR'),
('Pedro Destinatario','7700-5555','pedro@mail.com',     'Calle 5, Ciudad', 'DESTINATARIO');

-- Clientes
INSERT INTO clientes (id, codigo_cliente, fecha_registro) VALUES
(1, 'CLI-001', CURRENT_DATE),
(2, 'CLI-002', CURRENT_DATE);

-- Rutas
INSERT INTO rutas (codigo_ruta, nombre, origen, destino, distancia_km, tarifa_base) VALUES
('RUT-001', 'Centro-Norte', 'Centro',  'Zona Norte', 15.00, 2.50),
('RUT-002', 'Sur-Este',     'Zona Sur','Zona Este',  22.00, 2.00);

-- Repartidores
INSERT INTO repartidores (id, codigo_repartidor, licencia, disponible, id_ruta_actual) VALUES
(3, 'REP-001', 'LIC-123456', 1, NULL);

-- Usuarios (password = 'admin123' en texto plano para pruebas — hashear en producción)
INSERT INTO usuarios (username, password, rol, activo, id_persona) VALUES
('admin',    'admin123',    'ADMINISTRADOR', 1, 4),
('operador1','operador123', 'OPERADOR',      1, NULL);

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
