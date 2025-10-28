-- ============================================
-- Script de Datos Iniciales - Base de Datos: kilombo
-- ============================================

-- IMPORTANTE: Usar la BBDD 'kilombo'
USE kilombo;

-- Configurar codificación UTF-8 para inserciones
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ============================================
-- 1. Insertar datos en tabla clientes (8 Registros)
-- ============================================


INSERT INTO clientes (nombre, apellido, email, telefono) VALUES
('Juan', 'García Martínez', 'juan.garcia@kilombo.es', '+34 612 345 678'), -- ID 1
('María', 'López Fernández', 'maria.lopez@kilombo.es', '+34 623 456 789'), -- ID 2
('Carlos', 'Rodríguez Sánchez', 'carlos.rodriguez@kilombo.es', '+34 634 567 890'), -- ID 3
('Ana', 'Martínez González', 'ana.martinez@kilombo.es', '+34 645 678 901'), -- ID 4
('Pedro', 'Sánchez Pérez', 'pedro.sanchez@kilombo.es', '+34 656 789 012'), -- ID 5
('Laura', 'Fernández Ruiz', 'laura.fernandez@kilombo.es', '+34 667 890 123'), -- ID 6
('Miguel', 'González Díaz', 'miguel.gonzalez@kilombo.es', '+34 678 901 234'), -- ID 7
('Carmen', 'Díaz Moreno', 'carmen.diaz@kilombo.es', '+34 689 012 345'); -- ID 8

-- ============================================
-- 2. Insertar datos en tabla pedidos (8 Registros)
-- ============================================

INSERT INTO pedidos (id_cliente, fecha, total, estado) VALUES
(1, '2025-09-01', 599.99, 'COMPLETADO'),  -- ID 1
(2, '2025-09-05', 125.00, 'EN_PROCESO'),   -- ID 2 (Seguimiento WhatsApp)
(3, '2025-09-10', 1200.50, 'COMPLETADO'), -- ID 3 (BI)
(4, '2025-09-12', 45.00, 'COMPLETADO'),   -- ID 4 (BI)
(5, '2025-09-15', 350.00, 'PENDIENTE'),    -- ID 5
(6, '2025-09-20', 850.25, 'COMPLETADO'),  -- ID 6 (BI)
(7, '2025-09-22', 199.99, 'CANCELADO'),   -- ID 7
(8, '2025-09-25', 1500.00, 'EN_PROCESO');  -- ID 8 (Seguimiento WhatsApp)


-- ============================================
-- 3. Insertar datos en tabla detalles_pedido
-- ============================================

-- Pedido 1: Smartwatch (Ganancia Bruta alta)
INSERT INTO detalles_pedido (id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario) VALUES
(1, 'Smartwatch', 'Smartwatch GT3 Pro', 1, 350.00, 599.99); 

-- Pedido 2: EN_PROCESO (Accesorio)
INSERT INTO detalles_pedido (id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario) VALUES
(2, 'Accesorio', 'Cable de carga USB-C', 5, 5.00, 10.00),
(2, 'Accesorio', 'Cargador rápido PD', 5, 10.00, 15.00); 

-- Pedido 3: Portátil (Gran BI)
INSERT INTO detalles_pedido (id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario) VALUES
(3, 'Portátil', 'Portátil Gaming i9', 1, 900.00, 1200.50);

-- Pedido 4: Cafetera
INSERT INTO detalles_pedido (id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario) VALUES
(4, 'Cafetera', 'Cafetera automática', 1, 30.00, 45.00);

-- Pedido 6: Televisor (BI)
INSERT INTO detalles_pedido (id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario) VALUES
(6, 'Televisor', 'OLED 55 pulgadas 4K', 1, 600.00, 850.25);

-- Pedido 8: Componentes (EN_PROCESO)
INSERT INTO detalles_pedido (id_pedido, tipo_producto, descripcion, cantidad, costo_unitario, precio_unitario) VALUES
(8, 'Componente', 'Memoria RAM 16GB', 2, 50.00, 75.00),
(8, 'Componente', 'Tarjeta Gráfica RTX', 1, 800.00, 1200.00),
(8, 'Componente', 'Fuente de Poder 800W', 1, 50.00, 75.00);


-- ============================================
-- Verificación
-- ============================================

SELECT 'Datos insertados correctamente en KILOMBO:' AS Info;
SELECT COUNT(*) AS Total_Clientes FROM clientes;
SELECT COUNT(*) AS Total_Pedidos FROM pedidos;