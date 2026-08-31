# PharmaMobil 📱💊

Aplicativa móvil multiplataforma para gestión de farmacias, desarrollada con **Kotlin Multiplatform (KMP)**.

## 🎓 Contexto Acad émico

- **Universidad:** Universidad Peruana de Ciencias Aplicadas (UPeU)
- **Ciclo:** VIII Ciclo - 2026-2
- **Asignatura:** Desarrollo de Aplicaciones Móviles
- **Estudiante:** Fabian Rodriguez

## 🏗️ Arquitectura del Proyecto

```
PharmaMobil/
├── shared/
│   └── src/commonMain/kotlin/pe/edu/upeu/pharmamobil/
│       ├── domain/
│       │   ├── model/
│       │   └── usecase/
│       └── data/
│           └── repository/
├── androidApp/
└── iosApp/
```

## 📦 Entidades de Dominio

### Cliente, Producto, Pedido, DetallePedido, EstadoPedido

- **Null-safety:** Tipos `String?` y operador Elvis `?:`
- **Inmutabilidad:** Propiedades con `val`
- **Data classes:** `equals`, `hashCode`, `toString` autom áticos
- **Sealed class:** Estados `Pendiente`, `Procesando`, `Entregado`, `Rechazado`

## 🔧 Consultas Funcionales

- `productosDisponibles()` - `filter`
- `nombresDeProductos()` - `map`
- `buscarProductoPorId()` - `find`
- `valorTotalInventario()` - `sumOf`

## ⚡ Asincron ía

- **Corrutinas:** Funciones `suspend` con `delay`
- **Flow:** Emisi ón de estados `Cargando`, `Exitoso`, `Error`

## 🛠️ Stack

- Kotlin Multiplatform
- Corrutinas + Flow
- Clean Architecture

## 👨‍💻 Autor

Fabian Rodriguez - UPeU 2026-2
