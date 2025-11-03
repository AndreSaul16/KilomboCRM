# Instrucciones de Instalación - KilomboCRM

## 🚀 Instalación y Ejecución Paso a Paso

### 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

#### Para ejecución con JAR (recomendado para usuarios finales):
- **Java 17 o superior** (JDK 17+)
  - Descargar desde: https://adoptium.net/
  - Verificar: `java -version`

- **MySQL 8.0 o superior** (o MariaDB)
  - Descargar desde: https://dev.mysql.com/downloads/mysql/
  - Instalar y configurar usuario/password
  - MySQL Workbench (opcional pero recomendado)

#### Para desarrollo/compilación desde código fuente:
- **Java 17** (JDK 17)
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

#### Opción A: Ejecutar JAR empaquetado (Recomendado para usuarios finales)

Esta es la forma más sencilla - ¡solo ejecuta y listo!:

1. **Descargar el JAR empaquetado**
    - Archivo: `KilomboCRM-1.0.0-jar-with-dependencies.jar`

2. **Requisitos para esta opción:**
    - Java 17+ instalado
    - MySQL/MariaDB ejecutándose (se configura después)

3. **Ejecutar la aplicación (¡SOLO ESTE PASO!)**
    ```bash
    # Doble clic en el archivo JAR o ejecutar:
    java -jar KilomboCRM-1.0.0-jar-with-dependencies.jar
    ```

4. **La aplicación se abre automáticamente**
    - ✅ No requiere configuración previa
    - ✅ Funciona sin base de datos inicialmente
    - ✅ Interfaz completa disponible inmediatamente

5. **Configurar base de datos cuando lo necesites**
    - Ve al panel "Configuración" dentro de la aplicación
    - Ingresa datos de tu MySQL/MariaDB
    - La aplicación te guía en cada paso

6. **Ventajas de esta opción:**
    - ✅ Un solo archivo JAR
    - ✅ Sin instalación compleja
    - ✅ Funciona inmediatamente
    - ✅ Configuración opcional posterior
    - ✅ Portable a cualquier PC con Java

#### Opción B: Ejecutar desde código fuente (Para desarrolladores)

Para desarrollo o si quieres compilar desde cero:

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

4. **Crear JAR empaquetado (opcional)**
    ```bash
    mvn clean package
    # El JAR se crea en: target/KilomboCRM-1.0.0-jar-with-dependencies.jar
    ```

5. **Verificar funcionamiento**
    - Debería aparecer la ventana principal de KilomboCRM
    - Si hay datos de prueba, deberían mostrarse en las tablas

#### Opción C: Ejecutar JAR compilado desde código fuente

Después de compilar con Maven:

```bash
# Ejecutar el JAR generado:
java -jar target/KilomboCRM-1.0.0-jar-with-dependencies.jar
```

**Nota:** Esta opción combina lo mejor de ambos mundos - compilas desde fuente pero ejecutas como usuario final.

### 🔧 Solución de Problemas

#### Problemas con JAR empaquetado:

**Error: "Java not found" o "java is not recognized"**
- Verificar instalación de Java 17+: `java -version`
- Si no está instalado, descargar desde: https://adoptium.net/
- Agregar Java al PATH del sistema (variables de entorno)

**Error: "Unable to access jarfile"**
- Verificar que el archivo JAR existe en la ubicación correcta
- Asegurarse que el nombre del archivo es exacto (incluyendo versión)
- Intentar con comillas: `java -jar "KilomboCRM-1.0.0-jar-with-dependencies.jar"`

**Aplicación no inicia o cierra inmediatamente**
- Abrir terminal y ejecutar con logs: `java -jar KilomboCRM-1.0.0-jar-with-dependencies.jar > log.txt 2>&1`
- Revisar el archivo `log.txt` para errores
- Verificar que no hay procesos Java previos ejecutándose

#### Problemas de Base de Datos:

**Error: "Table 'kilombo.cliente' doesn't exist"**
- Verificar que ejecutaste `schema.sql` correctamente
- Asegurarse que la BD se llama exactamente `kilombo`
- Si usas el JAR, configurar la BD desde la interfaz de la aplicación

**Error: "Access denied for user 'admin'@'localhost'"**
- Para JAR: usar el panel de configuración de la aplicación
- Para desarrollo: verificar credenciales en `application.properties`
- Crear el usuario en MySQL si no existe

**Error: "Communications link failure"**
- Verificar que MySQL/MariaDB está ejecutándose
- Comprobar que el puerto 3306 no está bloqueado
- Si es BD remota, verificar conectividad de red

#### Problemas de Desarrollo/Compilación:

**Error: "Maven not found"**
- Verificar instalación de Maven: `mvn -version`
- Agregar Maven al PATH del sistema
- Usar Maven wrapper si está disponible: `./mvnw` en lugar de `mvn`

**Error de compilación de Java**
- Verificar versión de Java: debe ser exactamente 17 para desarrollo
- Limpiar y recompilar: `mvn clean compile`

#### Problemas de Codificación/Caracteres:

**Caracteres extraños en nombres (acentos)**
- Verificar que la BD se creó con `CHARACTER SET utf8mb4`
- Asegurarse que ejecutaste los scripts SQL con codificación UTF-8
- Para Windows: usar terminal con codificación UTF-8

**Interfaz se ve mal (fuentes, colores)**
- Verificar que Java tiene acceso a fuentes del sistema
- En Windows: ejecutar como administrador si hay problemas de visualización

### 🚀 Guía Rápida para Usuarios Finales

**¿Quieres usar KilomboCRM? ¡Es muy fácil!**

1. **Asegúrate de tener Java 17+** (descárgalo gratis de https://adoptium.net/)
2. **Descarga el archivo** `KilomboCRM-1.0.0-jar-with-dependencies.jar`
3. **Ejecuta con doble clic** o usa: `java -jar KilomboCRM-1.0.0-jar-with-dependencies.jar`
4. **¡La aplicación se abre automáticamente!** Sin configuración previa necesaria
5. **Cuando quieras datos reales**: Ve a "Configuración" dentro de la app y configura tu base de datos

**¿Necesitas base de datos?** MySQL/MariaDB debe estar ejecutándose, pero la configuras después desde la interfaz.

### 📞 Soporte

**Si algo no funciona:**

1. **Verifica Java**: Ejecuta `java -version` en terminal
2. **Para logs detallados**: `java -jar KilomboCRM-1.0.0-jar-with-dependencies.jar > error_log.txt 2>&1`
3. **Revisa el archivo** `error_log.txt` generado
4. **Configuración BD**: Usa el panel "Configuración" dentro de la aplicación

### 📋 Opciones Avanzadas (para desarrolladores)

| Método | Requisitos | Cuándo usarlo |
|--------|------------|---------------|
| **JAR Empaquetado** | Java 17+ | **Distribución final** ⭐ |
| **Código Fuente** | Java 17 + Maven | Desarrollo y personalización |
| **JAR desde Fuente** | Java 17 + Maven | Testing y despliegue |

### 🎯 Resumen Ejecutivo

- **Archivo principal**: `KilomboCRM-1.0.0-jar-with-dependencies.jar`
- **Ejecución**: Doble clic o `java -jar archivo.jar`
- **Configuración**: Opcional, desde la interfaz de usuario
- **Requisitos**: Solo Java 17+ y MySQL/MariaDB (opcional inicialmente)