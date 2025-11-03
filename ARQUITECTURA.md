# Arquitectura del Sistema KilomboCRM

## Visión General

KilomboCRM es un sistema de gestión de clientes y pedidos implementado siguiendo los principios de **Clean Architecture** (Arquitectura Limpia) propuestos por Robert C. Martin. Esta arquitectura garantiza la separación de responsabilidades, facilita el mantenimiento, testing y evolución del sistema.

## Principios Arquitectónicos

### 1. Separación de Responsabilidades
- **Regla de Dependencia**: Las dependencias apuntan hacia adentro, nunca hacia afuera. Los círculos internos no conocen nada de los círculos externos.
- **Principio de Inversión de Dependencias**: Los módulos de alto nivel no dependen de módulos de bajo nivel. Ambos dependen de abstracciones.

### 2. Capas Arquitectónicas

```
┌─────────────────────────────────────┐
│         PRESENTATION LAYER          │  ← Interfaz de Usuario (Swing)
│   (Frameworks & Drivers)            │
├─────────────────────────────────────┤
│         APPLICATION LAYER           │  ← Casos de Uso & Servicios
│   (Use Cases & Business Rules)      │
├─────────────────────────────────────┤
│         DOMAIN LAYER                │  ← Entidades & Reglas de Negocio
│   (Entities & Business Rules)       │
├─────────────────────────────────────┤
│         INFRASTRUCTURE LAYER        │  ← Persistencia & Frameworks
│   (Database & External Concerns)    │
└─────────────────────────────────────┘
```

## Detalle de Cada Capa

### 1. Capa de Dominio (Domain Layer)

**Ubicación**: `src/main/java/com/kilombo/crm/domain/`

**Responsabilidades**:
- Contiene las reglas de negocio puras.
- Define las entidades principales del sistema.
- Establece contratos (interfaces) para la persistencia.
- Es independiente de frameworks externos.

#### Entidades Principales

**Cliente** (`Cliente.java`):
```java
public class Cliente {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    // Reglas de negocio: validaciones de email, teléfono, etc.
    public boolean isEmailValid() { /* implementación */ }
    public boolean isTelefonoValid() { /* implementación */ }
}
```
Esta entidad encapsula toda la lógica de negocio relacionada con clientes, incluyendo validaciones de formato de email y teléfono. Contribuye a la robustez del sistema al centralizar las reglas de negocio en el dominio, evitando duplicación de lógica de validación en diferentes capas y facilitando cambios futuros en las reglas de negocio sin afectar otras partes del sistema.

**Pedido** (`Pedido.java`):
```java
public class Pedido {
    private Integer id;
    private Integer idCliente;
    private LocalDate fecha;
    private BigDecimal total;
    private EstadoPedido estado;

    // Reglas de negocio: cálculo de totales, estados válidos
    public void calcularTotal() { /* implementación */ }
    public boolean puedeSerCancelado() { /* implementación */ }
}
```
Representa la lógica de negocio de pedidos, incluyendo transiciones de estado válidas y cálculos de totales. Mejora la robustez al validar automáticamente las reglas de negocio (como no poder cancelar pedidos ya completados) y mantener la consistencia de datos, previniendo estados inválidos que podrían corromper la integridad de la información comercial.

#### Interfaces de Repositorio

**ClienteRepository**:
```java
public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Integer id);
    List<Cliente> findAll();
    void update(Cliente cliente);
    void deleteById(Integer id);
    boolean existsByEmail(String email);
}
```
Esta interfaz abstrae el acceso a datos de clientes, permitiendo cambiar la implementación de persistencia (MySQL, PostgreSQL, memoria) sin afectar la lógica de negocio. Contribuye a la robustez al proporcionar un contrato claro que garantiza consistencia en las operaciones de datos y facilita el testing mediante mocks.

**PedidoRepository**:
```java
public interface PedidoRepository {
    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Integer id);
    List<Pedido> findAll();
    List<Pedido> findByClienteId(Integer idCliente);
    void update(Pedido pedido);
    void deleteById(Integer id);
    int countByClienteId(Integer idCliente);
    BigDecimal sumTotalByClienteId(Integer idCliente);
}
```
Define el contrato para operaciones de pedidos, incluyendo consultas especializadas como búsqueda por cliente y cálculos agregados. Mejora la robustez del sistema al centralizar todas las operaciones de datos de pedidos, facilitando optimizaciones de rendimiento y mantenimiento de consultas complejas en un solo lugar.

#### Excepciones de Dominio

- `ClienteNotFoundException`: Cliente no encontrado.
- `PedidoNotFoundException`: Pedido no encontrado.
- `DatabaseException`: Errores de base de datos.
- `ValidationException`: Errores de validación de negocio.

### 2. Capa de Aplicación (Application Layer)

**Ubicación**: `src/main/java/com/kilombo/crm/application/`

**Responsabilidades**:
- Coordina la ejecución de casos de uso.
- Contiene la lógica de aplicación (no de negocio puro).
- Gestiona transacciones y orquestación.
- Transforma datos entre capas usando DTOs.

#### Servicios

**ClienteService**:
```java
@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteDTO crearCliente(ClienteDTO dto) {
        // Validación de negocio
        validarCliente(dto);

        // Transformación DTO → Entity
        Cliente cliente = ClienteMapper.toEntity(dto);

        // Persistencia
        Cliente guardado = clienteRepository.save(cliente);

        // Transformación Entity → DTO
        return ClienteMapper.toDTO(guardado);
    }

    public List<ClienteDTO> listarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(ClienteMapper::toDTO)
                .collect(Collectors.toList());
    }
}
```
Orquesta las operaciones de negocio de clientes, coordinando validaciones, transformaciones de datos y persistencia. Contribuye a la robustez al centralizar la lógica de aplicación, manejar transacciones de manera consistente y proporcionar una interfaz clara para la capa de presentación, facilitando cambios en las reglas de negocio sin afectar otras capas.

**PedidoService**:
```java
@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoDTO crearPedido(PedidoDTO dto) {
        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new ClienteNotFoundException(dto.getIdCliente()));

        // Calcular total si no viene especificado
        if (dto.getTotal() == null) {
            dto.setTotal(calcularTotalPedido(dto));
        }

        // Transformación y persistencia
        Pedido pedido = PedidoMapper.toEntity(dto);
        Pedido guardado = pedidoRepository.save(pedido);

        return PedidoMapper.toDTO(guardado);
    }
}
```
Gestiona la lógica compleja de pedidos, incluyendo validaciones cruzadas entre entidades (verificar existencia de cliente) y cálculos automáticos. Mejora la robustez al garantizar la integridad referencial, manejar cálculos de totales de forma centralizada y proporcionar una capa de abstracción que facilita el mantenimiento y evolución de las reglas de negocio de pedidos.

#### DTOs (Data Transfer Objects)

**ClienteDTO**:
```java
public class ClienteDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    // Validaciones de entrada
    @NotBlank @Size(max = 100)
    private String nombre;

    @NotBlank @Email
    private String email;
}
```
Transfiere datos de clientes entre capas sin exponer la entidad de dominio. Contribuye a la robustez al desacoplar la estructura interna del dominio de los contratos externos, permitiendo cambios en la entidad sin afectar APIs y facilitando validaciones específicas de entrada/salida.

**PedidoDTO**:
```java
public class PedidoDTO {
    private Integer id;
    private Integer idCliente;
    private LocalDate fecha;
    private BigDecimal total;
    private EstadoPedido estado;

    // Relación con cliente
    private ClienteDTO cliente;
}
```
Facilita la transferencia de datos complejos de pedidos incluyendo relaciones. Mejora la robustez al proporcionar una vista controlada de los datos, optimizar serialización para APIs y permitir validaciones específicas de transporte de datos sin comprometer la lógica de dominio.

### 3. Capa de Infraestructura (Infrastructure Layer)

**Ubicación**: `src/main/java/com/kilombo/crm/infrastructure/`

**Responsabilidades**:
- Implementa las interfaces definidas en el dominio.
- Maneja la persistencia de datos.
- Gestiona conexiones externas (BD, APIs, etc.).
- Contiene adaptadores para frameworks externos.

#### Persistencia - Repositorios

**ClienteRepositoryImpl**:
```java
@Repository
public class ClienteRepositoryImpl implements ClienteRepository {
    private static final Logger logger = Logger.getLogger(ClienteRepositoryImpl.class.getName());

    @Override
    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, apellido, email, telefono) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ClienteMapper.toStatement(stmt, cliente);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("No se pudo guardar el cliente");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getGeneratedKeys().getInt(1));
                }
            }

            return cliente;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al guardar cliente", e);
            throw new DatabaseException("Error al guardar el cliente", e);
        }
    }
}
```
Implementa el acceso a datos de clientes usando JDBC puro. Contribuye a la robustez mediante manejo exhaustivo de errores, uso de PreparedStatements para prevenir SQL injection, logging detallado para debugging y gestión automática de recursos con try-with-resources, garantizando conexiones seguras y eficientes.

#### Mappers

**ClienteMapper**:
```java
public class ClienteMapper {
    public static ClienteDTO toDTO(Cliente entity) {
        return ClienteDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .build();
    }

    public static Cliente toEntity(ClienteDTO dto) {
        return Cliente.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .build();
    }

    public static void toStatement(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNombre());
        stmt.setString(2, cliente.getApellido());
        stmt.setString(3, cliente.getEmail());
        stmt.setString(4, cliente.getTelefono());
    }

    public static Cliente fromResultSet(ResultSet rs) throws SQLException {
        return Cliente.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .apellido(rs.getString("apellido"))
                .email(rs.getString("email"))
                .telefono(rs.getString("telefono"))
                .build();
    }
}
```
Convierte entre objetos de dominio, DTOs y estructuras de BD. Contribuye a la robustez al centralizar las transformaciones de datos, evitar código duplicado en conversiones, manejar tipos de datos específicos de cada capa y facilitar el mantenimiento cuando cambian los esquemas de BD o contratos de API.

#### Conexión a Base de Datos

**ConexionBD** (Singleton Pattern):
```java
public class ConexionBD {
    private static ConexionBD instance;
    private Connection connection;

    private ConexionBD() {
        // Cargar configuración desde application.properties
        Properties properties = new Properties();
        // Configurar URL, usuario, password, driver

        // Cargar driver JDBC
        Class.forName(driver);

        // Configurar conexión con pool y reintentos
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

    public Connection getConnection() {
        // Implementar reintentos, validación de esquema, etc.
    }
}
```
Gestiona conexiones a BD de forma centralizada y thread-safe. Contribuye a la robustez mediante patrón Singleton para evitar múltiples conexiones simultáneas, reintentos automáticos en fallos de conexión, validación de esquema al conectar, timeouts configurables y pool de conexiones eficiente, garantizando alta disponibilidad y rendimiento.

### 4. Capa de Presentación (Presentation Layer)

**Ubicación**: `src/main/java/com/kilombo/crm/presentation/`

**Responsabilidades**:
- Gestiona la interfaz de usuario.
- Maneja eventos del usuario.
- Coordina con la capa de aplicación.
- Presenta datos al usuario.

#### Componentes Swing

**MainFrame**:
```java
public class MainFrame extends JFrame {
    private ClienteService clienteService;
    private PedidoService pedidoService;

    public MainFrame() {
        initServices();
        initComponents();
        setupEventHandlers();
    }

    private void initServices() {
        // Inyección de dependencias manual
        ClienteRepository clienteRepo = new ClienteRepositoryImpl();
        PedidoRepository pedidoRepo = new PedidoRepositoryImpl();

        this.clienteService = new ClienteService(clienteRepo);
        this.pedidoService = new PedidoService(pedidoRepo, clienteRepo);
    }
}
```
Ventana principal que coordina toda la aplicación Swing. Contribuye a la robustez mediante inyección manual de dependencias que facilita testing, gestión centralizada de servicios, manejo de eventos de ventana y coordinación entre diferentes paneles, proporcionando una experiencia de usuario coherente y manejable.

**ClientePanel**:
```java
public class ClientePanel extends JPanel {
    private JTable tablaClientes;
    private ClienteTableModel tableModel;
    private JButton btnNuevo, btnEditar, btnEliminar;

    public ClientePanel(ClienteService clienteService) {
        this.clienteService = clienteService;
        initComponents();
        cargarClientes();
    }

    private void cargarClientes() {
        List<ClienteDTO> clientes = clienteService.listarClientes();
        tableModel.setClientes(clientes);
    }
}
```
Panel especializado en gestión de clientes con interfaz rica. Mejora la robustez al proporcionar validación visual de datos, manejo de errores de usuario, actualización automática de vistas tras operaciones CRUD y separación clara entre lógica de presentación y negocio, facilitando mantenimiento y evolución de la interfaz.

## Patrones de Diseño Implementados

### 1. Patrón Repository
- Abstrae el acceso a datos.
- Permite cambiar la implementación de persistencia sin afectar el dominio.
- Facilita testing con mocks.

### 2. Patrón Service Layer
- Coordina operaciones complejas.
- Maneja transacciones.
- Centraliza lógica de aplicación.

### 3. Patrón DTO
- Transfiere datos entre capas.
- Evita exponer entidades de dominio.
- Optimiza serialización.

### 4. Patrón Singleton
- Gestiona instancia única de conexión a BD.
- Thread-safe con doble verificación.

### 5. Patrón Factory
- Crea instancias de servicios y repositorios.
- Centraliza configuración de dependencias.

### 6. Patrón Observer
- Maneja eventos en la interfaz gráfica.
- Separa lógica de presentación de lógica de negocio.

## Configuración y Dependencias

### application.properties
```properties
# Base de datos
db.url=jdbc:mysql://localhost:3306/kilombo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
db.username=admin
db.password=admin
db.driver=com.mysql.cj.jdbc.Driver

# Pool de conexiones
db.initialSize=5
db.maxActive=20
db.maxIdle=10
db.minIdle=5
db.maxWait=10000
```

### pom.xml
```xml
<dependencies>
    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- Validación -->
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>6.2.5.Final</version>
    </dependency>
</dependencies>
```

## Flujo de Datos Típico

1. **Usuario interactúa con UI** → `ClientePanel` captura evento.
2. **UI llama a Servicio** → `ClienteService.crearCliente(dto)`.
3. **Servicio valida y transforma** → Convierte DTO a Entity.
4. **Servicio llama a Repositorio** → `ClienteRepository.save(entity)`.
5. **Repositorio ejecuta SQL** → `ClienteRepositoryImpl.save()` usa JDBC.
6. **BD persiste datos** → MySQL guarda el registro.
7. **Respuesta fluye de vuelta** → Entity → DTO → UI.

## Ventajas de Esta Arquitectura

### Mantenibilidad
- Cambios en UI no afectan negocio.
- Cambios en BD no afectan dominio.
- Código modular y fácil de entender.

### Testabilidad
- Cada capa se puede testear independientemente.
- Mocks para repositorios en tests de servicios.
- Tests de integración para flujos completos.

### Escalabilidad
- Nuevas funcionalidades se agregan sin afectar existentes.
- Capas se pueden escalar independientemente.
- Fácil migración a microservicios.

### Flexibilidad
- BD se puede cambiar (MySQL → PostgreSQL) sin tocar dominio.
- UI se puede cambiar (Swing → Web) sin afectar negocio.
- Nuevos casos de uso se agregan fácilmente.

## Consideraciones de Implementación

### Manejo de Errores
- Excepciones específicas por capa.
- Logging centralizado.
- Transacciones rollback en errores.

### Seguridad
- Validación de entrada en todas las capas.
- PreparedStatements para prevenir SQL injection.
- Control de acceso (preparado para futura implementación).

### Rendimiento
- Pool de conexiones configurado.
- Consultas optimizadas con índices.
- Lazy loading donde aplica.

## 🔍 Análisis de Calidad y Áreas de Mejora

### ✅ Fortalezas Arquitectónicas
- **Separación de Capas**: Clean Architecture correctamente implementada
- **Principios SOLID**: Bien aplicados en la mayoría de componentes
- **Abstracciones**: Interfaces claras para repositorios y servicios
- **Encapsulamiento**: Lógica de negocio protegida en entidades

### 🚨 Problemas Críticos Identificados

#### 1. Duplicación Masiva de Código
**Ubicación**: Todos los repositorios (`ClienteRepositoryImpl`, `PedidoRepositoryImpl`, etc.)
**Problema**: Patrón try-catch con logging repetido 49+ veces
```java
try {
    // operación
} catch (SQLException e) {
    logger.log(Level.SEVERE, "Error SQL en " + operationName + ": " + e.getMessage(), e);
    throw new DatabaseException("Error en " + operationName + ": " + e.getMessage(), e);
} catch (Exception e) {
    logger.log(Level.SEVERE, "Error inesperado en " + operationName + ": " + e.getMessage(), e);
    throw new DatabaseException("Error inesperado en " + operationName + ": " + e.getMessage(), e);
}
```

**Solución Recomendada**: Implementar patrón Template Method
```java
public abstract class BaseRepository {
    protected <T> T executeWithErrorHandling(Supplier<T> operation, String operationName) {
        // Implementación centralizada del manejo de errores
    }
}
```

#### 2. Violación SRP en MainFrame
**Ubicación**: `MainFrame.java` (509 líneas)
**Problema**: Una clase maneja navegación, configuración de UI, coordinación de servicios
**Solución**: Dividir en clases especializadas
- `NavigationController`: Manejar navegación entre módulos
- `ActionPanelManager`: Gestionar paneles de acciones dinámicas
- `ModuleCoordinator`: Coordinar inicialización de módulos

#### 3. Validaciones Faltantes en DetallePedido
**Ubicación**: `DetallePedido.java`
**Problema**: No tiene reglas de negocio (cantidad > 0, precios válidos)
**Solución**: Agregar método `validar()` similar a `Cliente` y `Pedido`

#### 4. ConexionBD Sobrecargada
**Ubicación**: `ConexionBD.java` (496 líneas)
**Problema**: Maneja conexión, configuración, validación y logging
**Solución**: Dividir en clases especializadas
- `ConnectionFactory`: Crear conexiones
- `ConnectionValidator`: Validar esquema y estado
- `DatabaseConfigurator`: Gestionar configuración

### ⚠️ Mejoras Recomendadas

#### Inyección de Dependencias
**Estado Actual**: Instanciación manual en MainFrame
```java
// Actual
ClienteRepository repo = new ClienteRepositoryImpl();

// Recomendado
@Inject
private ClienteRepository clienteRepository;
```

#### Framework de Logging
**Estado Actual**: `java.util.logging` básico
**Recomendado**: SLF4J con Logback para configuración avanzada

#### Tests Unitarios
**Estado Actual**: 0% cobertura
**Recomendado**: JUnit 5 + Mockito para tests de servicios y repositorios

### 📊 Métricas de Calidad

| Aspecto | Estado | Puntuación |
|---------|--------|------------|
| Arquitectura | Excelente | 9/10 |
| SOLID Compliance | Bueno | 8/10 |
| Clean Code | Regular | 6/10 |
| Mantenibilidad | Media | 7/10 |
| Testability | Baja | 3/10 |

### 🎯 Plan de Refactorización Priorizado

#### Fase 1: Crítico (1-2 semanas)
1. Implementar BaseRepository con manejo centralizado de errores
2. Refactorizar MainFrame (dividir responsabilidades)
3. Agregar validaciones a DetallePedido

#### Fase 2: Importante (2-3 semanas)
1. Dividir ConexionBD en clases más pequeñas
2. Implementar framework de DI básico
3. Mejorar sistema de logging

#### Fase 3: Futuro (3-4 semanas)
1. Agregar tests unitarios
2. Implementar documentación JavaDoc completa
3. Migrar a Spring Boot (opcional)

Esta arquitectura proporciona una base sólida para el crecimiento y mantenimiento del sistema KilomboCRM, siguiendo las mejores prácticas de desarrollo de software empresarial.