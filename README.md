# PharmaMobil 📱💊

Aplicativa móvil multiplataforma para gestión de farmacias, desarrollada con **Kotlin Multiplatform (KMP)**.

## 🎓 Contexto Acad émico

- **Universidad:** Universidad Peruana Unión (UPeU)
- **Ciclo:** VIII Ciclo - 2026-2
- **Asignatura:** Desarrollo de Aplicaciones Móviles
- **Estudiante:** Fabian Rodriguez

## 🏗️ Arquitectura del Proyecto

```
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
└── iosApp/
```

## 📦 Modelo de Dominio

### `Cliente` 🧑‍💼

```kotlin
data class Cliente(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String?
)
```

**M étodos:**
- `obtenerTelefono()` - Retorna el teléfono o "No registrado" si es null

**Caracter ísticas:**
- Null-safety con operador Elvis `?:`
- Inmutabilidad con `val`

---

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

**M étodos:**
- `verificarStock(cantidad)` - Valida si hay stock suficiente
- `estadoDisponible()` - Retorna `true` si stock > 0
- `valorInventario()` - Calcula precio × stock
- `disminuirStock(cantidad)` - Reduce stock de forma segura con validaciones

---

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

---

### `DetallePedido` 📝

```kotlin
data class DetallePedido(
    val producto: Producto,
    val cantidad: Int
)
```

**Validaci ón:**
- Cantidad debe ser mayor que 0

**M étodos:**
- `subtotal()` - Calcula precio × cantidad

---

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
- Evaluaci ón exhaustiva con `when`
- Transporte de datos en `Rechazado(motivo)`

---

## 🔧 Consultas Funcionales

### `ProductoQueries.kt`

| Funci ón | Descripci ón | Operador |
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

## ⚡ Asincron ía con Corrutinas y Flow

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

**Implementaci ón `ProductoRepositoryFake`:**
- Simula latencia de red con `delay(1000)`
- Manejo de errores con `Result`
- L ógica para aumentar o disminuir stock

---

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

**Flujo de emisi ón:**
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

## 🎯 Caracter ísticas Implementadas

### Null-Safety ✅
- Uso de tipos `String?` para datos opcionales
- Operador Elvis `?:` para valores por defecto
- Evita operador `!!` (aserci ón forzada)

### Inmutabilidad ✅
- Propiedades declaradas con `val`
- Data classes con m étodo `copy()`
- Listas inmutables con `toList()`

### Data Classes ✅
- Generaci ón autom ática de `equals`, `hashCode`, `toString`
- Pattern matching con `when`
- Desestructuraci ón de objetos

### Sealed Classes ✅
- Estados restringidos a conjunto conocido
- Evaluaci ón exhaustiva en compilaci ón
- Transporte de datos en subtipos

### Corrutinas ✅
- Funciones `suspend` para operaciones as íncronas
- `delay()` para simulaci ón de latencia
- No bloqueo del hilo principal

### Flow ✅
- Emisi ón m últiple de valores en el tiempo
- Manejo reactivo de estados
- Integraci ón con ViewModel (StateFlow)

---

## 🛠️ Stack Tecnol ógico

| Tecnolog ía | Prop ósito |
|------------|------------|
| Kotlin Multiplatform | Compartici ón de c ódigo Android/iOS |
| Corrutinas | Asincron ía no bloqueante |
| Flow | Programaci ón reactiva |
| Clean Architecture | Separaci ón de capas |

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

**Fabian Rodriguez**  
Sistemas Engineering Student - UPeU  
[GitHub](https://github.com/fabianrodriguez33)

---

## 📄 Licencia

Proyecto acad émico para la asignatura de Desarrollo de Aplicaciones M óviles.
