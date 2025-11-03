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

Esta es la forma más sencilla para usuarios que solo quieren usar la aplicación:

1. **Descargar el JAR empaquetado**
    - Archivo: `KilomboCRM-1.0.0-jar-with-dependencies.jar`
    - Ubicación: `target/` después de compilar

2. **Requisitos para esta opción:**
    - Java 17+ instalado
    - MySQL/MariaDB configurado (opcional al inicio)

3. **Ejecutar la aplicación**
    ```bash
    # Desde la carpeta donde está el JAR:
    java -jar KilomboCRM-1.0.0-jar-with-dependencies.jar
    ```

4. **Configurar base de datos (si no tienes una)**
    - La aplicación se abre sin BD configurada
    - Ir al panel "Configuración"
    - Ingresar datos de tu MySQL (host, usuario, password, base de datos)
    - Probar conexión y guardar

5. **Ventajas de esta opción:**
    - ✅ No requiere Maven ni compilar
    - ✅ Archivo único portable
    - ✅ Todas las dependencias incluidas
    - ✅ Puede iniciarse sin base de datos

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

### 📞 Soporte

Si encuentras problemas:

#### Para usuarios del JAR empaquetado:
1. Ejecutar con logs: `java -jar KilomboCRM-1.0.0-jar-with-dependencies.jar > error_log.txt 2>&1`
2. Revisar el archivo `error_log.txt` generado
3. Verificar que Java 17+ está instalado correctamente
4. Probar la configuración de BD desde la interfaz de la aplicación

#### Para desarrolladores:
1. Revisar los logs en la consola donde ejecutas el programa
2. Verificar que todos los pasos de instalación se completaron
3. Comprobar que MySQL está ejecutándose
4. Limpiar dependencias: `mvn clean install`
5. Revisar la documentación en [ARQUITECTURA.md](ARQUITECTURA.md) para detalles técnicos

### 📋 Resumen de Opciones de Ejecución

| Método | Requisitos | Ventajas | Para quién |
|--------|------------|----------|------------|
| **JAR Empaquetado** | Java 17+ | Más sencillo, portable, no requiere compilar | Usuarios finales |
| **Código Fuente** | Java 17 + Maven | Personalizable, actualizable | Desarrolladores |
| **JAR desde Fuente** | Java 17 + Maven | Mejor de ambos mundos | Equipos de desarrollo |

### 🎯 Recomendación

- **Para distribución**: Usa el JAR empaquetado (`KilomboCRM-1.0.0-jar-with-dependencies.jar`)
- **Para desarrollo**: Compila desde fuente con Maven
- **Para testing**: Ejecuta el JAR generado después de compilar