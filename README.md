# PharmaMobil 📱💊

> **Aplicación móvil multiplataforma para la gestión moderna de farmacias**, desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform (Material 3)**.

<div align="center">

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform_1.11-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Material 3](https://img.shields.io/badge/Material_3-Ready-795548?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Android](https://img.shields.io/badge/Android-API_24+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![iOS Ready](https://img.shields.io/badge/iOS-14+-000000?style=for-the-badge&logo=apple&logoColor=white)](https://apple.com)
[![Tests](https://img.shields.io/badge/Tests-10%20Passed%20(100%25)-brightgreen?style=for-the-badge&logo=githubactions&logoColor=white)](#-testing-y-control-de-calidad)

</div>

---

## 🎓 Contexto Académico

* **Institución:** Universidad Peruana Unión (UPeU)
* **Carrera:** Ingeniería de Sistemas
* **Asignatura:** Desarrollo de Aplicaciones Móviles
* **Ciclo:** VIII Ciclo
* **Estudiante:** Andrey Mestanza
* **Perfil GitHub:** [@17Yerdna](https://github.com/17Yerdna)

---

## 🌟 Novedad: Sesión 3 - Formulario Reactivo de Productos

En esta etapa se implementó el **Reto 02: Gestión del Estado, Validación y Retroalimentación Visual**, integrando un flujo declarativo y seguro para el registro de productos farmacéuticos.

### 📋 Reglas Funcionales y Validación Secuencial

| Campo Evaluado | Regla Estricta | Mensaje de Error en UI |
| :--- | :--- | :--- |
| **Nombre** | No vacío ni compuesto únicamente por espacios (`isNotBlank()`) | *"El nombre es obligatorio."* |
| **Precio** | Conversión numérica exitosa (`toDoubleOrNull()`) | *"Ingrese un precio numérico."* |
| **Precio** | Valor estrictamente positivo (`> 0.0`) | *"El precio debe ser mayor que cero."* |
| **Stock** | Formato de número entero (`toIntOrNull()`) | *"Ingrese un stock entero."* |
| **Stock** | Cantidad no negativa (`>= 0`) | *"El stock no puede ser negativo."* |

### ✨ Características del Formulario (`ProductoScreen.kt`):
* **Estados Reactivos:** Control de entradas con `remember { mutableStateOf(...) }` para preservar la experiencia del usuario entre recomposiciones.
* **Conversión Segura (`*OrNull`):** Eliminación total de excepciones fatales (`NumberFormatException`) previniendo caídas imprevistas (*crashes*).
* **Control de Intento de Envío:** La propiedad `isError` y los avisos visuales se activan **únicamente tras pulsar el botón de Registrar**, evitando alertar al usuario antes de interactuar.
* **Limpieza Automática:** Tras registrar con éxito, se restablecen los campos a cadenas vacías y se reinicia el estado de validación.

---

## 🏗️ Arquitectura del Proyecto

El proyecto aplica **Clean Architecture** compartiendo la lógica de negocio y presentación en el módulo `shared`:

```text
PharmaMobil/
├── shared/
│   └── src/commonMain/kotlin/pe/edu/upeu/pharmamobil/
│       ├── domain/
│       │   ├── model/          # Entidades con invariantes (Producto, Cliente, etc.)
│       │   ├── usecase/        # Casos de uso y consultas funcionales
│       │   └── result/         # Clases selladas de respuesta (ResultadoProductos)
│       ├── data/
│       │   └── repository/     # Repositorios (simulación reactiva)
│       ├── presentation/
│       │   └── producto/       # UI reactiva en Compose (ProductoScreen.kt)
│       └── demo/               # Demostraciones de flujo reactivo
├── androidApp/                 # Punto de entrada y configuración nativa de Android
├── iosApp/                     # Punto de entrada Swift/SwiftUI para iOS
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 📦 Modelo de Dominio

### `Producto` 💊
Entidad central con invariantes de validación en su constructor (`init`):
```kotlin
data class Producto(
    val id: Long,
    val nombre: String,
    val precio: Double,
    val stock: Int
) {
    init {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(precio > 0) { "El precio debe ser mayor que 0" }
        require(stock >= 0) { "El stock no puede ser negativo" }
    }
}
```
* **Métodos principales:** `verificarStock(cantidad)`, `estadoDisponible()`, `valorInventario()`, `disminuirStock(cantidad)`.

### `Cliente` 🧑‍💼
```kotlin
data class Cliente(
    val id: Long,
    val nombre: String,
    val correo: String,
    val telefono: String?
) {
    fun obtenerTelefono(): String = telefono ?: "No registrado"
}
```

### `Pedido` y `DetallePedido` 📋
* **`DetallePedido`:** Representa un ítem adquirido calculando subtotales (`precio * cantidad`).
* **`EstadoPedido`:** `sealed class` con estados tipados: `Pendiente`, `Procesando`, `Entregado` y `Rechazado(motivo)`.

---

## ⚡ Asincronía, Corrutinas y Flow

* **`ProductoRepository`:** Interface para obtención de datos con funciones suspendidas (`suspend`).
* **`ObservarProductosUseCase`:** Emite flujos reactivos (`Flow<ResultadoProductos>`) modelando tres estados:
  1. `cargando` $\rightarrow$ Renderizado de indicadores de progreso.
  2. `Exito` $\rightarrow$ Emisión inmutable de la lista de productos.
  3. `Error` $\rightarrow$ Captura y propagación segura de fallos.

---

## 🧪 Testing y Control de Calidad

La lógica del formulario y del dominio está respaldada por una suite de pruebas unitarias automatizadas:

```bash
./gradlew :shared:allTests
```

### Matriz de Casos de Prueba (`ProductoValidationTest.kt`)

| Caso | Nombre | Precio | Stock | Resultado Verificado | Estado |
| :---: | :--- | :---: | :---: | :--- | :---: |
| **1** | `Paracetamol 500 mg` | `8.50` | `100` | Registro exitoso y objeto `Producto` creado | ✅ Aprobado |
| **2** | `""` (Vacío / Espacios) | `8.50` | `100` | Error: *"El nombre es obligatorio."* | ✅ Aprobado |
| **3** | `Ibuprofeno` | `abc` | `50` | Error: *"Ingrese un precio numérico."* | ✅ Aprobado |
| **4** | `Ibuprofeno` | `0` | `50` | Error: *"El precio debe ser mayor que cero."* | ✅ Aprobado |
| **5** | `Amoxicilina` | `18.50` | `abc` | Error: *"Ingrese un stock entero."* | ✅ Aprobado |
| **6** | `Amoxicilina` | `18.50` | `-5` | Error: *"El stock no puede ser negativo."* | ✅ Aprobado |
| **7** | `Loratadina` | `10` | `0` | Registro exitoso con stock cero | ✅ Aprobado |

**Resultado total:** `10 tests ejecutados, 0 fallos (100% de éxito).`

---

## 🚀 Ejecución del Proyecto

### Requisitos Previos
* **JDK 17** o superior
* **Android SDK** (API 24+)
* **Android Studio** o IntelliJ IDEA con soporte Kotlin Multiplatform

### Comandos de Utilidad

```powershell
# Ejecutar todas las pruebas unitarias
./gradlew :shared:allTests

# Compilar e instalar la aplicación en tu emulador o dispositivo Android
./gradlew :androidApp:installDebug

# Compilar todo el proyecto
./gradlew assembleDebug
```

---

## 📝 Buenas Prácticas Aplicadas

1. **Arquitectura Declarativa:** Interfaz reactiva en función del estado local (`UI = f(state)`).
2. **Defensive Programming:** Conversiones numéricas seguras mediante `toDoubleOrNull()` y `toIntOrNull()`.
3. **Inmutabilidad y Null-Safety:** Uso predominante de `val`, operadores elvis (`?:`) y descarte del operador de aserción forzada (`!!`).
4. **Validaciones en Capa de Dominio:** Bloques `init` con llamadas a `require(...)` para proteger la integridad de las entidades.
5. **Separación de Responsabilidades:** Lógica de validación desacoplada y lista para su traslado a un `ViewModel`.

---

## 👨‍💻 Autor

**Andrey Mestanza**  
Estudiante de Ingeniería de Sistemas — Universidad Peruana Unión (UPeU)  
GitHub: [@17Yerdna](https://github.com/17Yerdna)

---

## 📄 Licencia

Proyecto académico desarrollado para el curso de **Desarrollo de Aplicaciones Móviles**. Todos los derechos reservados.
