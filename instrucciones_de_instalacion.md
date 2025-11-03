# Instrucciones de Instalación - KilomboCRM

## 🚀 Guía Rápida para Usuarios Finales

**¿Quieres usar KilomboCRM? ¡Es muy fácil!**

### 📋 Opción 1: JAR Ejecutable (Más Sencillo)

1. **Descarga el JAR ejecutable**
   - `KilomboCRM-1.0.0-jar-with-dependencies.jar`
   - Archivo único que contiene todo lo necesario

2. **Ejecuta la aplicación**
   - Doble clic en el archivo JAR
   - O usa el comando: `java -jar KilomboCRM-1.0.0-jar-with-dependencies.jar`

3. **¡Listo! La aplicación funciona inmediatamente**
   - ✅ Sin instalación compleja
   - ✅ Funciona sin base de datos inicialmente
   - ✅ Configuración opcional posterior

### 📋 Opción 2: Instalador EXE Completo (¡Ya disponible!)

**¡El instalador MSI está listo!** Archivo: `KilomboCRM-Installer.exe`

1. **Descarga el instalador**
   - `KilomboCRM-Installer.exe` (4.2 MB)
   - Ubicación: `target/KilomboCRM-Installer.exe`

2. **Ejecuta el instalador**
   - Doble clic en `KilomboCRM-Installer.exe`
   - El instalador detectará automáticamente si necesitas Java y MySQL
   - Si no están instalados, te preguntará si quieres instalarlos

3. **¡Instalación completa automática!**
   - ✅ Instala la aplicación
   - ✅ Crea accesos directos en escritorio y menú inicio
   - ✅ Registra en programas instalados
   - ✅ Incluye desinstalador

4. **Características del instalador:**
   - Detecta Java automáticamente
   - Ofrece instalar MySQL si no existe
   - Instalación silenciosa opcional
   - Desinstalador completo incluido
   - Compatible con Windows 10/11

### 📋 Opción 2: Instalación Manual (Si prefieres control total)

#### Lo Único que Necesitas
1. **Java 17+ instalado** (gratuito)
   - Descargar desde: https://adoptium.net/
   - Verificar instalación: `java -version`

2. **MySQL/MariaDB ejecutándose** (opcional inicialmente)
   - Solo si quieres guardar datos reales
   - Se configura después desde la aplicación

#### Ejecutar la Aplicación
1. **Descarga el archivo JAR**
   - `KilomboCRM-1.0.0-jar-with-dependencies.jar`

2. **Ejecuta con doble clic** o usa el comando:
   ```bash
   java -jar KilomboCRM-1.0.0-jar-with-dependencies.jar
   ```

3. **¡La aplicación se abre automáticamente!**
   - ✅ Sin configuración previa
   - ✅ Funciona inmediatamente
   - ✅ Interfaz completa disponible

### 🗄️ Configurar Base de Datos (Opcional - Después)

Cuando quieras guardar datos reales:

1. **Ve al panel "Configuración"** dentro de la aplicación
2. **Ingresa los datos de tu MySQL/MariaDB:**
   - Host: `localhost` (o IP del servidor)
   - Usuario: tu usuario de MySQL
   - Contraseña: tu contraseña
   - Base de datos: nombre de tu BD
3. **Prueba la conexión** y guarda

### 📦 Información para Desarrolladores

Si quieres modificar el código fuente o compilar desde cero:

#### Requisitos para Desarrollo:
- **Java 17** (JDK exacto)
- **Maven 3.6+**
- **MySQL 8.0+**

#### Pasos para Desarrollo:
1. **Clonar/compilar el proyecto**
2. **Configurar BD** (opcional inicialmente)
3. **Ejecutar**: `mvn exec:java -Dexec.mainClass="com.kilombo.crm.presentation.MainFrame"`
4. **Generar JAR**: `mvn clean package`

### 🔧 Solución de Problemas

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

#### Para Distribución Fácil:
- **Archivo recomendado**: `KilomboCRM-1.0.0-jar-with-dependencies.jar`
- **Ejecución**: Doble clic - funciona inmediatamente
- **Usuario final**: Sin instalación, sin configuración previa

#### Para Distribución Avanzada:
- **Archivo principal**: `KilomboCRM-1.0.0-jar-with-dependencies.jar`
- **Ejecución**: Doble clic o `java -jar archivo.jar`
- **Configuración**: Opcional, desde la interfaz de usuario
- **Requisitos**: Solo Java 17+ y MySQL/MariaDB (opcional inicialmente)

### 📦 Archivos para Distribución

1. **Para usuarios finales (recomendado)**: `KilomboCRM-Installer.exe` ⭐
2. **Para usuarios avanzados**: `KilomboCRM-1.0.0-jar-with-dependencies.jar`
3. **Para desarrollo**: Código fuente completo + `KilomboCRM.bat`