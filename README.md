# PharmaMobil 📱💊

Aplicativa móvil multiplataforma para gestión de farmacias, desarrollada con Kotlin Multiplatform (KMP).

<div align="center">
  <img src="https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin" alt="Kotlin Multiplatform" />
  <img src="https://img.shields.io/badge/Android-Compose-3DDC84?style=for-the-badge&logo=android" alt="Android Compose" />
  <img src="https://img.shields.io/badge/iOS-Ready-000000?style=for-the-badge&logo=apple" alt="iOS" />
</div>

## 🎓 Contexto académico

- **Universidad:** Universidad Peruana Unión (UPeU)
- **Ciclo:** VIII Ciclo - 2026-2
- **Asignatura:** Desarrollo de Aplicaciones Móviles
- **Estudiante:** Andrey Mestanza
- **GitHub:** https://github.com/17Yerdna

---

## 🏗️ Arquitectura del proyecto

```text
PharmaMobil/
├── shared/
│   └── src/commonMain/kotlin/pe/edu/upeu/pharmamobil/
│       ├── domain/
│       │   ├── model/          # Entidades de dominio
│       │   ├── usecase/        # Casos de uso y consultas
│       │   └── result/         # Resultados sellados
│       ├── data/
│       │   └── repository/     # Repositorios
│       └── demo/               # Demostraciones
├── androidApp/
├── iosApp/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
└── README.md
```

---

## 📦 Modelo de dominio

### `Cliente` 🧑‍💼

```kotlin
data class Cliente(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String?
)
```

**Métodos:**
- `obtenerTelefono()` - Retorna el teléfono o "No registrado" si es null.

**Características:**
- Null-safety con operador Elvis `?:`
- Inmutabilidad con `val`

### `Producto` 💊

```kotlin
data class Producto(
    val id: Long,
    val nombre: String,
    val precio: Double,
    val stock: Int
)
```

**Validaciones en `init`:**
- Nombre no vacío (`isNotBlank()`)
- Precio mayor a cero
- Stock no negativo

**Métodos:**
- `verificarStock(cantidad)` - Valida si hay stock suficiente
- `estadoDisponible()` - Retorna `true` si stock > 0
- `valorInventario()` - Calcula precio × stock
- `disminuirStock(cantidad)` - Reduce stock de forma segura con validaciones

### `Pedido` 📋

```kotlin
data class Pedido(
    val id: Long,
    val cliente: Cliente,
    val detalles: List<DetallePedido>,
    val estado: EstadoPedido
)
```

**Relaciones:**
- Un `Pedido` pertenece a un `Cliente`
- Un `Pedido` contiene múltiples `DetallePedido`

### `DetallePedido` 📝

```kotlin
data class DetallePedido(
    val producto: Producto,
    val cantidad: Int
)
```

**Validación:**
- Cantidad debe ser mayor que 0

**Métodos:**
- `subtotal()` - Calcula precio × cantidad

### `EstadoPedido` 🔄

```kotlin
sealed class EstadoPedido {
    data object Pendiente : EstadoPedido()
    data object Procesando : EstadoPedido()
    data object Entregado : EstadoPedido()
    data class Rechazado(val motivo: String) : EstadoPedido()
}
```

**Ventajas:**
- Conjunto cerrado de estados conocidos
- Evaluación exhaustiva con `when`
- Transporte de datos en `Rechazado(motivo)`

---

## 🔧 Consultas funcionales

### `ProductoQueries.kt`

| Función | Descripción | Operador |
|----------|-------------|----------|
| `productosDisponibles()` | Filtra productos con stock > 0 | `filter` |
| `nombresDeProductos()` | Extrae nombres de productos | `map` |
| `buscarProductoPorId()` | Busca producto por ID | `find` |
| `valorTotalInventario()` | Calcula valor total del inventario | `sumOf` |
| `productosConStockBajo()` | Filtra productos con stock entre 1 y límite | `filter` |

**Ejemplo de uso:**

```kotlin
val disponibles = productosDisponibles(productos)
val nombres = nombresDeProductos(productos)
val producto = buscarProductoPorId(productos, 2L)
val total = valorTotalInventario(productos)
```

---

## ⚡ Async con corrutinas y Flow

### `ProductoRepository` 🗄️

```kotlin
interface ProductoRepository {
    suspend fun obtenerProductos(): List<Producto>
    suspend fun actualizarStock(
        productoId: Long,
        nuevoStock: Int
    ): Result<Producto>
}
```

**Implementación `ProductoRepositoryFake`:**
- Simula latencia de red con `delay(1000)`
- Manejo de errores con `Result`
- Lógica para aumentar o disminuir stock

### `ObservarProductosUseCase` 🔄

```kotlin
class ObservarProductosUseCase(
    private val repository: ProductoRepository
) {
    operator fun invoke(): Flow<ResultadoProductos>
}
```

**Estados de `ResultadoProductos`:**

```kotlin
sealed class ResultadoProductos {
    data object cargando : ResultadoProductos()
    data class Exito(val productos: List<Producto>) : ResultadoProductos()
    data class Error(val mensaje: String) : ResultadoProductos()
}
```

**Flujo de emisión:**
1. `cargando` → Muestra indicador de carga
2. `Exito` → Emite lista de productos
3. `Error` → Maneja excepciones

---

## 🧪 Testing

### `SharedLogicAndroidHostTest`

```kotlin
class SharedLogicAndroidHostTest {
    @Test
    fun clienteTelefono() {
        val cliente = Cliente(
            id = 1L,
            nombre = "Farmacia Laufarma",
            correo = "ventas@laufarma.pe",
            telefono = "987654321"
        )
        val resultado = cliente.obtenerTelefono()
        assertEquals(expected = "987654321", actual = resultado)
    }
}
```

---

## 🎯 Características implementadas

### Null-Safety ✅
- Uso de tipos `String?` para datos opcionales
- Operador Elvis `?:` para valores por defecto
- Evita operador `!!` (aserción forzada)

### Inmutabilidad ✅
- Propiedades declaradas con `val`
- Data classes con método `copy()`
- Listas inmutables con `toList()`

### Data Classes ✅
- Generación automática de `equals`, `hashCode`, `toString`
- Pattern matching con `when`
- Desestructuración de objetos

### Sealed Classes ✅
- Estados restringidos a conjunto conocido
- Evaluación exhaustiva en compilación
- Transporte de datos en subtipos

### Corrutinas ✅
- Funciones `suspend` para operaciones asíncronas
- `delay()` para simulación de latencia
- No bloqueo del hilo principal

### Flow ✅
- Emisión múltiple de valores en el tiempo
- Manejo reactivo de estados
- Integración con ViewModel (StateFlow)

---

## 🛠️ Stack tecnológico

| Tecnología | Propósito |
|------------|-----------|
| Kotlin Multiplatform | Compartición de código Android/iOS |
| Corrutinas | Asincronía no bloqueante |
| Flow | Programación reactiva |
| Clean Architecture | Separación de capas |
| Compose Multiplatform | UI compartida |

---

## 🚀 Ejecución

### Requisitos
- Android Studio Koala o superior
- JDK 17
- Android SDK
- Gradle 8.0+

### Comandos

```bash
# Sincronizar proyecto
./gradlew :shared:sync

# Ejecutar tests
./gradlew :shared:allTests

# Build del proyecto
./gradlew build
```

---

## 📝 Buenas prácticas aplicadas

1. Código 100% `commonMain` - sin dependencias de Android/iOS
2. Commits atómicos - cada cambio en un commit separado
3. Validaciones en `init` - reglas de negocio en el constructor
4. Propiedades derivadas - cálculos como funciones, no como `var`
5. Manejo seguro de nulos - sin `!!`, con `?:` y `?.`

---

## 👨‍💻 Autor

**Andrey Mestanza**  
Sistemas Engineering Student - UPeU  
[GitHub](https://github.com/17Yerdna)

---

## 📄 Licencia

Proyecto académico para la asignatura de Desarrollo de Aplicaciones Móviles.

---

## 📁 Estructura de Archivos

```
shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/
├── domain/
│   ├── model/
│   │   ├── Cliente.kt
│   │   ├── Producto.kt
│   │   ├── Pedido.kt
│   │   ├── DetallePedido.kt
│   │   └── EstadoPedido.kt
│   ├── usecase/
│   │   ├── ProductoQueries.kt
│   │   └── ObservarProductosUseCase.kt
│   └── result/
│       └── ResultadoProductos.kt
├── data/
│   └── repository/
│       └── ProductoRepository.kt
└── demo/
    └── DemoFunctions.kt
```

---

## 🚀 Ejecuci ón

### Requisitos
- Android Studio Koala o superior
- JDK 17
- Android SDK
- Gradle 8.0+

### Comandos
```bash
# Sincronizar proyecto
./gradlew :shared:sync

# Ejecutar tests
./gradlew :shared:allTests

# Build del proyecto
./gradlew build
```

---

## 📝 Buenas Pr ácticas Aplicadas

1. **C ódigo 100% commonMain** - Sin dependencias de Android/iOS
2. **Commits at ómicos** - Cada cambio en un commit separado
3. **Validaciones en `init`** - Reglas de negocio en el constructor
4. **Propiedades derivadas** - C álculos como funciones, no como `var`
5. **Manejo seguro de nulos** - Sin `!!`, con `?:` y `?.`

---

## 👨‍💻 Autor

**Andrey Mestanza**  
Sistemas Engineering Student - UPeU  
[GitHub](https://github.com/17Yerdna)

---

## 📄 Licencia

Proyecto acad émico para la asignatura de Desarrollo de Aplicaciones M óviles.
