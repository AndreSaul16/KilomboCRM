# Instrucciones de Instalación - KilomboCRM

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