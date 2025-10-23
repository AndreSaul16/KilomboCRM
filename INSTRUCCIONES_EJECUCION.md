# Instrucciones de Ejecución - KilomboCRM

## 📋 Requisitos Previos

Antes de ejecutar la aplicación, asegúrate de tener instalado:

1. **JDK 17** o superior
2. **Maven 3.8+**
3. **H2 Database** (viene incluido - no requiere instalación)
4. **IDE** (opcional): IntelliJ IDEA, Eclipse o VS Code con extensiones Java

**🚨 IMPORTANTE**: Esta aplicación usa **H2 Database** (base de datos embebida) en lugar de MySQL. **No necesitas instalar ni configurar MySQL**.

## 🗄️ Paso 1: ¡No necesitas configurar la Base de Datos!

**🎉 ¡Buenas noticias!**

Esta aplicación usa **H2 Database** (base de datos embebida) que:
- ✅ **Se crea automáticamente** cuando ejecutas la aplicación
- ✅ **No requiere instalación** de MySQL u otro servidor de BD
- ✅ **Incluye datos de prueba** listos para usar
- ✅ **Funciona inmediatamente** sin configuración adicional

### ¿Qué hace automáticamente la aplicación?

1. **Crea la base de datos H2** en `~/kilombocrm.mv.db`
2. **Ejecuta los scripts SQL** (`schema.sql` y `data.sql`) automáticamente
3. **Inserta 8 clientes y 27 pedidos** de prueba
4. **Verifica la integridad** de los datos al iniciar

### Verificar que funciona (opcional)

Después de ejecutar la aplicación, puedes verificar la BD accediendo a la **consola web de H2**:
- URL: `http://localhost:8082`
- JDBC URL: `jdbc:h2:~/kilombocrm`
- Usuario: `sa`
- Password: *(vacío)*

En la consola podrás ejecutar consultas SQL para ver los datos.

## ⚙️ Paso 2: ¡La configuración ya está lista!

**🎉 ¡No necesitas editar nada!**

La aplicación viene **pre-configurada** para usar H2 Database:

```properties
# Configuración automática (NO EDITAR)
db.url=jdbc:h2:~/kilombocrm;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
db.username=sa
db.password=
db.driver=org.h2.Driver
```

**¿Qué significa esta configuración?**
- `jdbc:h2:~/kilombocrm`: BD se guarda en tu directorio home
- `DB_CLOSE_ON_EXIT=FALSE`: Los datos persisten entre ejecuciones
- `AUTO_SERVER=TRUE`: Permite conexiones remotas si es necesario
- Usuario `sa` sin password (configuración estándar de H2)

## 🔨 Paso 3: Compilar el Proyecto

Desde la raíz del proyecto, ejecuta:

```bash
mvn clean compile
```

Esto descargará las dependencias y compilará el código.

## 🚀 Paso 4: Ejecutar la Aplicación

### Opción A: Usando Maven

```bash
mvn exec:java -Dexec.mainClass="com.kilombo.crm.presentation.MainFrame"
```

### Opción B: Usando tu IDE

1. Abre el proyecto en tu IDE
2. Navega a [`src/main/java/com/kilombo/crm/presentation/MainFrame.java`](src/main/java/com/kilombo/crm/presentation/MainFrame.java)
3. Ejecuta el método `main()`

### Opción C: Crear JAR ejecutable

```bash
mvn clean package
java -jar target/KilomboCRM-1.0.0-jar-with-dependencies.jar
```

## 🧪 Paso 5: Probar la Aplicación

### 5.1 Verificar Conexión

Al iniciar la aplicación, verás en la consola mensajes como:
```
INFO: Intentando establecer conexión a BD (intento 1/3)
INFO: Conexión a BD establecida exitosamente
INFO: Esquema de base de datos validado correctamente
INFO: Aplicación iniciada correctamente
```

**¿Qué significan estos mensajes?**
- ✅ **Conexión exitosa**: H2 Database se creó/inició correctamente
- ✅ **Esquema validado**: Las tablas existen y están correctas
- ✅ **Datos listos**: 8 clientes y 27 pedidos disponibles

Si hay problemas, la aplicación mostrará mensajes específicos y sugerencias de solución.

### 5.2 Probar Gestión de Clientes

1. **Listar Clientes**:
   - La tabla debe mostrar los 8 clientes de prueba
   - Verifica que se muestren: ID, Nombre, Apellido, Email, Teléfono

2. **Añadir Cliente**:
   - Click en "Añadir"
   - Completa el formulario:
     - Nombre: "Test"
     - Apellido: "Usuario"
     - Email: "test@example.com"
     - Teléfono: "+34 600 000 000"
   - Click en "Guardar"
   - Verifica que aparece en la tabla

3. **Modificar Cliente**:
   - Selecciona un cliente
   - Click en "Modificar"
   - Cambia algún dato
   - Click en "Guardar"
   - Verifica que se actualizó

4. **Eliminar Cliente**:
   - Selecciona un cliente (preferiblemente el de prueba)
   - Click en "Eliminar"
   - Confirma la eliminación
   - Verifica que desaparece de la tabla

5. **Ver Pedidos de Cliente**:
   - Selecciona un cliente con pedidos
   - Click en "Ver Pedidos"
   - Verifica que cambia a la pestaña de pedidos
   - Verifica que se filtran solo los pedidos de ese cliente

### 5.3 Probar Gestión de Pedidos

1. **Listar Pedidos**:
   - Cambia a la pestaña "Pedidos"
   - La tabla debe mostrar los 27 pedidos de prueba
   - Verifica: ID, Cliente, Fecha, Total

2. **Filtrar por Cliente**:
   - Usa el combo "Filtrar por cliente"
   - Selecciona un cliente
   - Verifica que solo se muestran sus pedidos

3. **Añadir Pedido**:
   - Click en "Añadir"
   - Selecciona un cliente
   - Selecciona una fecha
   - Ingresa un total (ej: 150.50)
   - Click en "Guardar"
   - Verifica que aparece en la tabla

4. **Modificar Pedido**:
   - Selecciona un pedido
   - Click en "Modificar"
   - Cambia el total
   - Click en "Guardar"
   - Verifica que se actualizó

5. **Eliminar Pedido**:
   - Selecciona un pedido
   - Click en "Eliminar"
   - Confirma la eliminación
   - Verifica que desaparece

### 5.4 Probar Validaciones

1. **Cliente con email duplicado**:
   - Intenta crear un cliente con un email existente
   - Debe mostrar error: "Ya existe un cliente con el email..."

2. **Campos obligatorios vacíos**:
   - Intenta guardar un cliente sin nombre
   - Debe mostrar error: "El nombre es obligatorio"

3. **Email inválido**:
   - Intenta guardar un cliente con email "test"
   - Debe mostrar error: "El formato del email no es válido"

4. **Pedido con fecha futura**:
   - Intenta crear un pedido con fecha futura
   - Debe mostrar error: "La fecha del pedido no puede ser futura"

5. **Pedido con total negativo**:
   - Intenta crear un pedido con total 0 o negativo
   - Debe mostrar error: "El total debe ser mayor que cero"

### 5.5 Probar Funcionalidades del Menú

1. **Archivo → Actualizar Todo** (F5):
   - Recarga todos los datos
   - Verifica que se actualizan ambas tablas

2. **Ver → Clientes** (Ctrl+1):
   - Cambia a la pestaña de clientes

3. **Ver → Pedidos** (Ctrl+2):
   - Cambia a la pestaña de pedidos

4. **Ayuda → Probar Conexión**:
   - Muestra información de la conexión a BD

5. **Ayuda → Acerca de**:
   - Muestra información de la aplicación

## 🐛 Solución de Problemas

### Error: "No se pudo conectar a la base de datos"

**Causas posibles**:
1. Archivo de BD corrupto (`~/kilombocrm.mv.db`)
2. Puerto 9092 ocupado (usado por H2)
3. Permisos insuficientes en el directorio home
4. Versión de Java incompatible

**Solución**:
```bash
# 1. Eliminar archivo de BD corrupto (pierdes los datos)
rm ~/kilombocrm.mv.db

# 2. Verificar puerto disponible
netstat -an | grep 9092

# 3. Recompilar y ejecutar
mvn clean compile
mvn exec:java -Dexec.mainClass="com.kilombo.crm.presentation.MainFrame"
```

### Error: "Esquema de base de datos corrupto"

**Causa**: Los scripts SQL no se ejecutaron correctamente

**Solución**: La aplicación intentará recrear las tablas automáticamente en el siguiente inicio.

### Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Causa**: Dependencia de MySQL no descargada

**Solución**:
```bash
mvn clean install
```

### Error: "ValidationException" al guardar

**Causa**: Datos no cumplen las validaciones

**Solución**: Verifica que:
- Nombre y apellido no estén vacíos
- Email tenga formato válido
- Teléfono tenga formato válido (si se proporciona)
- Total del pedido sea mayor que 0
- Fecha del pedido no sea futura

### La aplicación se cierra inmediatamente

**Causa**: Excepción no capturada al iniciar

**Solución**:
```bash
# Ejecutar con salida de errores
mvn exec:java -Dexec.mainClass="com.kilombo.crm.presentation.MainFrame" 2>&1 | tee error.log
```

Revisa `error.log` para ver el error específico.

## 📊 Verificar Datos en H2 Database

Puedes verificar los datos usando la **consola web de H2**:

1. **Acceder a la consola**: http://localhost:8082
2. **Credenciales**:
   - JDBC URL: `jdbc:h2:~/kilombocrm`
   - Usuario: `sa`
   - Password: *(vacío)*

### Consultas útiles:

```sql
-- Ver todos los clientes
SELECT * FROM cliente ORDER BY apellido, nombre;

-- Ver todos los pedidos con nombre del cliente
SELECT p.id, c.nombre, c.apellido, p.fecha, p.total
FROM pedido p
INNER JOIN cliente c ON p.id_cliente = c.id
ORDER BY p.fecha DESC;

-- Estadísticas por cliente
SELECT
    c.id,
    c.nombre,
    c.apellido,
    COUNT(p.id) AS num_pedidos,
    COALESCE(SUM(p.total), 0) AS total_gastado
FROM cliente c
LEFT JOIN pedido p ON c.id = p.id_cliente
GROUP BY c.id, c.nombre, c.apellido
ORDER BY total_gastado DESC;

-- Ver estructura de tablas
SHOW TABLES;
DESCRIBE cliente;
DESCRIBE pedido;
```

## 🧹 Limpiar y Reiniciar

Si quieres empezar de cero (eliminar todos los datos):

```bash
# 1. Detener la aplicación si está ejecutándose

# 2. Eliminar archivo de base de datos H2
rm ~/kilombocrm.mv.db

# 3. Limpiar y recompilar
mvn clean compile

# 4. Ejecutar (se recreará la BD automáticamente)
mvn exec:java -Dexec.mainClass="com.kilombo.crm.presentation.MainFrame"
```

**Nota**: Esto eliminará todos los datos que hayas añadido. Los datos de prueba se restaurarán automáticamente.

## 📝 Notas Adicionales

- La aplicación usa **auto-commit** por defecto para simplicidad
- Los cambios se guardan inmediatamente en la BD H2
- La eliminación de un cliente elimina automáticamente sus pedidos (CASCADE)
- Todas las operaciones incluyen **logging detallado** para debugging
- **Reintentos automáticos** en caso de errores temporales de conexión
- **Validaciones multinivel**: UI, aplicación, dominio y BD
- Las fechas se muestran en formato dd/MM/yyyy
- Los importes se muestran con 2 decimales y símbolo €
- **Recuperación automática**: La aplicación continúa funcionando tras errores

## ✅ Checklist de Verificación

- [x] **JDK 17+ instalado**
- [x] **Maven 3.8+ instalado**
- [x] **Proyecto clonado/descargado**
- [x] **Proyecto compilado sin errores** (`mvn clean compile`)
- [x] **Aplicación inicia correctamente** (ventana Swing se abre)
- [x] **Conexión a BD H2 verificada** (mensajes en consola)
- [x] **Esquema de BD validado** (tablas creadas automáticamente)
- [x] **Datos de prueba cargados** (8 clientes, 27 pedidos)
- [x] **Operaciones CRUD de clientes funcionan**
- [x] **Operaciones CRUD de pedidos funcionan**
- [x] **Validaciones funcionan correctamente**
- [x] **Filtros y búsquedas funcionan**
- [x] **Menús y atajos de teclado funcionan**
- [x] **Logging muestra información útil**
- [x] **Recuperación automática de errores**

## 🎉 ¡Listo para Aprender y Desarrollar!

Si todos los pasos funcionan correctamente, tienes una aplicación completamente funcional que demuestra:

### 🏗️ Arquitectura Profesional
- **Clean Architecture** aplicada correctamente
- **Clean Code** con mejores prácticas
- **Principios SOLID** implementados
- **Patrones de Diseño** reales

### 🛡️ Robustez Empresarial
- **Manejo de errores** completo y profesional
- **Validaciones** en múltiples niveles
- **Logging** integrado y útil
- **Recuperación automática** de fallos

### 📚 Material Educativo
- **Código comentado** y autodocumentado
- **Documentación completa** en múltiples formatos
- **Ejemplos reales** de desarrollo Java
- **Progresión de aprendizaje** clara

### 🚀 Próximos Pasos de Aprendizaje

Después de entender este proyecto, puedes avanzar a:

1. **Spring Boot** - Framework web profesional
2. **JPA/Hibernate** - Mapeo objeto-relacional
3. **REST APIs** - Servicios web
4. **Testing avanzado** - JUnit, Mockito, integración
5. **Docker** - Contenerización
6. **CI/CD** - Integración y despliegue continuo

### 📖 Recursos de Aprendizaje Incluidos

- [`README.md`](README.md) - Visión general y guía de aprendizaje
- [`ARQUITECTURA.md`](ARQUITECTURA.md) - Arquitectura detallada
- [`PLAN_IMPLEMENTACION.md`](PLAN_IMPLEMENTACION.md) - Cómo se construyó
- [`DIAGRAMAS.md`](DIAGRAMAS.md) - Diagramas visuales
- [`RESUMEN_ARQUITECTURA.md`](RESUMEN_ARQUITECTURA.md) - Resumen ejecutivo

### 💡 Consejos para Juniors

1. **Lee la documentación** antes de tocar el código
2. **Entiende la arquitectura** antes de modificar
3. **Experimenta** cambiando funcionalidades pequeñas
4. **Revisa los logs** para entender qué hace la aplicación
5. **Aprende de los errores** - están diseñados para enseñar

---

**¡Felicitaciones! Has completado un proyecto Java profesional completo. Ahora tienes las bases para convertirte en un desarrollador Java senior. ¡Sigue aprendiendo!** 🎓