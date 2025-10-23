# KilomboCRM - Sistema de Gestión de Clientes y Pedidos

![Java](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue)
![H2](https://img.shields.io/badge/H2-2.2+-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-green)

## 📋 Descripción

KilomboCRM es una aplicación de escritorio desarrollada en Java con interfaz gráfica Swing que permite gestionar clientes y pedidos de la empresa Kilombo. El proyecto implementa operaciones CRUD completas sobre una base de datos H2 embebida, siguiendo los principios de **Clean Architecture** y **Clean Code**.

**🚨 IMPORTANTE**: Esta aplicación ahora usa **H2 Database** en lugar de MySQL para simplificar la instalación y ejecución. H2 es una base de datos embebida que no requiere instalación separada.

## 🎯 Características

- ✅ Gestión completa de clientes (CRUD)
- ✅ Gestión completa de pedidos (CRUD)
- ✅ Visualización de pedidos por cliente
- ✅ Validación de datos en tiempo real
- ✅ Manejo robusto de excepciones
- ✅ Interfaz gráfica intuitiva con Swing
- ✅ Arquitectura limpia y escalable
- ✅ Conexión segura a MySQL mediante JDBC

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en capas basada en Clean Architecture:

```
┌─────────────────────────────────────┐
│   Presentación (Swing UI)           │
├─────────────────────────────────────┤
│   Aplicación (Servicios)            │
├─────────────────────────────────────┤
│   Dominio (Entidades + Interfaces)  │
├─────────────────────────────────────┤
│   Infraestructura (BD + DAOs)       │
└─────────────────────────────────────┘
```

Para más detalles, consulta [`ARQUITECTURA.md`](ARQUITECTURA.md)

## 🛠️ Tecnologías

- **Java 17** (LTS)
- **Maven 3.8+** (gestión de dependencias)
- **H2 Database 2.2+** (base de datos embebida)
- **JDBC** (conectividad)
- **Swing** (interfaz gráfica)
- **JUnit 5** (testing)

## 📦 Requisitos Previos

1. **JDK 17** instalado
    ```bash
    java -version
    ```

2. **Maven** instalado
    ```bash
    mvn -version
    ```

3. **H2 Database** (viene incluido en el proyecto - no requiere instalación separada)

4. **IDE** recomendado: IntelliJ IDEA, Eclipse o VS Code con extensiones Java

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone https://github.com/tuusuario/KilomboCRM.git
cd KilomboCRM
```

### 2. Compilar el proyecto
```bash
mvn clean compile
```

### 3. Ejecutar la aplicación
```bash
mvn exec:java -Dexec.mainClass="com.kilombo.crm.presentation.MainFrame"
```

O desde tu IDE, ejecuta la clase [`MainFrame`](src/main/java/com/kilombo/crm/presentation/MainFrame.java)

### 🎉 ¡Eso es todo!

**La aplicación se ejecutará automáticamente con:**
- ✅ Base de datos H2 creada automáticamente
- ✅ Tablas creadas automáticamente
- ✅ Datos de prueba insertados automáticamente
- ✅ Conexión configurada automáticamente

**No necesitas configurar MySQL ni ejecutar scripts manualmente.**

## 📁 Estructura del Proyecto

```
KilomboCRM/
├── src/
│   ├── main/
│   │   ├── java/com/kilombo/crm/
│   │   │   ├── domain/              # Entidades y lógica de negocio
│   │   │   ├── application/         # Servicios y casos de uso
│   │   │   ├── infrastructure/      # Acceso a datos y BD
│   │   │   └── presentation/        # Interfaz gráfica Swing
│   │   └── resources/
│   │       ├── application.properties
│   │       └── database/
│   │           ├── schema.sql
│   │           └── data.sql
│   └── test/                        # Tests unitarios
├── pom.xml                          # Configuración Maven
├── README.md                        # Este archivo
└── ARQUITECTURA.md                  # Documentación arquitectónica
```

## 💾 Modelo de Datos

### Tabla: cliente
| Campo    | Tipo          | Descripción              |
|----------|---------------|--------------------------|
| id       | INT (PK)      | Identificador único      |
| nombre   | VARCHAR(100)  | Nombre del cliente       |
| apellido | VARCHAR(100)  | Apellido del cliente     |
| email    | VARCHAR(150)  | Email (único)            |
| telefono | VARCHAR(20)   | Teléfono de contacto     |

### Tabla: pedido
| Campo      | Tipo          | Descripción              |
|------------|---------------|--------------------------|
| id         | INT (PK)      | Identificador único      |
| id_cliente | INT (FK)      | Referencia a cliente     |
| fecha      | DATE          | Fecha del pedido         |
| total      | DOUBLE        | Importe total            |

**Relación**: Un cliente puede tener múltiples pedidos (1:N)

## 🎨 Interfaz de Usuario

### Ventana Principal
- Menú de navegación
- Panel de clientes con tabla y botones CRUD
- Panel de pedidos con tabla y botones CRUD
- Filtro para ver pedidos por cliente

### Funcionalidades
- **Añadir**: Abre diálogo para crear nuevo registro
- **Modificar**: Edita el registro seleccionado
- **Eliminar**: Elimina el registro (con confirmación)
- **Listar**: Actualiza la tabla con todos los registros
- **Ver Pedidos**: Muestra pedidos del cliente seleccionado

## 🧪 Testing

### Ejecutar todos los tests
```bash
mvn test
```

### Ejecutar tests específicos
```bash
mvn test -Dtest=ClienteServiceTest
```

## 📝 Uso de la Aplicación

### Gestionar Clientes

1. **Añadir Cliente**
   - Click en "Añadir Cliente"
   - Completar formulario
   - Validación automática
   - Click en "Guardar"

2. **Modificar Cliente**
   - Seleccionar cliente en la tabla
   - Click en "Modificar"
   - Editar campos
   - Click en "Guardar"

3. **Eliminar Cliente**
   - Seleccionar cliente
   - Click en "Eliminar"
   - Confirmar acción

4. **Ver Pedidos de Cliente**
   - Seleccionar cliente
   - Click en "Ver Pedidos"
   - Se muestra lista de pedidos

### Gestionar Pedidos

Similar a clientes, con validación adicional de:
- Cliente debe existir
- Fecha no puede ser futura
- Total debe ser mayor que 0

## 🔒 Validaciones Robusta

### Cliente
- ✅ **Nombre**: obligatorio, máx 100 caracteres, no vacío
- ✅ **Apellido**: obligatorio, máx 100 caracteres, no vacío
- ✅ **Email**: formato válido, único, máx 150 caracteres, normalizado a minúsculas
- ✅ **Teléfono**: formato válido, máx 20 caracteres (opcional)

### Pedido
- ✅ **Cliente**: debe existir en la BD (validación de clave foránea)
- ✅ **Fecha**: obligatoria, no puede ser futura, formato válido
- ✅ **Total**: mayor que 0, valor numérico válido

### Validaciones de Integridad
- ✅ **Duplicados**: Email único en clientes
- ✅ **Referencias**: Pedidos solo pueden referenciar clientes existentes
- ✅ **Datos corruptos**: Validación de estructura de BD al iniciar
- ✅ **Conexiones**: Reintentos automáticos y timeouts configurables

## 🐛 Manejo Robusto de Errores

La aplicación implementa un sistema completo de manejo de errores:

### Tipos de Excepciones
- **ValidationException**: Datos inválidos o reglas de negocio violadas
- **ClienteNotFoundException**: Cliente no encontrado en operaciones de búsqueda/actualización
- **PedidoNotFoundException**: Pedido no encontrado en operaciones de búsqueda/actualización
- **DatabaseException**: Errores de conexión, SQL, o integridad de BD

### Estrategias de Recuperación
- **Reintentos automáticos**: Hasta 3 intentos con backoff exponencial para conexiones fallidas
- **Timeouts configurables**: 5 segundos para conexión, 5 segundos para validación
- **Mensajes específicos**: Cada error muestra información detallada al usuario
- **Logging completo**: Todos los errores se registran con contexto para debugging
- **Recuperación automática**: La UI puede reintentar operaciones fallidas automáticamente

### Validación en Múltiples Niveles
1. **UI**: Validación inmediata en formularios
2. **Aplicación**: Validación de reglas de negocio
3. **Dominio**: Validación de integridad de entidades
4. **BD**: Constraints y validaciones automáticas

## 🔧 Características Avanzadas Implementadas

### Gestión Robusta de Conexiones
- **Reintentos automáticos**: Hasta 3 intentos con backoff exponencial
- **Timeouts configurables**: 5 segundos para conexión, 5 segundos para validación
- **Validación de esquema**: Verifica automáticamente que las tablas existan
- **Detección de datos corruptos**: Identifica problemas de integridad al iniciar

### Sistema de Logging Completo
- **Logging integrado**: Todas las operaciones críticas se registran
- **Niveles apropiados**: INFO, WARNING, SEVERE según la gravedad
- **Contexto detallado**: Información específica para debugging
- **Sin dependencias externas**: Usa java.util.logging nativo

### Validaciones de Integridad
- **Estructura de BD**: Verificación automática de tablas y columnas
- **Datos huérfanos**: Detección de pedidos sin cliente asociado
- **Consistencia**: Validación de datos básicos (nombres no vacíos, etc.)
- **Referencias**: Verificación de claves foráneas

### Recuperación Automática
- **Operaciones fallidas**: Reintento automático en la UI
- **Estados visuales**: Indicadores de carga y progreso
- **Mensajes informativos**: Feedback claro al usuario
- **Continuidad**: La aplicación sigue funcionando tras errores temporales

## 📚 Documentación Completa para Aprendizaje

### 📖 Guías de Aprendizaje
- [`ARQUITECTURA.md`](ARQUITECTURA.md) - Arquitectura detallada con explicaciones paso a paso
- [`PLAN_IMPLEMENTACION.md`](PLAN_IMPLEMENTACION.md) - Plan completo de desarrollo
- [`DIAGRAMAS.md`](DIAGRAMAS.md) - Diagramas visuales de la arquitectura
- [`INSTRUCCIONES_EJECUCION.md`](INSTRUCCIONES_EJECUCION.md) - Guía detallada de instalación y uso
- [`RESUMEN_ARQUITECTURA.md`](RESUMEN_ARQUITECTURA.md) - Resumen ejecutivo de la arquitectura

### 📚 Recursos de Aprendizaje Incluidos
- **Clean Architecture**: Explicación completa con ejemplos reales
- **Clean Code**: Principios aplicados en todo el código
- **Patrones de Diseño**: Singleton, DAO, Service Layer, DTO, Repository
- **Principios SOLID**: Explicación práctica de cada principio
- **Manejo de Errores**: Estrategias robustas y recuperación automática
- **Validaciones**: Múltiples niveles de validación de datos
- **Logging**: Sistema completo de registro de eventos
- **JDBC**: Acceso a base de datos profesional
- **Swing**: Desarrollo de interfaces gráficas
- **Maven**: Gestión de dependencias y construcción

### 🎯 Para Juniors que Aprenden
Este proyecto está diseñado específicamente para que los desarrolladores juniors aprendan:

1. **Cómo estructurar proyectos Java profesionales**
2. **Principios de arquitectura limpia**
3. **Manejo robusto de errores y excepciones**
4. **Validación de datos en múltiples niveles**
5. **Acceso a base de datos con JDBC**
6. **Desarrollo de interfaces gráficas con Swing**
7. **Gestión de dependencias con Maven**
8. **Logging y debugging profesional**
9. **Patrones de diseño aplicados**
10. **Buenas prácticas de código**

### 📋 Checklist de Aprendizaje
- [ ] Entender Clean Architecture
- [ ] Aprender principios SOLID
- [ ] Dominar manejo de excepciones
- [ ] Practicar validaciones de datos
- [ ] Aprender JDBC y SQL
- [ ] Desarrollar interfaces Swing
- [ ] Usar Maven correctamente
- [ ] Implementar logging
- [ ] Aplicar patrones de diseño
- [ ] Escribir código limpio y mantenible

### 🚀 Próximos Pasos de Aprendizaje
Después de este proyecto, puedes aprender:
- **Spring Boot** para desarrollo web
- **JPA/Hibernate** para ORM
- **REST APIs** con Spring Web
- **Testing** avanzado con JUnit y Mockito
- **Docker** para contenerización
- **CI/CD** con GitHub Actions

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la Licencia MIT.

## 👥 Autores

- **Equipo Kilombo** - Desarrollo inicial y mejoras de robustez

## 🙏 Agradecimientos

- **Clean Architecture** por Robert C. Martin
- **Patrones de Diseño** Gang of Four
- **Comunidad Java** por las mejores prácticas
- **H2 Database** por la base de datos embebida
- **Maven** por la gestión de dependencias
- **Swing** por la interfaz gráfica

## 📞 Soporte y Aprendizaje

### Para Usuarios
Si tienes problemas con la aplicación:
1. Revisa [`INSTRUCCIONES_EJECUCION.md`](INSTRUCCIONES_EJECUCION.md)
2. Verifica la documentación en [`ARQUITECTURA.md`](ARQUITECTURA.md)
3. Busca en los logs de la aplicación (se muestran automáticamente)

### Para Juniors Aprendiendo
Si eres nuevo en desarrollo Java:
1. **Lee primero** [`RESUMEN_ARQUITECTURA.md`](RESUMEN_ARQUITECTURA.md) - visión general
2. **Estudia** [`ARQUITECTURA.md`](ARQUITECTURA.md) - detalles técnicos
3. **Sigue** [`PLAN_IMPLEMENTACION.md`](PLAN_IMPLEMENTACION.md) - cómo se construyó
4. **Ejecuta** siguiendo [`INSTRUCCIONES_EJECUCION.md`](INSTRUCCIONES_EJECUCION.md)
5. **Analiza** el código fuente con los comentarios incluidos
6. **Experimenta** modificando y extendiendo funcionalidades

### Comunidad
- 📧 **Email**: Para preguntas específicas sobre el código
- 💬 **Issues**: Para reportar bugs o sugerir mejoras
- 📚 **Wiki**: Documentación adicional y tutoriales

---

## 🎓 ¿Qué Aprenderás con Este Proyecto?

Este proyecto es un **ejemplo completo y profesional** de desarrollo Java que cubre:

### 🏗️ Arquitectura y Diseño
- **Clean Architecture** aplicada en un proyecto real
- **Principios SOLID** con ejemplos concretos
- **Patrones de Diseño** implementados correctamente
- **Separación de responsabilidades** clara

### 💻 Tecnologías y Herramientas
- **Java 17** con características modernas
- **Maven** para gestión de proyectos
- **H2 Database** para persistencia
- **Swing** para interfaces gráficas
- **JDBC** para acceso a datos
- **JUnit** para testing

### 🛡️ Calidad y Robustez
- **Manejo de errores** completo y profesional
- **Validaciones** en múltiples niveles
- **Logging** integrado
- **Recuperación automática** de errores
- **Gestión de conexiones** robusta

### 📚 Mejores Prácticas
- **Código limpio** y legible
- **Documentación** completa
- **Testing** básico incluido
- **Configuración** externalizada
- **Mantenibilidad** y escalabilidad

---

**¡Este proyecto es tu puerta de entrada al desarrollo Java profesional!** 🚀

**¡Gracias por usar KilomboCRM y aprender con nosotros!** 🎓