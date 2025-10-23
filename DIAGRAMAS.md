
# Diagramas del Sistema KilomboCRM

Este documento contiene los diagramas visuales que representan la arquitectura y flujos del sistema.

## 1. Arquitectura en Capas

```mermaid
graph TB
    subgraph Presentation["🖥️ CAPA DE PRESENTACIÓN"]
        MainFrame[MainFrame]
        ClientePanel[ClientePanel]
        PedidoPanel[PedidoPanel]
        ClienteDialog[ClienteDialog]
        PedidoDialog[PedidoDialog]
        TableModels[Table Models]
    end

    subgraph Application["⚙️ CAPA DE APLICACIÓN"]
        ClienteService[ClienteService]
        PedidoService[PedidoService]
        DTOs[DTOs]
    end

    subgraph Domain["💎 CAPA DE DOMINIO"]
        Cliente[Cliente Entity]
        Pedido[Pedido Entity]
        ClienteRepo[ClienteRepository Interface]
        PedidoRepo[PedidoRepository Interface]
        Exceptions[Domain Exceptions]
    end

    subgraph Infrastructure["🔧 CAPA DE INFRAESTRUCTURA"]
        ConexionBD[ConexionBD Singleton]
        ClienteDAO[ClienteRepositoryImpl]
        PedidoDAO[PedidoRepositoryImpl]
        Mappers[Mappers]
        MySQL[(MySQL Database)]
    end

    MainFrame --> ClientePanel
    MainFrame --> PedidoPanel
    ClientePanel --> ClienteDialog
    PedidoPanel --> PedidoDialog
    ClientePanel --> TableModels
    PedidoPanel --> TableModels

    ClientePanel --> ClienteService
    PedidoPanel --> PedidoService
    ClienteService --> DTOs
    PedidoService --> DTOs

    ClienteService --> ClienteRepo
    PedidoService --> PedidoRepo
    ClienteService --> Cliente
    PedidoService --> Pedido
    ClienteRepo --> Exceptions
    PedidoRepo --> Exceptions

    ClienteDAO -.implements.-> ClienteRepo
    PedidoDAO -.implements.-> PedidoRepo
    ClienteDAO --> ConexionBD
    PedidoDAO --> ConexionBD
    ClienteDAO --> Mappers
    PedidoDAO --> Mappers
    ConexionBD --> MySQL

    style Presentation fill:#e1f5ff
    style Application fill:#fff4e1
    style Domain fill:#f0e1ff
    style Infrastructure fill:#e1ffe1
```

## 2. Flujo de Creación de Cliente

```mermaid
sequenceDiagram
    actor Usuario
    participant UI as ClientePanel
    participant Dialog as ClienteDialog
    participant Service as ClienteService
    participant Entity as Cliente
    participant Repo as ClienteRepository
    participant DAO as ClienteRepositoryImpl
    participant DB as MySQL

    Usuario->>UI: Click en Añadir
    UI->>Dialog: new ClienteDialog()
    Dialog->>Usuario: Mostrar formulario
    Usuario->>Dialog: Completar datos
    Dialog->>Dialog: validarDatos()
    
    alt Datos válidos
        Dialog->>UI: return ClienteDTO
        UI->>Service: crearCliente(dto)
        Service->>Service: validar negocio
        Service->>Entity: new Cliente(datos)
        Entity->>Entity: validar entidad
        Service->>Repo: save(cliente)
        Repo->>DAO: save(cliente)
        DAO->>DB: INSERT INTO cliente
        DB-->>DAO: ID generado
        DAO-->>Repo: Cliente con ID
        Repo-->>Service: Cliente guardado
        Service-->>UI: ClienteDTO
        UI->>UI: cargarClientes()
        UI->>Usuario: Mensaje éxito
    else Datos inválidos
        Dialog->>Usuario: Mostrar errores
    end
```

## 3. Flujo de Consulta de Pedidos por Cliente

```mermaid
sequenceDiagram
    actor Usuario
    participant UI as ClientePanel
    participant PedidoPanel as PedidoPanel
    participant Service as PedidoService
    participant Repo as PedidoRepository
    participant DAO as PedidoRepositoryImpl
    participant DB as MySQL

    Usuario->>UI: Seleccionar cliente
    Usuario->>UI: Click Ver Pedidos
    UI->>UI: obtener cliente seleccionado
    UI->>PedidoPanel: mostrar con filtro
    PedidoPanel->>Service: obtenerPedidosPorCliente(idCliente)
    Service->>Repo: findByClienteId(idCliente)
    Repo->>DAO: findByClienteId(idCliente)
    DAO->>DB: SELECT * FROM pedido WHERE id_cliente = ?
    DB-->>DAO: ResultSet
    DAO->>DAO: mapear resultados
    DAO-->>Repo: List de Pedidos
    Repo-->>Service: List de Pedidos
    Service->>Service: convertir a DTOs
    Service-->>PedidoPanel: List de PedidoDTO
    PedidoPanel->>PedidoPanel: actualizar tabla
    PedidoPanel->>Usuario: Mostrar pedidos
```

## 4. Modelo de Datos

```mermaid
erDiagram
    CLIENTE ||--o{ PEDIDO : tiene
    
    CLIENTE {
        int id PK
        varchar nombre
        varchar apellido
        varchar email UK
        varchar telefono
    }
    
    PEDIDO {
        int id PK
        int id_cliente FK
        date fecha
        double total
    }
```

## 5. Diagrama de Clases - Dominio

```mermaid
classDiagram
    class Cliente {
        -Integer id
        -String nombre
        -String apellido
        -String email
        -String telefono
        +Cliente(nombre, apellido, email, telefono)
        +validar() void
        +getId() Integer
        +getNombre() String
        +getApellido() String
        +getEmail() String
        +getTelefono() String
    }

    class Pedido {
        -Integer id
        -Integer idCliente
        -LocalDate fecha
        -Double total
        +Pedido(idCliente, fecha, total)
        +validar() void
        +getId() Integer
        +getIdCliente() Integer
        +getFecha() LocalDate
        +getTotal() Double
    }

    class ClienteRepository {
        <<interface>>
        +save(Cliente) Cliente
        +findById(Integer) Optional~Cliente~
        +findAll() List~Cliente~
        +update(Cliente) void
        +deleteById(Integer) void
        +existsByEmail(String) boolean
    }

    class PedidoRepository {
        <<interface>>
        +save(Pedido) Pedido
        +findById(Integer) Optional~Pedido~
        +findAll() List~Pedido~
        +findByClienteId(Integer) List~Pedido~
        +update(Pedido) void
        +deleteById(Integer) void
    }

    class ValidationException {
        -String message
        +ValidationException(message)
    }

    class ClienteNotFoundException {
        -Integer id
        +ClienteNotFoundException(id)
    }

    class PedidoNotFoundException {
        -Integer id
        +PedidoNotFoundException(id)
    }

    Cliente --> ValidationException : throws
    Pedido --> ValidationException : throws
    ClienteRepository --> Cliente : manages
    ClienteRepository --> ClienteNotFoundException : throws
    PedidoRepository --> Pedido : manages
    PedidoRepository --> PedidoNotFoundException : throws
```

## 6. Diagrama de Clases - Aplicación

```mermaid
classDiagram
    class ClienteService {
        -ClienteRepository repository
        +ClienteService(repository)
        +crearCliente(ClienteDTO) ClienteDTO
        +obtenerCliente(Integer) ClienteDTO
        +listarClientes() List~ClienteDTO~
        +actualizarCliente(ClienteDTO) void
        +eliminarCliente(Integer) void
    }

    class PedidoService {
        -PedidoRepository pedidoRepo
        -ClienteRepository clienteRepo
        +PedidoService(pedidoRepo, clienteRepo)
        +crearPedido(PedidoDTO) PedidoDTO
        +obtenerPedido(Integer) PedidoDTO
        +listarPedidos() List~PedidoDTO~
        +obtenerPedidosPorCliente(Integer) List~PedidoDTO~
        +actualizarPedido(PedidoDTO) void
        +eliminarPedido(Integer) void
    }

    class ClienteDTO {
        -Integer id
        -String nombre
        -String apellido
        -String email
        -String telefono
        +fromEntity(Cliente) ClienteDTO
        +toEntity() Cliente
    }

    class PedidoDTO {
        -Integer id
        -Integer idCliente
        -LocalDate fecha
        -Double total
        +fromEntity(Pedido) PedidoDTO
        +toEntity() Pedido
    }

    ClienteService --> ClienteDTO : uses
    PedidoService --> PedidoDTO : uses
    ClienteService --> ClienteRepository : depends on
    PedidoService --> PedidoRepository : depends on
    PedidoService --> ClienteRepository : depends on
```

## 7. Diagrama de Clases - Infraestructura

```mermaid
classDiagram
    class ConexionBD {
        -ConexionBD instance
        -Connection connection
        -Properties properties
        -ConexionBD()
        +getInstance() ConexionBD
        +getConnection() Connection
        +closeConnection() void
    }

    class ClienteRepositoryImpl {
        +save(Cliente) Cliente
        +findById(Integer) Optional~Cliente~
        +findAll() List~Cliente~
        +update(Cliente) void
        +deleteById(Integer) void
        +existsByEmail(String) boolean
    }

    class PedidoRepositoryImpl {
        +save(Pedido) Pedido
        +findById(Integer) Optional~Pedido~
        +findAll() List~Pedido~
        +findByClienteId(Integer) List~Pedido~
        +update(Pedido) void
        +deleteById(Integer) void
    }

    class ClienteMapper {
        +fromResultSet(ResultSet) Cliente
        +toStatement(PreparedStatement, Cliente) void
    }

    class PedidoMapper {
        +fromResultSet(ResultSet) Pedido
        +toStatement(PreparedStatement, Pedido) void
    }

    ClienteRepositoryImpl ..|> ClienteRepository : implements
    PedidoRepositoryImpl ..|> PedidoRepository : implements
    ClienteRepositoryImpl --> ConexionBD : uses
    PedidoRepositoryImpl --> ConexionBD : uses
    ClienteRepositoryImpl --> ClienteMapper : uses
    PedidoRepositoryImpl --> PedidoMapper : uses
```

## 8. Diagrama de Componentes UI

```mermaid
graph TB
    subgraph MainFrame["MainFrame JFrame"]
        MenuBar[JMenuBar]
        TabbedPane[JTabbedPane]
    end

    subgraph ClienteTab["Tab Clientes"]
        ClientePanel[ClientePanel]
        ClienteTable[JTable con ClienteTableModel]
        ClienteBotones[Panel de Botones]
    end

    subgraph PedidoTab["Tab Pedidos"]
        PedidoPanel[PedidoPanel]
        PedidoTable[JTable con PedidoTableModel]
        PedidoBotones[Panel de Botones]
    end

    subgraph Dialogs["Diálogos Modales"]
        ClienteDialog[ClienteDialog]
        PedidoDialog[PedidoDialog]
    end

    MainFrame --> MenuBar
    MainFrame --> TabbedPane
    TabbedPane --> ClienteTab
    TabbedPane --> PedidoTab
    ClientePanel --> ClienteTable
    ClientePanel --> ClienteBotones
    PedidoPanel --> PedidoTable
    PedidoPanel --> PedidoBotones
    ClienteBotones -.abre.-> ClienteDialog
    PedidoBotones -.abre.-> PedidoDialog

    style MainFrame fill:#e3f2fd
    style ClienteTab fill:#f3e5f5
    style PedidoTab fill:#e8f5e9
    style Dialogs fill:#fff3e0
```

## 9. Flujo de Manejo de Excepciones

```mermaid
graph TD
    A[Operación Iniciada] --> B{Validación UI}
    B -->|Inválido| C[Mostrar Error UI]
    B -->|Válido| D[Llamar Servicio]
    D --> E{Validación Negocio}
    E -->|Inválido| F[ValidationException]
    E -->|Válido| G[Llamar Repositorio]
    G --> H{Operación BD}
    H -->|Error SQL| I[DatabaseException]
    H -->|No Encontrado| J[NotFoundException]
    H -->|Éxito| K[Retornar Resultado]
    
    F --> L[Capturar en Servicio]
    I --> L
    J --> L
    L --> M[Transformar Mensaje]
    M --> N[Retornar a UI]
    N --> O[Mostrar JOptionPane]
    
    K --> P[Actualizar UI]
    C --> Q[Usuario Corrige]
    O --> Q
    Q --> A

    style A fill:#e1f5fe
    style K fill:#c8e6c9
    style P fill:#c8e6c9
    style F fill:#ffcdd2
    style I fill:#ffcdd2
    style J fill:#ffcdd2
    style O fill:#fff9c4
```

## 10. Patrón Singleton - ConexionBD

```mermaid
classDiagram
    class ConexionBD {
        -static ConexionBD instance
        -Connection connection
        -Properties properties
        -ConexionBD()
        +static getInstance() ConexionBD
        +getConnection() Connection
        +closeConnection() void
        -loadProperties() void
        -createConnection() void
    }

    note for ConexionBD "Singleton Pattern
    - Constructor privado
    - Instancia única
    - Thread-safe con synchronized
    - Lazy initialization"
```

## 11. Ciclo de Vida de una Transacción

```mermaid
stateDiagram-v2
    [*] --> Iniciada: Usuario inicia operación
    Iniciada --> Validando: Enviar datos
    Validando --> Procesando: Datos válidos
    Validando --> Error: Datos inválidos
    Procesando --> Conectando: Llamar BD
    Conectando --> Ejecutando: Conexión OK
    Conectando --> Error: Conexión fallida
    Ejecutando --> Confirmando: SQL OK
    Ejecutando --> Rollback: SQL Error
    Confirmando --> Completada: Commit
    Rollback --> Error: Deshacer cambios
    Completada --> [*]: Actualizar UI
    Error --> [*]: Mostrar mensaje
```

## 12. Estructura de Paquetes

```mermaid
graph LR
    Root[com.kilombo.crm] --> Domain[domain]
    Root --> Application[application]
    Root --> Infrastructure[infrastructure]
    Root --> Presentation[presentation]

    Domain --> DomainModel[model]
    Domain --> DomainRepo[repository]
    Domain --> DomainExc[exception]

    Application --> AppService[service]
    Application --> AppDTO[dto]

    Infrastructure --> InfraDB[database]
    Infrastructure --> InfraRepo[repository]
    Infrastructure --> InfraMapper[mapper]

    Presentation --> PresPanel[panel]
    Presentation --> PresDialog[dialog]
    Presentation --> PresTable[table]
    Presentation --> PresUtil[util]

    style Root fill:#1976d2,color:#fff
    style Domain fill:#7b1fa2,color:#fff
    style Application fill:#f57c00,color:#fff
    style Infrastructure fill:#388e3c,color:#fff
    style Presentation fill:#0097a7,color:#fff
```

## 13. Dependencias entre Capas

```mermaid
graph BT
    Infrastructure[Infraestructura] -.implementa.-> Domain[Dominio]
    Application[Aplicación] --> Domain
    Presentation[Presentación] --> Application
    Presentation -.usa DTOs.-> Application
    Application -.usa interfaces.-> Domain
    Infrastructure -.usa entidades.-> Domain

    style Domain fill:#f0e1ff
    style Application fill:#fff4e1
    style Infrastructure fill:#e1ffe1
    style Presentation fill:#e1f5ff
```

## Conclusión

Estos diagramas proporcionan una visión completa de:
- ✅ La arquitectura en capas del