DetallePedido carece de validaciones de negocio (debería tener reglas como cantidad > 0, costo/precio válidos)
Duplicación masiva de código: Patrón try-catch repetido en todos los métodos
Métodos muy largos: Algunos métodos superan las 50 líneas
Violación DRY: Manejo de excepciones idéntico en ClienteRepositoryImpl, PedidoRepositoryImpl, DetallePedidoRepositoryImpl
Duplicación: Patrón de logging y manejo de excepciones repetido
Métodos largos: listarPedidos() y obtenerPedidosPorCliente() son complejos
Problemas identificados:

MainFrame sobrecargado: 509 líneas, maneja demasiadas responsabilidades
Violación SRP: MainFrame hace de controlador, vista y coordinador
Dependencias directas: Importa implementaciones concretas en lugar de interfaces
Métodos largos: initComponents() y setupClientesActionPanel() muy extensos

Problemas identificados:

ConexionBD muy largo: 496 líneas, debería dividirse
Múltiples responsabilidades: ConexionBD maneja conexión, configuración y validación