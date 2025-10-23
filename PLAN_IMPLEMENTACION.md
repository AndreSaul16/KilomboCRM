# Plan de Implementación - KilomboCRM

## 📋 Resumen Ejecutivo

Este documento detalla el plan paso a paso para implementar el sistema KilomboCRM siguiendo los principios de Clean Architecture y Clean Code.

## 🎯 Objetivos

1. Crear una aplicación Java funcional con interfaz Swing
2. Implementar CRUD completo para clientes y pedidos
3. Conectar con MySQL mediante JDBC
4. Seguir Clean Architecture y Clean Code
5. Validar datos y manejar excepciones correctamente

## 📅 Fases de Implementación

### FASE 1: Configuración Inicial del Proyecto ⏱️ 30 min

#### 1.1 Crear estructura Maven
```bash
mkdir -p src/main/java/com/kilombo/crm/{domain,application,infrastructure,presentation}
mkdir -p src/main/resources/database
mkdir -p src/test/java/com/kilombo/crm
```

#### 1.2 Crear pom.xml
Configurar:
- Java 17
- MySQL Connector/J 8.0.33
- JUnit 5.9.3
- Encoding UTF-8

#### 1.3 Crear application.properties
Configuración de conexión a BD

**Entregables:**
- ✅ Estructura de directorios completa
- ✅ [`pom.xml`](pom.xml) configurado
- ✅ [`application.properties`](src/main/resources/application.properties)

---

### FASE 2: Base de Datos ⏱️ 45 min

#### 2.1 Crear schema.sql
```sql
CREATE DATABASE IF NOT EXISTS empresa;
USE empresa;

CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    INDEX idx_email (email),
    INDEX idx_nombre_apellido (nombre, apellido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha DATE NOT NULL,
    total DOUBLE NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id) ON DELETE CASCADE,
    INDEX idx_cliente (id_cliente),
    INDEX idx_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 2.2 Crear data.sql
Insertar 5+ registros por tabla con datos realistas

**Entregables:**
- ✅ [`schema.sql`](src/main/resources/database/schema.sql)
- ✅ [`data.sql`](src/main/resources/database/data.sql)
- ✅ BD creada y poblada

---

### FASE 3: Capa de Dominio ⏱️ 1h 30min

#### 3.1 Crear entidades

**Cliente.java**
```java
package com.kilombo.crm.domain.model;

public class Cliente {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    
    // Constructor, getters, setters
    // Validaciones de negocio
    // equals, hashCode, toString
}
```

**Pedido.java**
```java
package com.kilombo.crm.domain.model;

import java.time.LocalDate;

public class Pedido {
    private Integer id;
    private Integer idCliente;
    private LocalDate fecha;
    private Double total;
    
    // Constructor, getters, setters
    // Validaciones de negocio
    // equals, hashCode, toString
}
```

#### 3.2 Crear interfaces de repositorio

**ClienteRepository.java**
```java
package com.kilombo.crm.domain.repository;

import com.kilombo.crm.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Integer id);
    List<Cliente> findAll();
    void update(Cliente cliente);
    void deleteById(Integer id);
    boolean existsByEmail(String email);
}
```

**PedidoRepository.java**
```java
package com.kilombo.crm.domain.repository;

import com.kilombo.crm.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Integer id);
    List<Pedido> findAll();
    List<Pedido> findByClienteId(Integer idCliente);
    void update(Pedido pedido);
    void deleteById(Integer id);
}
```

#### 3.3 Crear excepciones de dominio

- [`ValidationException.java`](src/main/java/com/kilombo/crm/domain/exception/ValidationException.java)
- [`ClienteNotFoundException.java`](src/main/java/com/kilombo/crm/domain/exception/ClienteNotFoundException.java)
- [`PedidoNotFoundException.java`](src/main/java/com/kilombo/crm/domain/exception/PedidoNotFoundException.java)
- [`DatabaseException.java`](src/main/java/com/kilombo/crm/domain/exception/DatabaseException.java)

**Entregables:**
- ✅ Entidades [`Cliente`](src/main/java/com/kilombo/crm/domain/model/Cliente.java) y [`Pedido`](src/main/java/com/kilombo/crm/domain/model/Pedido.java)
- ✅ Interfaces de repositorio
- ✅ Excepciones de dominio
- ✅ Validaciones implementadas

---

### FASE 4: Capa de Infraestructura ⏱️ 2h

#### 4.1 Crear gestión de conexión

**ConexionBD.java** (Singleton)
```java
package com.kilombo.crm.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    private static ConexionBD instance;
    private Connection connection;
    private Properties properties;
    
    private ConexionBD() {
        // Cargar properties
        // Establecer conexión
    }
    
    public static ConexionBD getInstance() {
        if (instance == null) {
            synchronized (ConexionBD.class) {
                if (instance == null) {
                    instance = new ConexionBD();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Reconectar
        }
        return connection;
    }
    
    public void closeConnection() {
        // Cerrar conexión
    }
}
```

#### 4.2 Crear mappers

**ClienteMapper.java**
```java
package com.kilombo.crm.infrastructure.mapper;

import com.kilombo.crm.domain.model.Cliente;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteMapper {
    public static Cliente fromResultSet(ResultSet rs) throws SQLException {
        // Mapear ResultSet a Cliente
    }
}
```

**PedidoMapper.java** (similar)

#### 4.3 Implementar DAOs

**ClienteRepositoryImpl.java**
```java
package com.kilombo.crm.infrastructure.repository;

import com.kilombo.crm.domain.model.Cliente;
import com.kilombo.crm.domain.repository.ClienteRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryImpl implements ClienteRepository {
    
    @Override
    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO cliente (nombre, apellido, email, telefono) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefono());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
            
            return cliente;
        } catch (SQLException e) {
            throw new DatabaseException("Error al guardar cliente", e);
        }
    }
    
    // Implementar resto de métodos...
}
```

**PedidoRepositoryImpl.java** (similar)

**Entregables:**
- ✅ [`ConexionBD`](src/main/java/com/kilombo/crm/infrastructure/database/ConexionBD.java) (Singleton)
- ✅ Mappers para entidades
- ✅ Implementaciones DAO completas
- ✅ Manejo de excepciones SQL

---

### FASE 5: Capa de Aplicación ⏱️ 1h 30min

#### 5.1 Crear DTOs

**ClienteDTO.java**
```java
package com.kilombo.crm.application.dto;

public class ClienteDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    
    // Constructor, getters, setters
    
    public static ClienteDTO fromEntity(Cliente cliente) {
        // Convertir entidad a DTO
    }
    
    public Cliente toEntity() {
        // Convertir DTO a entidad
    }
}
```

**PedidoDTO.java** (similar)

#### 5.2 Crear servicios

**ClienteService.java**
```java
package com.kilombo.crm.application.service;

import com.kilombo.crm.application.dto.ClienteDTO;
import com.kilombo.crm.domain.model.Cliente;
import com.kilombo.crm.domain.repository.ClienteRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteService {
    private final ClienteRepository clienteRepository;
    
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    
    public ClienteDTO crearCliente(ClienteDTO dto) {
        // Validar
        // Convertir a entidad
        // Guardar
        // Retornar DTO
    }
    
    public ClienteDTO obtenerCliente(Integer id) {
        // Buscar
        // Convertir a DTO
        // Retornar
    }
    
    public List<ClienteDTO> listarClientes() {
        // Obtener todos
        // Convertir a DTOs
        // Retornar lista
    }
    
    public void actualizarCliente(ClienteDTO dto) {
        // Validar
        // Verificar existencia
        // Actualizar
    }
    
    public void eliminarCliente(Integer id) {
        // Verificar existencia
        // Eliminar
    }
}
```

**PedidoService.java**
```java
package com.kilombo.crm.application.service;

import com.kilombo.crm.application.dto.PedidoDTO;
import com.kilombo.crm.domain.repository.PedidoRepository;
import com.kilombo.crm.domain.repository.ClienteRepository;
import java.util.List;

public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    
    public PedidoService(PedidoRepository pedidoRepository, 
                         ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }
    
    public PedidoDTO crearPedido(PedidoDTO dto) {
        // Validar cliente existe
        // Validar datos
        // Guardar
        // Retornar DTO
    }
    
    public List<PedidoDTO> obtenerPedidosPorCliente(Integer idCliente) {
        // Verificar cliente existe
        // Obtener pedidos
        // Convertir a DTOs
        // Retornar lista
    }
    
    // Resto de métodos CRUD...
}
```

**Entregables:**
- ✅ DTOs para transferencia de datos
- ✅ [`ClienteService`](src/main/java/com/kilombo/crm/application/service/ClienteService.java) completo
- ✅ [`PedidoService`](src/main/java/com/kilombo/crm/application/service/PedidoService.java) completo
- ✅ Validaciones de aplicación

---

### FASE 6: Capa de Presentación ⏱️ 3h

#### 6.1 Crear modelos de tabla

**ClienteTableModel.java**
```java
package com.kilombo.crm.presentation.table;

import com.kilombo.crm.application.dto.ClienteDTO;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClienteTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ID", "Nombre", "Apellido", "Email", "Teléfono"};
    private List<ClienteDTO> clientes = new ArrayList<>();
    
    @Override
    public int getRowCount() {
        return clientes.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClienteDTO cliente = clientes.get(rowIndex);
        switch (columnIndex) {
            case 0: return cliente.getId();
            case 1: return cliente.getNombre();
            case 2: return cliente.getApellido();
            case 3: return cliente.getEmail();
            case 4: return cliente.getTelefono();
            default: return null;
        }
    }
    
    public void setClientes(List<ClienteDTO> clientes) {
        this.clientes = clientes;
        fireTableDataChanged();
    }
    
    public ClienteDTO getClienteAt(int row) {
        return clientes.get(row);
    }
}
```

**PedidoTableModel.java** (similar)

#### 6.2 Crear diálogos de formulario

**ClienteDialog.java**
```java
package com.kilombo.crm.presentation.dialog;

import com.kilombo.crm.application.dto.ClienteDTO;
import javax.swing.*;
import java.awt.*;

public class ClienteDialog extends JDialog {
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private ClienteDTO cliente;
    private boolean confirmado = false;
    
    public ClienteDialog(Frame parent, ClienteDTO cliente) {
        super(parent, cliente == null ? "Nuevo Cliente" : "Editar Cliente", true);
        this.cliente = cliente;
        initComponents();
        if (cliente != null) {
            cargarDatos();
        }
    }
    
    private void initComponents() {
        // Crear y configurar componentes
        // Layout
        // Listeners
    }
    
    private void cargarDatos() {
        // Cargar datos del cliente en los campos
    }
    
    private boolean validarDatos() {
        // Validar campos
        // Mostrar errores si hay
        // Retornar true/false
    }
    
    public ClienteDTO getCliente() {
        if (confirmado) {
            // Crear DTO con datos del formulario
            return cliente;
        }
        return null;
    }
}
```

**PedidoDialog.java** (similar, con JComboBox para cliente)

#### 6.3 Crear paneles principales

**ClientePanel.java**
```java
package com.kilombo.crm.presentation.panel;

import com.kilombo.crm.application.service.ClienteService;
import com.kilombo.crm.application.dto.ClienteDTO;
import com.kilombo.crm.presentation.table.ClienteTableModel;
import com.kilombo.crm.presentation.dialog.ClienteDialog;
import javax.swing.*;
import java.awt.*;

public class ClientePanel extends JPanel {
    private ClienteService clienteService;
    private JTable table;
    private ClienteTableModel tableModel;
    private JButton btnAnadir;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnVerPedidos;
    
    public ClientePanel(ClienteService clienteService) {
        this.clienteService = clienteService;
        initComponents();
        cargarClientes();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Crear tabla
        tableModel = new ClienteTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Crear panel de botones
        JPanel panelBotones = new JPanel();
        btnAnadir = new JButton("Añadir");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar");
        btnVerPedidos = new JButton("Ver Pedidos");
        
        panelBotones.add(btnAnadir);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVerPedidos);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Agregar listeners
        btnAnadir.addActionListener(e -> anadirCliente());
        btnModificar.addActionListener(e -> modificarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnActualizar.addActionListener(e -> cargarClientes());
        btnVerPedidos.addActionListener(e -> verPedidos());
    }
    
    private void cargarClientes() {
        try {
            List<ClienteDTO> clientes = clienteService.listarClientes();
            tableModel.setClientes(clientes);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar clientes: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void anadirCliente() {
        ClienteDialog dialog = new ClienteDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        ClienteDTO nuevoCliente = dialog.getCliente();
        if (nuevoCliente != null) {
            try {
                clienteService.crearCliente(nuevoCliente);
                cargarClientes();
                JOptionPane.showMessageDialog(this, "Cliente creado exitosamente");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al crear cliente: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Implementar resto de métodos...
}
```

**PedidoPanel.java** (similar)

#### 6.4 Crear ventana principal

**MainFrame.java**
```java
package com.kilombo.crm.presentation;

import com.kilombo.crm.application.service.ClienteService;
import com.kilombo.crm.application.service.PedidoService;
import com.kilombo.crm.infrastructure.repository.ClienteRepositoryImpl;
import com.kilombo.crm.infrastructure.repository.PedidoRepositoryImpl;
import com.kilombo.crm.presentation.panel.ClientePanel;
import com.kilombo.crm.presentation.panel.PedidoPanel;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private ClientePanel clientePanel;
    private PedidoPanel pedidoPanel;
    
    public MainFrame() {
        initComponents();
        setTitle("KilomboCRM - Sistema de Gestión");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Inicializar servicios
        ClienteService clienteService = new ClienteService(new ClienteRepositoryImpl());
        PedidoService pedidoService = new PedidoService(
            new PedidoRepositoryImpl(), 
            new ClienteRepositoryImpl()
        );
        
        // Crear paneles
        clientePanel = new ClientePanel(clienteService);
        pedidoPanel = new PedidoPanel(pedidoService, clienteService);
        
        // Crear pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Clientes", clientePanel);
        tabbedPane.addTab("Pedidos", pedidoPanel);
        
        add(tabbedPane);
        
        // Crear menú
        crearMenu();
    }
    
    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);
        
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(itemAcerca);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
            "KilomboCRM v1.0\nSistema de Gestión de Clientes y Pedidos",
            "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}
```

**Entregables:**
- ✅ Modelos de tabla personalizados
- ✅ Diálogos de formulario con validación
- ✅ Paneles principales con CRUD
- ✅ [`MainFrame`](src/main/java/com/kilombo/crm/presentation/MainFrame.java) completo
- ✅ Interfaz funcional y usable

---

### FASE 7: Validaciones y Excepciones ⏱️ 1h

#### 7.1 Implementar validaciones en entidades
- Validar longitudes de campos
- Validar formatos (email, teléfono)
- Validar valores obligatorios

#### 7.2 Implementar validaciones en servicios
- Validar existencia de registros
- Validar reglas de negocio
- Validar integridad referencial

#### 7.3 Mejorar manejo de excepciones
- Try-catch en todos los métodos críticos
- Mensajes descriptivos
- Logging de errores

#### 7.4 Validaciones en UI
- Validación en tiempo real
- Deshabilitar botones cuando corresponda
- Mensajes claros al usuario

**Entregables:**
- ✅ Validaciones completas en todas las capas
- ✅ Manejo robusto de excepciones
- ✅ Experiencia de usuario mejorada

---

### FASE 8: Testing y Documentación ⏱️ 1h 30min

#### 8.1 Crear tests unitarios
- Tests para servicios
- Tests para validaciones
- Tests para mappers

#### 8.2 Documentación JavaDoc
- Documentar todas las clases públicas
- Documentar métodos públicos
- Incluir ejemplos de uso

#### 8.3 Actualizar README
- Instrucciones de instalación
- Guía de uso
- Troubleshooting

**Entregables:**
- ✅ Tests unitarios básicos
- ✅ JavaDoc completo
- ✅ Documentación actualizada

---

## 📊 Resumen de Tiempo Estimado

| Fase | Descripción | Tiempo |
|------|-------------|--------|
| 1 | Configuración inicial | 30 min |
| 2 | Base de datos | 45 min |
| 3 | Capa de dominio | 1h 30min |
| 4 | Capa de infraestructura | 2h |
| 5 | Capa de aplicación | 1h 30min |
| 6 | Capa de presentación | 3h |
| 7 | Validaciones y excepciones | 1h |
| 8 | Testing y documentación | 1h 30min |
| **TOTAL** | | **~12 horas** |

## ✅ Checklist de Completitud

### Requisitos Funcionales
- [ ] CRUD completo de clientes
- [ ] CRUD completo de pedidos
- [ ] Ver pedidos por cliente
- [ ] Validación de datos
- [ ] Manejo de excepciones
- [ ] Interfaz gráfica Swing
- [ ] Botones: Añadir, Modificar, Eliminar, Listar

### Requisitos Técnicos
- [ ] Base de datos MySQL creada
- [ ] Tablas con estructura correcta
- [ ] 5+ registros por tabla
- [ ] Conector JDBC configurado
- [ ] Clase [`ConexionBD`](src/main/java/com/kilombo/crm/infrastructure/database/ConexionBD.java) implementada
- [ ] Operaciones CRUD funcionando

### Requisitos de Calidad
- [ ] Clean Architecture aplicada
- [ ] Clean Code aplicado
- [ ] Código documentado
- [ ] Excepciones manejadas
- [ ] Validaciones implementadas
- [ ] Tests básicos creados

## 🎯 Criterios de Aceptación

1. **Funcionalidad**: Todas las operaciones CRUD funcionan correctamente
2. **Validación**: Los datos se validan antes de guardar
3. **Excepciones**: Los errores se manejan y muestran apropiadamente
4. **UI**: La interfaz es intuitiva y responsive
5. **Arquitectura**: El código sigue Clean Architecture
6. **Código**: El código es limpio, legible y mantenible
7. **BD**: La base de datos está correctamente estructurada
8. **Documentación**: El proyecto está bien documentado

## 🚀 Próximos Pasos

Una vez completada la implementación:

1. **Revisar** el código completo
2. **Probar** todas las funcionalidades
3. **Documentar** cualquier decisión de diseño
4. **Preparar** presentación del proyecto
5. **Considerar** mejoras futuras

## 📝 Notas Importantes

- Seguir el orden de las fases para evitar dependencias circulares
- Probar cada capa antes de pasar a la siguiente
- Hacer commits frecuentes con mensajes descriptivos
- Mantener la separación de responsabilidades
- No mezclar lógica de negocio con lógica de presentación
- Usar PreparedStatement para prevenir SQL injection
- Cerrar recursos (Connection, Statement, ResultSet) apropiadamente

---

**¡Éxito en la implementación!** 🎉