package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.presentation.theme.AppIcons

/**
 * Representa el resultado de evaluar el formulario de registro de productos.
 */
data class ResultadoValidacion(
    val producto: Producto? = null,
    val mensaje: String,
    val esExitoso: Boolean
)

/**
 * Pasos 5, 6 y 7: Conversión segura (*OrNull) y validación secuencial con when.
 */
fun validarRegistroProducto(
    nombre: String,
    precio: String,
    stock: String,
    activo: Boolean = true
): ResultadoValidacion {
    val precioParsed = precio.toDoubleOrNull()
    val stockParsed = stock.toIntOrNull()

    return when {
        nombre.isBlank() -> ResultadoValidacion(
            mensaje = "El nombre es obligatorio.",
            esExitoso = false
        )
        precioParsed == null -> ResultadoValidacion(
            mensaje = "Ingrese un precio numérico.",
            esExitoso = false
        )
        precioParsed <= 0.0 -> ResultadoValidacion(
            mensaje = "El precio debe ser mayor que cero.",
            esExitoso = false
        )
        stockParsed == null -> ResultadoValidacion(
            mensaje = "Ingrese un stock entero.",
            esExitoso = false
        )
        stockParsed < 0 -> ResultadoValidacion(
            mensaje = "El stock no puede ser negativo.",
            esExitoso = false
        )
        else -> {
            val nuevoProducto = Producto(
                id = 0L,
                nombre = nombre.trim(),
                precio = precioParsed,
                stock = stockParsed,
                activo = activo
            )
            ResultadoValidacion(
                producto = nuevoProducto,
                mensaje = "¡Registro exitoso! Producto: ${nuevoProducto.nombre} creado correctamente.",
                esExitoso = true
            )
        }
    }
}

/**
 * Pantalla de Productos con pestañas (Tabs) para clasificar el inventario:
 * - Tab 0: Activos
 * - Tab 1: Inactivos
 * - Tab 2: Bajo Stock (stock <= 5)
 *
 * Incluye formulario de registro de la Sesión 3 y datos de prueba de la Guía Autónoma Sesión 4.
 */
@Composable
fun ProductoScreen(
    modifier: Modifier = Modifier
) {
    // Paso 3: Estado de la pestaña seleccionada
    // 0: Activos, 1: Inactivos, 2: Bajo Stock
    var tabSeleccionada by remember { mutableStateOf(0) }

    // Paso 4: Mock Data inicial solicitado en la Guía Autónoma
    val productos = remember {
        mutableStateListOf(
            Producto(id = 1L, nombre = "Paracetamol", precio = 15.50, stock = 100, activo = true),
            Producto(id = 2L, nombre = "Ibuprofeno", precio = 18.90, stock = 50, activo = true),
            Producto(id = 3L, nombre = "Amoxicilina", precio = 25.00, stock = 5, activo = true),
            Producto(id = 4L, nombre = "Loratadina", precio = 12.50, stock = 0, activo = false),
            Producto(id = 5L, nombre = "Diclofenaco", precio = 20.00, stock = 3, activo = true)
        )
    }

    // Estados para el formulario de registro (Sesión 3)
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var esActivoRegistro by remember { mutableStateOf(true) }
    var mensaje by remember { mutableStateOf("") }
    var intentoRegistrar by remember { mutableStateOf(false) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    val precioDouble = precio.toDoubleOrNull()
    val stockInt = stock.toIntOrNull()

    val isNombreError = intentoRegistrar && nombre.isBlank()
    val isPrecioError = intentoRegistrar && (precioDouble == null || precioDouble <= 0.0)
    val isStockError = intentoRegistrar && (stockInt == null || stockInt < 0)

    // Pasos 5 y 6: Lógica estricta de filtrado según pestaña
    // Tab 0: Activos (activo == true)
    // Tab 1: Inactivos (activo == false)
    // Tab 2: Bajo stock (stock <= 5, documentando stock == 0)
    val productosFiltrados = remember(tabSeleccionada, productos.size, productos.toList()) {
        when (tabSeleccionada) {
            0 -> productos.filter { it.activo }
            1 -> productos.filter { !it.activo }
            2 -> productos.filter { it.stock <= 5 }
            else -> productos
        }
    }

    val tabs = listOf(
        "Activos" to productos.count { it.activo },
        "Inactivos" to productos.count { !it.activo },
        "Bajo Stock" to productos.count { it.stock <= 5 }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Encabezado de la pantalla con botón para alternar formulario
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Inventario de Productos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Gestión y clasificación por estado de stock",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FilledTonalButton(
                onClick = { mostrarFormulario = !mostrarFormulario },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (mostrarFormulario) "Ver Lista" else "+ Nuevo",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Paso 3: Barra de Pestañas (PrimaryTabRow / TabRow)
        PrimaryTabRow(
            selectedTabIndex = tabSeleccionada,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, (titulo, cantidad) ->
                Tab(
                    selected = tabSeleccionada == index,
                    onClick = {
                        tabSeleccionada = index
                        mostrarFormulario = false
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = titulo,
                                fontWeight = if (tabSeleccionada == index) FontWeight.Bold else FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (tabSeleccionada == index) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = "$cantidad",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tabSeleccionada == index) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (mostrarFormulario) {
            // Formulario de Registro de Producto (Sesión 3)
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Registrar Nuevo Producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del Producto") },
                        placeholder = { Text("Ej. Paracetamol 500 mg") },
                        isError = isNombreError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = precio,
                            onValueChange = { precio = it },
                            label = { Text("Precio (S/.)") },
                            placeholder = { Text("15.50") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = isPrecioError,
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = stock,
                            onValueChange = { stock = it },
                            label = { Text("Stock") },
                            placeholder = { Text("100") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = isStockError,
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (esActivoRegistro) "Estado: Activo" else "Estado: Inactivo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = esActivoRegistro,
                            onCheckedChange = { esActivoRegistro = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            intentoRegistrar = true
                            val resultado = validarRegistroProducto(
                                nombre = nombre,
                                precio = precio,
                                stock = stock,
                                activo = esActivoRegistro
                            )
                            mensaje = resultado.mensaje

                            if (resultado.esExitoso && resultado.producto != null) {
                                val nuevoId = (productos.maxOfOrNull { it.id } ?: 0L) + 1L
                                productos.add(0, resultado.producto.copy(id = nuevoId))
                                nombre = ""
                                precio = ""
                                stock = ""
                                intentoRegistrar = false
                                mostrarFormulario = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Registrar Producto", fontWeight = FontWeight.Bold)
                    }

                    if (mensaje.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = mensaje,
                            color = if (mensaje.startsWith("¡Registro")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        } else {
            // Lista de productos filtrados según la pestaña activa
            if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay productos en esta categoría.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(productosFiltrados, key = { it.id }) { producto ->
                        ProductoItemCard(producto = producto)
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta que representa un producto en la lista, destacando estado y bajo stock.
 */
@Composable
fun ProductoItemCard(
    producto: Producto,
    modifier: Modifier = Modifier
) {
    val esBajoStock = producto.stock <= 5

    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (esBajoStock && producto.activo) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!producto.activo) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Inactivo",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Precio: S/. ${((producto.precio * 100).toLong() / 100.0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = when {
                        !producto.activo -> MaterialTheme.colorScheme.surfaceVariant
                        producto.stock == 0 -> MaterialTheme.colorScheme.error
                        producto.stock <= 5 -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        if (esBajoStock && producto.activo) {
                            Icon(
                                imageVector = AppIcons.Warning,
                                contentDescription = "Alerta bajo stock",
                                tint = if (producto.stock == 0) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (producto.stock == 0) "Agotado (0 u.)" else "Stock: ${producto.stock} u.",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                !producto.activo -> MaterialTheme.colorScheme.onSurfaceVariant
                                producto.stock == 0 -> MaterialTheme.colorScheme.onError
                                producto.stock <= 5 -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }
                }
                if (esBajoStock && producto.activo) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Bajo stock (<= 5)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
