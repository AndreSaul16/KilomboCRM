-- ============================================
-- Script de Datos Iniciales
-- KilomboCRM - Sistema de Gestión
-- ============================================

USE empresa;

-- ============================================
-- Insertar datos en tabla cliente
-- ============================================

INSERT INTO cliente (nombre, apellido, email, telefono) VALUES
('Juan', 'García Martínez', 'juan.garcia@email.com', '+34 612 345 678'),
('María', 'López Fernández', 'maria.lopez@email.com', '+34 623 456 789'),
('Carlos', 'Rodríguez Sánchez', 'carlos.rodriguez@email.com', '+34 634 567 890'),
('Ana', 'Martínez González', 'ana.martinez@email.com', '+34 645 678 901'),
('Pedro', 'Sánchez Pérez', 'pedro.sanchez@email.com', '+34 656 789 012'),
('Laura', 'Fernández Ruiz', 'laura.fernandez@email.com', '+34 667 890 123'),
('Miguel', 'González Díaz', 'miguel.gonzalez@email.com', '+34 678 901 234'),
('Carmen', 'Díaz Moreno', 'carmen.diaz@email.com', '+34 689 012 345');

-- ============================================
-- Insertar datos en tabla pedido
-- ============================================

-- Pedidos para Juan García (id_cliente = 1)
INSERT INTO pedido (id_cliente, fecha, total) VALUES
(1, '2024-01-15', 150.50),
(1, '2024-02-20', 275.00),
(1, '2024-03-10', 89.99);

-- Pedidos para María López (id_cliente = 2)
INSERT INTO pedido (id_cliente, fecha, total) VALUES
(2, '2024-01-18', 320.75),
(2, '2024-02-25', 450.00),
(2, '2024-03-15', 125.50),
(2, '2024-04-05', 199.99);

-- Pedidos para Carlos Rodríguez (id_cliente = 3)
INSERT INTO pedido (id_cliente, fecha, total) VALUES
(3, '2024-01-22', 500.00),
(3, '2024-03-08', 350.25);

-- Pedidos para Ana Martínez (id_cliente = 4)
INSERT INTO pedido (id_cliente, fecha, total) VALUES
(4, '2024-02-10', 175.80),
(4, '2024-03-20', 290.50),
(4, '2024-04-12', 420.00);

-- Pedidos para Pedro Sánchez (id_cliente = 5)
INSERT INTO pedido (id_cliente, fecha, total) VALUES
(5, '2024-01-25', 680.00),
(5, '2024-02-28', 125.75),
(5, '2024-03-30', 310.50),
(5, '2024-04-15', 95.99);

-- Pedidos para Laura Fernández (id_cliente = 6)
INSERT INTO pedido (id_cliente, fecha, total) VALUES
(6, '2024-02-05', 220.00),
(6, '2024-03-12', 380.50);

-- Pedidos para Miguel González (id_cliente = 7)
INSERT INTO pedido (id_cliente, fecha, total) VALUES
(7, '2024-01-30', 540.25),
(7, '2024-02-18', 165.00),
(7, '2024-03-25', 275.80);

-- Pedidos para Carmen Díaz (id_cliente = 8)
INSERT INTO pedido (id_cliente, fecha, total) VALUES
(8, '2024-02-12', 890.00),
(8, '2024-03-18', 450.75),
(8, '2024-04-08', 320.50),
(8, '2024-04-20', 175.25);

-- ============================================
-- Verificación de datos insertados
-- ============================================

-- Contar registros
SELECT 'Clientes insertados:' AS Info, COUNT(*) AS Total FROM cliente;
SELECT 'Pedidos insertados:' AS Info, COUNT(*) AS Total FROM pedido;

-- Mostrar algunos datos
SELECT 'Primeros 5 clientes:' AS Info;
SELECT id, nombre, apellido, email FROM cliente LIMIT 5;

SELECT 'Primeros 5 pedidos:' AS Info;
SELECT p.id, c.nombre, c.apellido, p.fecha, p.total 
FROM pedido p
INNER JOIN cliente c ON p.id_cliente = c.id
LIMIT 5;

-- Estadísticas por cliente
SELECT 'Pedidos por cliente:' AS Info;
SELECT 
    c.id,
    c.nombre,
    c.apellido,
    COUNT(p.id) AS num_pedidos,
    COALESCE(SUM(p.total), 0) AS total_gastado
FROM cliente c
LEFT JOIN pedido p ON c.id_cliente = p.id
GROUP BY c.id, c.nombre, c.apellido
ORDER BY total_gastado DESC;