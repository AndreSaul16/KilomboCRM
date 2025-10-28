# KilomboCRM - Sistema de Gestión de Clientes y Pedidos

KilomboCRM es un sistema de gestión de clientes y pedidos desarrollado en Java con arquitectura limpia (Clean Architecture) que permite:

- Gestionar clientes (CRUD completo)
- Gestionar pedidos asociados a clientes
- Realizar búsquedas y filtrados
- Generar informes básicos

## 🚀 Instalación y Ejecución Paso a Paso

### 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java 8 o superior** (JDK 8+)
  - Descargar desde: https://adoptium.net/
  - Verificar: `java -version`

- **MySQL 8.0 o superior**
  - Descargar desde: https://dev.mysql.com/downloads/mysql/
  - Instalar y configurar usuario/password
  - MySQL Workbench (opcional pero recomendado)

- **Maven 3.6+**
  - Descargar desde: https://maven.apache.org/download.cgi
  - Verificar: `mvn -version`

### 📦 Paso 1: Preparar el Proyecto

1. **Descargar el proyecto**
   - El proyecto viene en un archivo `.zip`
   - Extraer el contenido en una carpeta (ej: `C:\Proyectos\KilomboCRM`)

2. **Verificar estructura**
   ```
   KilomboCRM/
   ├── src/
   ├── pom.xml
   ├── README.md
   ├── ARQUITECTURA.md
   └── .gitignore
   ```

### 🗄️ Paso 2: Configurar Base de Datos MySQL

1. **Abrir MySQL Workbench** o **MySQL Command Line Client**

2. **Crear la base de datos**
   ```sql
   -- Ejecutar este comando en MySQL:
   CREATE DATABASE IF NOT EXISTS kilombo
   CHARACTER SET utf8mb4
   COLLATE utf8mb4_unicode_ci;
   ```

3. **Crear las tablas**
   - Abrir el archivo `src/main/resources/database/schema.sql`
   - Copiar todo el contenido
   - Pegar y ejecutar en MySQL Workbench

4. **Insertar datos de prueba** (opcional)
   - Abrir el archivo `src/main/resources/database/data.sql`
   - Copiar todo el contenido
   - Pegar y ejecutar en MySQL Workbench

5. **Crear usuario de aplicación** (opcional, si no usas root)
   ```sql
   CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
   GRANT ALL PRIVILEGES ON kilombo.* TO 'admin'@'localhost';
   FLUSH PRIVILEGES;
   ```

### ⚙️ Paso 3: Configurar la Aplicación

1. **Editar archivo de configuración**
   - Abrir `src/main/resources/application.properties`
   - Modificar las credenciales de BD si es necesario:
   ```properties
   # Cambiar si usas usuario/password diferentes
   db.username=admin
   db.password=admin
   ```

2. **Verificar configuración**
   - Asegurarse que la URL apunta a tu instalación local:
   ```properties
   db.url=jdbc:mysql://localhost:3306/kilombo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8
   ```

### ▶️ Paso 4: Ejecutar la Aplicación

1. **Abrir terminal/command prompt**
   - Windows: `cmd` o PowerShell
   - Navegar a la carpeta del proyecto: `cd C:\Proyectos\KilomboCRM`

2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn exec:java -Dexec.mainClass="com.kilombo.crm.presentation.MainFrame"
   ```

4. **Verificar funcionamiento**
   - Debería aparecer la ventana principal de KilomboCRM
   - Si hay datos de prueba, deberían mostrarse en las tablas

### 🔧 Solución de Problemas

#### Error: "Table 'kilombo.cliente' doesn't exist"
- Verificar que ejecutaste `schema.sql` correctamente
- Asegurarse que la BD se llama exactamente `kilombo`

#### Error: "Access denied for user 'admin'@'localhost'"
- Verificar credenciales en `application.properties`
- Crear el usuario en MySQL si no existe

#### Error: "Java not found"
- Verificar instalación de Java: `java -version`
- Agregar Java al PATH del sistema

#### Error: "Maven not found"
- Verificar instalación de Maven: `mvn -version`
- Agregar Maven al PATH del sistema

#### Caracteres extraños en nombres (acentos)
- Verificar que la BD se creó con `CHARACTER SET utf8mb4`
- Asegurarse que ejecutaste los scripts SQL con codificación UTF-8

### 📞 Soporte

Si encuentras problemas:
1. Revisar los logs en la consola donde ejecutas el programa
2. Verificar que todos los pasos de instalación se completaron
3. Comprobar que MySQL está ejecutándose
4. Revisar la documentación en [ARQUITECTURA.md](ARQUITECTURA.md) para detalles técnicos

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

---

**Desarrollado con ❤️ usando Clean Architecture**