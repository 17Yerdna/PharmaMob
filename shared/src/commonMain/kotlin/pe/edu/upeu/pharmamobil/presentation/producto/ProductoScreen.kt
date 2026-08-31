package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.pharmamobil.domain.model.Producto

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
 * Evaluación estricta en orden:
 * 1. Nombre obligatorio (isNotBlank)
 * 2. Conversión numérica de precio (toDoubleOrNull)
 * 3. Rango de precio (> 0.0)
 * 4. Conversión de stock a entero (toIntOrNull)
 * 5. Rango de stock no negativo (>= 0)
 * 6. Bloque else: Creación segura de la instancia de Producto
 */
fun validarRegistroProducto(nombre: String, precio: String, stock: String): ResultadoValidacion {
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
                id = 1L,
                nombre = nombre.trim(),
                precio = precioParsed,
                stock = stockParsed
            )
            ResultadoValidacion(
                producto = nuevoProducto,
                mensaje = "¡Registro exitoso! Producto: ${nuevoProducto.nombre} creado correctamente.",
                esExitoso = true
            )
        }
    }
}

@Composable
fun ProductoScreen(
    modifier: Modifier = Modifier
) {
    // Pasos 1 y 2: Estados observables para los campos de texto
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // Pasos 3 y 4: Estados de control y retroalimentación
    var mensaje by remember { mutableStateOf("") }
    var intentoRegistrar by remember { mutableStateOf(false) }

    // Variables de apoyo para evaluación de errores visuales en UI
    val precioDouble = precio.toDoubleOrNull()
    val stockInt = stock.toIntOrNull()

    // Pasos 8 y 9: Propiedad isError en UI únicamente tras pulsar Registrar
    val isNombreError = intentoRegistrar && nombre.isBlank()
    val isPrecioError = intentoRegistrar && (precioDouble == null || precioDouble <= 0.0)
    val isStockError = intentoRegistrar && (stockInt == null || stockInt < 0)

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Registro de Producto",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "PharmaMobil - Gestión de Farmacia",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo 1: Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del Producto") },
            placeholder = { Text("Ej. Paracetamol 500 mg") },
            isError = isNombreError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo 2: Precio
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio (S/.)") },
            placeholder = { Text("Ej. 8.50") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = isPrecioError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo 3: Stock
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock Disponible") },
            placeholder = { Text("Ej. 100") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isStockError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Registro
        Button(
            onClick = {
                intentoRegistrar = true
                val resultado = validarRegistroProducto(nombre, precio, stock)
                mensaje = resultado.mensaje

                if (resultado.esExitoso) {
                    // Pasos 8 y 9: Limpieza automática del formulario al tener éxito
                    nombre = ""
                    precio = ""
                    stock = ""
                    intentoRegistrar = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Registrar",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Panel de Retroalimentación / Mensaje
        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            val isSuccess = !intentoRegistrar && mensaje.startsWith("¡Registro exitoso!")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSuccess) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mensaje,
                    color = if (isSuccess) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
