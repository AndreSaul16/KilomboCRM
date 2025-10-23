-- ============================================
-- Script de Creación de Base de Datos
-- KilomboCRM - Sistema de Gestión
-- ============================================

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS empresa 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE empresa;

-- Eliminar tablas si existen (para recrear)
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS cliente;

-- ============================================
-- Tabla: cliente
-- Descripción: Almacena información de clientes
-- ============================================
CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único del cliente',
    nombre VARCHAR(100) NOT NULL COMMENT 'Nombre del cliente',
    apellido VARCHAR(100) NOT NULL COMMENT 'Apellido del cliente',
    email VARCHAR(150) NOT NULL UNIQUE COMMENT 'Email del cliente (único)',
    telefono VARCHAR(20) COMMENT 'Teléfono de contacto',
    
    -- Índices para mejorar rendimiento
    INDEX idx_email (email),
    INDEX idx_nombre_apellido (nombre, apellido),
    INDEX idx_apellido (apellido)
) ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla de clientes del sistema';

-- ============================================
-- Tabla: pedido
-- Descripción: Almacena pedidos de clientes
-- ============================================
CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único del pedido',
    id_cliente INT NOT NULL COMMENT 'Referencia al cliente',
    fecha DATE NOT NULL COMMENT 'Fecha del pedido',
    total DOUBLE NOT NULL COMMENT 'Importe total del pedido',
    
    -- Clave foránea con eliminación en cascada
    CONSTRAINT fk_pedido_cliente 
        FOREIGN KEY (id_cliente) 
        REFERENCES cliente(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    -- Restricción: el total debe ser positivo
    CONSTRAINT chk_total_positivo 
        CHECK (total > 0),
    
    -- Índices para mejorar rendimiento
    INDEX idx_cliente (id_cliente),
    INDEX idx_fecha (fecha),
    INDEX idx_cliente_fecha (id_cliente, fecha)
) ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla de pedidos del sistema';

-- ============================================
-- Verificación de tablas creadas
-- ============================================
SHOW TABLES;

-- Mostrar estructura de las tablas
DESCRIBE cliente;
DESCRIBE pedido;