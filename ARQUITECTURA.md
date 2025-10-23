# Arquitectura del Proyecto KilomboCRM

## 1. Visión General

KilomboCRM es una aplicación de escritorio Java que implementa un sistema de gestión de clientes y pedidos siguiendo los principios de **Clean Architecture** y **Clean Code**.

## 2. Principios Arquitectónicos

### Clean Architecture
La aplicación se estructura en capas concéntricas donde las dependencias apuntan hacia el centro:

```
┌─────────────────────────────────────────┐
│     Presentación (UI - Swing)           │
├─────────────────────────────────────────┤
│     Aplicación (Casos de Uso)           │
├─────────────────────────────────────────┤
│     Dominio (Entidades + Interfaces)    │
├─────────────────────────────────────────┤
│     Infraestructura (BD + DAOs)         │
└─────────────────────────────────────────┘
```

### Clean Code
- Nombres descriptivos y significativos
- Funciones pequeñas con una única responsabilidad
- Comentarios solo cuando sea necesario
- Manejo adecuado de excepciones
- Código DRY (Don't Repeat Yourself)

## 3. Estructura del Proyecto

```
KilomboCRM/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── kilombo/
│   │   │           └── crm/
│   │   │               ├── domain/              # Capa de Dominio
│   │   │               │   ├── model/           # Entidades
│   │   │               │   │   ├── Cliente.java
│   │   │               │   │   └── Pedido.java
│   │   │               │   ├── repository/      # Interfaces (puertos)
│   │   │               │   │   ├── ClienteRepository.java
│   │   │               │   │   └── PedidoRepository.java
│   │   │               │   └── exception/       # Excepciones de dominio
│   │   │               │       ├── ClienteNotFoundException.java
│   │   │               │       ├── PedidoNotFoundException.java
│   │   │               │       └── ValidationException.java
│   │   │               │
│   │   │               ├── application/         # Capa de Aplicación
│   │   │               │   ├── service/         # Servicios/Casos de uso
│   │   │               │   │   ├── ClienteService.java
│   │   │               │   │   └── PedidoService.java
│   │   │               │   └── dto/             # Data Transfer Objects
│   │   │               │       ├── ClienteDTO.java
│   │   │               │       └── PedidoDTO.java
│   │   │               │
│   │   │               ├── infrastructure/      # Capa de Infraestructura
│   │   │               │   ├── database/        # Gestión de BD
│   │   │               │   │   ├── ConexionBD.java
│   │   │               │   │   └── DatabaseConfig.java
│   │   │               │   ├── repository/      # Implementaciones DAO
│   │   │               │   │   ├── ClienteRepositoryImpl.java
│   │   │               │   │   └── PedidoRepositoryImpl.java
│   │   │               │   └── mapper/          # Mapeo BD <-> Entidad
│   │   │               │       ├── ClienteMapper.java
│   │   │               │       └── PedidoMapper.java
│   │   │               │
│   │   │               └── presentation/        # Capa de Presentación
│   │   │                   ├── MainFrame.java   # Ventana principal
│   │   │                   ├── panel/           # Paneles de UI
│   │   │                   │   ├── ClientePanel.java
│   │   │                   │   └── PedidoPanel.java
│   │   │                   ├── dialog/          # Diálogos
│   │   │                   │   ├── ClienteDialog.java
│   │   │                   │   └── PedidoDialog.java
│   │   │                   ├── table/           # Modelos de tabla
│   │   │                   │   ├── ClienteTableModel.java
│   │   │                   │   └── PedidoTableModel.java
│   │   │                   └── util/            # Utilidades UI
│   │   │                       ├── SwingUtils.java
│   │   │                       └── ValidationUI.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties           # Configuración
│   │       └── database/
│   │           ├── schema.sql                   # Creación de tablas
│   │           └── data.sql                     # Datos iniciales
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── kilombo/
│                   └── crm/
│                       ├── service/              # Tests de servicios
│                       └── repository/           # Tests de repositorios
│
├── pom.xml                                      # Configuración Maven
├── README.md                                    # Documentación principal
└── ARQUITECTURA.md                              # Este documento
```

## 4. Descripción de Capas

### 4.1 Capa de Dominio (domain)
**Responsabilidad**: Contiene la lógica de negocio pura y las reglas del dominio.

- **model**: Entidades del dominio ([`Cliente`](src/main/java/com/kilombo/crm/domain/model/Cliente.java), [`Pedido`](src/main/java/com/kilombo/crm/domain/model/Pedido.java))
  - Sin dependencias externas
  - Validaciones de negocio
  - Inmutabilidad cuando sea posible

- **repository**: Interfaces que definen contratos de persistencia
  - Principio de Inversión de Dependencias
  - El dominio define QUÉ necesita, no CÓMO se implementa

- **exception**: Excepciones específicas del dominio
  - Excepciones checked para errores recuperables
  - Mensajes descriptivos

### 4.2 Capa de Aplicación (application)
**Responsabilidad**: Orquesta el flujo de datos entre capas.

- **service**: Casos de uso de la aplicación
  - [`ClienteService`](src/main/java/com/kilombo/crm/application/service/ClienteService.java): CRUD de clientes
  - [`PedidoService`](src/main/java/com/kilombo/crm/application/service/PedidoService.java): CRUD de pedidos + consulta por cliente
  - Transacciones
  - Validaciones de aplicación

- **dto**: Objetos de transferencia de datos
  - Desacopla la UI del dominio
  - Facilita la serialización

### 4.3 Capa de Infraestructura (infrastructure)
**Responsabilidad**: Implementa detalles técnicos y frameworks.

- **database**: Gestión de conexiones
  - [`ConexionBD`](src/main/java/com/kilombo/crm/infrastructure/database/ConexionBD.java): Singleton para conexión MySQL
  - Pool de conexiones (opcional con HikariCP)
  - Configuración externalizada

- **repository**: Implementaciones DAO
  - Patrón DAO para acceso a datos
  - Uso de PreparedStatement (seguridad SQL injection)
  - Manejo de transacciones

- **mapper**: Conversión entre capas
  - ResultSet → Entidad
  - Entidad → PreparedStatement

### 4.4 Capa de Presentación (presentation)
**Responsabilidad**: Interfaz gráfica Swing.

- **MainFrame**: Ventana principal con menú y navegación
- **panel**: Paneles reutilizables para cada entidad
  - Tablas con datos
  - Botones de acción (CRUD)
  - Filtros y búsquedas

- **dialog**: Diálogos modales para formularios
  - Validación en tiempo real
  - Mensajes de error claros

- **table**: Modelos personalizados de JTable
  - AbstractTableModel
  - Actualización dinámica

## 5. Patrones de Diseño Aplicados

### 5.1 Singleton
- [`ConexionBD`](src/main/java/com/kilombo/crm/infrastructure/database/ConexionBD.java): Una única instancia de conexión

### 5.2 DAO (Data Access Object)
- Abstracción del acceso a datos
- Interfaces en dominio, implementaciones en infraestructura

### 5.3 Service Layer
- Encapsula lógica de negocio
- Coordina múltiples repositorios

### 5.4 DTO (Data Transfer Object)
- Transferencia de datos entre capas
- Evita exponer entidades de dominio

### 5.5 Factory (opcional)
- Creación de objetos complejos
- Centraliza la lógica de construcción

### 5.6 Observer (Swing)
- Listeners para eventos de UI
- Actualización reactiva de componentes

## 6. Flujo de Datos

### Ejemplo: Crear un Cliente

```
[UI] ClienteDialog
    ↓ (usuario completa formulario)
[UI] Validación básica
    ↓ (datos válidos)
[Service] ClienteService.crearCliente(ClienteDTO)
    ↓ (convierte DTO → Entidad)
[Domain] Cliente (validaciones de negocio)
    ↓ (entidad válida)
[Repository] ClienteRepository.save(Cliente)
    ↓ (implementación)
[Infrastructure] ClienteRepositoryImpl
    ↓ (SQL INSERT)
[Database] MySQL
    ↓ (retorna ID generado)
[Infrastructure] Retorna Cliente con ID
    ↓
[Service] Retorna ClienteDTO
    ↓
[UI] Actualiza tabla y muestra mensaje
```

## 7. Gestión de Excepciones

### Jerarquía de Excepciones

```
Exception
└── RuntimeException
    └── CRMException (base)
        ├── ValidationException
        ├── ClienteNotFoundException
        ├── PedidoNotFoundException
        └── DatabaseException
```

### Estrategia de Manejo

1. **Capa de Dominio**: Lanza excepciones de negocio
2. **Capa de Aplicación**: Captura y transforma excepciones
3. **Capa de Infraestructura**: Captura SQLException y lanza DatabaseException
4. **Capa de Presentación**: Muestra mensajes amigables al usuario

## 8. Validaciones

### Validaciones de Dominio (Cliente)
- Nombre: no vacío, máx 100 caracteres
- Apellido: no vacío, máx 100 caracteres
- Email: formato válido, único, máx 150 caracteres
- Teléfono: formato válido, máx 20 caracteres

### Validaciones de Dominio (Pedido)
- Cliente: debe existir
- Fecha: no nula, no futura
- Total: mayor que 0

### Validaciones de UI
- Campos obligatorios marcados con *
- Validación en tiempo real
- Mensajes de error específicos

## 9. Configuración de Base de Datos H2

### application.properties
```properties
# Configuración de Base de Datos H2 Embebida
db.url=jdbc:h2:~/kilombocrm;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
db.username=sa
db.password=
db.driver=org.h2.Driver

# Configuración de Conexiones Robusta
db.pool.size=10
db.pool.timeout=30000
```

### Características de H2 Implementadas
- **Base de datos embebida**: No requiere instalación separada
- **Auto-creación**: Las tablas se crean automáticamente al iniciar
- **Persistencia**: Los datos se guardan en `~/kilombocrm.mv.db`
- **Modo servidor**: Permite conexiones remotas si es necesario
- **Compatibilidad**: Soporta sintaxis SQL estándar

## 10. Dependencias Maven

### Principales
- **H2 Database**: 2.2.224 (base de datos embebida)
- **JUnit**: 5.9.3 (testing)
- **Mockito**: 5.3.1 (testing)
- **Java Logging**: java.util.logging (logging integrado, sin dependencias externas)

### Ventajas de H2 para Aprendizaje
- **Cero configuración**: No requiere instalación de servidor de BD
- **Auto-inicialización**: Crea BD y tablas automáticamente
- **Consola web**: Acceso vía navegador en http://localhost:8082
- **Modo embebido**: Perfecto para aplicaciones de escritorio
- **SQL estándar**: Compatible con la mayoría de sintaxis SQL

## 11. Principios SOLID Aplicados

### Single Responsibility Principle (SRP)
- Cada clase tiene una única razón para cambiar
- Servicios separados por entidad
- DAOs específicos por tabla

### Open/Closed Principle (OCP)
- Interfaces para repositorios
- Extensible sin modificar código existente

### Liskov Substitution Principle (LSP)
- Implementaciones intercambiables de repositorios
- Contratos bien definidos

### Interface Segregation Principle (ISP)
- Interfaces específicas y cohesivas
- No forzar implementaciones innecesarias

### Dependency Inversion Principle (DIP)
- Dependencias hacia abstracciones
- Inyección de dependencias manual

## 12. Mejoras Implementadas (Robustez)

### ✅ Mejoras de Conexión y BD
- [x] **Reintentos automáticos**: Hasta 3 intentos con backoff exponencial
- [x] **Timeouts configurables**: 5 segundos para conexión y validación
- [x] **Validación de esquema**: Verificación automática de tablas y estructura
- [x] **Detección de datos corruptos**: Validación de integridad al iniciar
- [x] **Logging integrado**: Sistema completo sin dependencias externas

### ✅ Mejoras de Validación y Error Handling
- [x] **Validaciones multinivel**: UI, aplicación, dominio y BD
- [x] **Manejo de excepciones robusto**: Tipos específicos y recuperación automática
- [x] **Mensajes de error informativos**: Feedback claro al usuario
- [x] **Recuperación automática**: Reintentos en operaciones fallidas

### ✅ Mejoras de UI/UX
- [x] **Estados de carga**: Indicadores visuales durante operaciones
- [x] **Mensajes contextuales**: Información específica según el error
- [x] **Recuperación en UI**: Opción de reintentar operaciones fallidas
- [x] **Validación en tiempo real**: Feedback inmediato en formularios

## 13. Mejoras Futuras (Opcional)

### Fase 2 (Funcionalidades)
- [ ] Implementar paginación en listados grandes
- [ ] Añadir búsqueda y filtros avanzados
- [ ] Exportar datos a PDF/Excel/CSV
- [ ] Implementar caché de datos (para mejorar rendimiento)
- [ ] Añadir auditoría (quién/cuándo modificó registros)

### Fase 3 (Avanzado - Nuevo Proyecto)
- [ ] Migrar a Spring Boot (proyecto separado)
- [ ] Añadir API REST con Spring Web
- [ ] Implementar autenticación y autorización
- [ ] Añadir tests unitarios completos con cobertura
- [ ] CI/CD con GitHub Actions
- [ ] Dockerizar la aplicación
- [ ] Añadir monitoring con Spring Actuator

### Fase 4 (Distribuido)
- [ ] Microservicios con Spring Cloud
- [ ] API Gateway
- [ ] Service Discovery
- [ ] Configuración centralizada
- [ ] Logs centralizados

## 13. Convenciones de Código

### Nomenclatura
- **Clases**: PascalCase (ej: [`ClienteService`](src/main/java/com/kilombo/crm/application/service/ClienteService.java))
- **Métodos**: camelCase (ej: `crearCliente()`)
- **Constantes**: UPPER_SNAKE_CASE (ej: `MAX_NOMBRE_LENGTH`)
- **Paquetes**: lowercase (ej: `com.kilombo.crm.domain`)

### Formato
- Indentación: 4 espacios
- Llaves: estilo Java (misma línea)
- Líneas: máximo 120 caracteres
- Imports: organizados y sin wildcards

### Comentarios
- JavaDoc para clases y métodos públicos
- Comentarios inline solo cuando sea necesario
- TODO/FIXME para tareas pendientes

## 14. Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  MainFrame   │  │ClientePanel  │  │ PedidoPanel  │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                  │               │
└─────────┼─────────────────┼──────────────────┼───────────────┘
          │                 │                  │
          ▼                 ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                          │
│         ┌──────────────────┐  ┌──────────────────┐          │
│         │ ClienteService   │  │  PedidoService   │          │
│         └────────┬─────────┘  └────────┬─────────┘          │
│                  │                     │                     │
└──────────────────┼─────────────────────┼─────────────────────┘
                   │                     │
                   ▼                     ▼
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                             │
│  ┌──────────┐  ┌──────────┐  ┌────────────────────────┐    │
│  │ Cliente  │  │  Pedido  │  │ Repository Interfaces  │    │
│  └──────────┘  └──────────┘  └────────────────────────┘    │
│                                         ▲                    │
└─────────────────────────────────────────┼────────────────────┘
                                          │
                                          │ implements
┌─────────────────────────────────────────┼────────────────────┐
│                 INFRASTRUCTURE LAYER    │                    │
│  ┌──────────────┐  ┌──────────────────────────────────┐     │
│  │  ConexionBD  │  │  Repository Implementations      │     │
│  └──────┬───────┘  │  (ClienteDAO, PedidoDAO)         │     │
│         │          └──────────────┬───────────────────┘     │
│         │                         │                          │
│         ▼                         ▼                          │
│    ┌─────────────────────────────────────┐                  │
│    │         MySQL Database              │                  │
│    │  ┌──────────┐    ┌──────────┐      │                  │
│    │  │ cliente  │    │  pedido  │      │                  │
│    │  └──────────┘    └──────────┘      │                  │
│    └─────────────────────────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

## 15. Conclusión - Arquitectura Robusta Implementada

Esta arquitectura proporciona:

### ✅ Calidades Arquitectónicas
- **Separación de responsabilidades**: Cada capa tiene un propósito claro y definido
- **Testabilidad**: Fácil de probar cada componente independientemente
- **Mantenibilidad**: Código organizado, documentado y fácil de entender
- **Escalabilidad**: Preparado para crecer con nuevas funcionalidades
- **Flexibilidad**: Fácil cambiar implementaciones sin afectar otras capas

### ✅ Robustez Implementada
- **Manejo de errores completo**: Excepciones específicas y recuperación automática
- **Validaciones multinivel**: UI, aplicación, dominio y base de datos
- **Gestión de conexiones robusta**: Reintentos, timeouts y validación de esquema
- **Logging integrado**: Seguimiento completo de operaciones críticas
- **Recuperación automática**: La aplicación continúa funcionando tras errores temporales

### ✅ Enfoque Educativo
- **Proyecto completo**: Desde la arquitectura hasta la implementación
- **Documentación extensa**: Múltiples documentos para diferentes niveles de aprendizaje
- **Ejemplos reales**: Aplicación de patrones y principios en código funcional
- **Mejores prácticas**: Código limpio, profesional y mantenible
- **Progresión de aprendizaje**: Desde conceptos básicos hasta avanzados

### 🎯 Resultado Final
El proyecto combina lo mejor de ambos mundos:
- **Educativo**: Perfecto para juniors que aprenden desarrollo Java
- **Profesional**: Código de calidad production-ready
- **Completo**: Arquitectura, implementación, testing y documentación
- **Robusto**: Manejo de errores y validaciones profesionales
- **Escalable**: Preparado para futuras ampliaciones

Este proyecto es un **ejemplo completo y real** de cómo desarrollar aplicaciones Java empresariales siguiendo las mejores prácticas de la industria, mientras sirve como material de aprendizaje excepcional para desarrolladores juniors.