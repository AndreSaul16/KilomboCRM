# KilomboCRM - Sistema de Gestión de Clientes y Pedidos

KilomboCRM es un sistema de gestión de clientes y pedidos desarrollado en Java con arquitectura limpia (Clean Architecture) que permite:

- Gestionar clientes (CRUD completo)
- Gestionar pedidos asociados a clientes
- Realizar búsquedas y filtrados
- Generar informes básicos

## 📖 Documentación

- **[Instrucciones de Instalación](instrucciones_de_instalacion.md)** - Guía completa paso a paso
- **[Arquitectura del Sistema](ARQUITECTURA.md)** - Detalles técnicos y diseño
- **[Próxima Actualización](Próxima actualización.md)** - Plan de mejoras futuras

## 📋 Características Principales

### Gestión de Clientes
- ✅ Crear, leer, actualizar y eliminar clientes
- ✅ Validación de datos (email único, teléfono)
- ✅ Búsqueda y filtrado por nombre/apellido/email

### Gestión de Pedidos
- ✅ Crear pedidos asociados a clientes
- ✅ Estados: PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO
- ✅ Cálculo automático de totales
- ✅ Listado de pedidos por cliente

### Interfaz de Usuario
- ✅ Interfaz gráfica con Java Swing
- ✅ Tablas interactivas con ordenamiento
- ✅ Formularios de creación/edición
- ✅ Filtros y búsquedas en tiempo real

### Base de Datos
- ✅ MySQL con codificación UTF-8
- ✅ Pool de conexiones configurado
- ✅ Validación automática de esquema
- ✅ Transacciones y manejo de errores

## 🔍 Análisis de Calidad del Código

### Arquitectura y Principios SOLID
- ✅ **Clean Architecture**: Capas bien separadas (Domain, Application, Infrastructure, Presentation)
- ✅ **SRP (Single Responsibility)**: Cada clase tiene una responsabilidad única
- ✅ **DIP (Dependency Inversion)**: Dependencias apuntan a abstracciones
- ✅ **ISP (Interface Segregation)**: Interfaces específicas por funcionalidad
- ✅ **OCP (Open/Closed)**: Código extensible sin modificar existente

### Áreas de Mejora Identificadas

#### 🚨 Críticas (Prioridad Alta)
- **Duplicación masiva de código**: Patrón try-catch repetido 49+ veces en repositorios
- **MainFrame sobrecargado**: 509 líneas, viola SRP (maneja navegación, acciones, coordinación)
- **Métodos largos**: Varios métodos superan las 50 líneas recomendadas

#### ⚠️ Mejoras (Prioridad Media)
- **Validaciones faltantes**: `DetallePedido` carece de reglas de negocio
- **Inyección de dependencias**: Instanciación manual en lugar de contenedor DI
- **ConexionBD grande**: 496 líneas, múltiples responsabilidades

#### 📈 Mejoras Futuras (Prioridad Baja)
- **Framework DI**: Implementar Spring o similar
- **Tests unitarios**: Cobertura actual 0%
- **Documentación API**: Falta documentación de métodos públicos

## 🏗️ Arquitectura

El proyecto sigue **Clean Architecture** con separación clara en capas:

- **Dominio**: Entidades y reglas de negocio puras
- **Aplicación**: Servicios y casos de uso
- **Infraestructura**: Persistencia y UI
- **Presentación**: Interfaz gráfica

Para detalles técnicos profundos, ver [ARQUITECTURA.md](ARQUITECTURA.md).

## 🛠️ Tecnologías

- **Java 8+**: Lenguaje principal
- **MySQL**: Base de datos relacional
- **JDBC**: Conectividad a BD
- **Maven**: Gestión de dependencias
- **Swing**: Interfaz gráfica

## 📊 Business Intelligence

- Cálculo automático de ganancias por pedido
- Estadísticas de clientes más rentables
- Totales de ventas por período
- Dashboard preparado para gráficos avanzados

## 🔧 Configuración

### application.properties
```properties
# Base de datos
db.url=jdbc:mysql://localhost:3306/kilombo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
db.username=tu_usuario
db.password=tu_password
db.driver=com.mysql.cj.jdbc.Driver

# Pool de conexiones
db.initialSize=5
db.maxActive=20
db.maxIdle=10
db.minIdle=5
db.maxWait=10000
```

## 📁 Estructura del Proyecto

```
KilomboCRM/
├── src/main/java/com/kilombo/crm/
│   ├── domain/                 # Capa de dominio
│   │   ├── model/             # Entidades (Cliente, Pedido)
│   │   ├── repository/        # Interfaces de repositorio
│   │   └── exception/         # Excepciones de dominio
│   ├── application/           # Capa de aplicación
│   │   ├── service/           # Servicios de negocio
│   │   └── dto/               # Objetos de transferencia
│   ├── infrastructure/        # Capa de infraestructura
│   │   ├── database/          # Conexión a BD
│   │   ├── repository/        # Implementaciones de repositorio
│   │   └── mapper/            # Mapeadores DTO/Entity
│   └── presentation/          # Capa de presentación
│       ├── MainFrame.java     # Ventana principal
│       ├── panel/             # Paneles de la UI
│       ├── dialog/            # Diálogos modales
│       └── table/             # Modelos de tabla
├── src/main/resources/
│   ├── application.properties # Configuración
│   └── database/              # Scripts SQL
├── ARQUITECTURA.md            # Documentación técnica detallada
└── README.md                  # Este archivo
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

### Guías de Desarrollo
- Seguir Clean Architecture
- Mantener separación de responsabilidades
- Agregar tests para nuevas funcionalidades
- Documentar cambios significativos

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 🆘 Soporte

Para soporte técnico o preguntas:
- Abrir un issue en GitHub
- Revisar la documentación en [ARQUITECTURA.md](ARQUITECTURA.md)
- Verificar logs de aplicación para debugging

## 🗺️ Roadmap de Mejoras

### ✅ Completado
- Análisis completo de calidad del código siguiendo SOLID y Clean Code
- Identificación de áreas críticas de mejora
- Documentación actualizada con hallazgos

### 🚧 En Progreso
- [ ] Eliminación de duplicación de manejo de errores en repositorios
- [ ] Refactorización de MainFrame (dividir responsabilidades)
- [ ] Mejora de validaciones en DetallePedido
- [ ] Implementación de patrón Template Method para repositorios

### 📋 Próximas Mejoras (Ver [Próxima actualización.md](Próxima actualización.md))
- [ ] Inyección de dependencias automática
- [ ] División de ConexionBD en clases más pequeñas
- [ ] Tests unitarios con JUnit
- [ ] Framework de logging centralizado
- [ ] Documentación de API con JavaDoc
- [ ] Migración a Spring Boot (futuro lejano)

### 🎯 Métricas de Calidad
- **Complejidad Ciclomática**: Alta en algunos métodos
- **Duplicación de Código**: 49+ patrones try-catch repetidos
- **Cumplimiento SOLID**: 80% (bueno, con áreas de mejora)
- **Mantenibilidad**: Media (mejorable con refactorización)

---

**Desarrollado con ❤️ usando Clean Architecture**