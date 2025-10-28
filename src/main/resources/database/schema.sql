-- ============================================
-- Script de Creación de Base de Datos
-- Empresa 'Kilombo' - Clientes y Pedidos
-- ============================================

-- REQUISITO 1: Crear la base de datos 'kilombo'
CREATE DATABASE IF NOT EXISTS kilombo
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE kilombo;

-- Eliminar tablas si existen 
DROP TABLE IF EXISTS detalles_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS clientes;

-- ============================================
-- 1. Tabla: clientes
-- ============================================
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_nombre_apellido (nombre, apellido)
) ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla de clientes del sistema Kilombo';

-- ============================================
-- 2. Tabla: pedidos 
-- ============================================
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único del pedido',
    id_cliente INT NOT NULL COMMENT 'Referencia al cliente',
    fecha DATE NOT NULL COMMENT 'Fecha del pedido',
    -- Total calculado por Java, pero aquí lo ponemos como requerido por el enunciado
    total DECIMAL(10, 2) NOT NULL COMMENT 'Importe total del pedido', 
    estado ENUM('PENDIENTE', 'EN_PROCESO', 'COMPLETADO', 'CANCELADO') DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Clave foránea (REQUISITO: ON DELETE CASCADE)
    CONSTRAINT fk_pedido_cliente 
        FOREIGN KEY (id_cliente) 
        REFERENCES clientes(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    -- Restricción: el total debe ser positivo
    CONSTRAINT chk_total_positivo CHECK (total > 0),
    
    INDEX idx_cliente (id_cliente),
    INDEX idx_fecha (fecha),
    INDEX idx_estado (estado)
) ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla de pedidos del sistema';

-- ============================================
-- 3. Tabla: detalles_pedido
-- ============================================
CREATE TABLE detalles_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    tipo_producto VARCHAR(100) NOT NULL COMMENT 'Para personalización de mensajes (ej: Lavadora, Smartphone)',
    descripcion VARCHAR(255) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    costo_unitario DECIMAL(10, 2) NOT NULL COMMENT 'Costo para la empresa (para BI)',
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED COMMENT 'Columna generada: Venta total del item',
    ganancia_bruta DECIMAL(10, 2) GENERATED ALWAYS AS (cantidad * (precio_unitario - costo_unitario)) STORED COMMENT 'Columna generada: Ganancia bruta del item',
    
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE,
    INDEX idx_pedido (id_pedido)
) ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla de detalle de ítems por pedido';

-- ============================================
-- Verificación del Esquema
-- ============================================
SHOW TABLES;
