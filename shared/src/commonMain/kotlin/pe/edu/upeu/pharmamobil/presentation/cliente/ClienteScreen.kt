package pe.edu.upeu.pharmamobil.presentation.cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.pharmamobil.domain.model.Cliente
import pe.edu.upeu.pharmamobil.presentation.theme.AppIcons

@Composable
fun ClienteScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var busqueda by remember { mutableStateOf("") }

    // Clientes de demostración basados en el modelo de dominio Cliente
    val listaClientes = remember {
        listOf(
            Cliente(1L, "Botica San Jerónimo", "contacto@sanjeronimo.pe", "987654321"),
            Cliente(2L, "Farmacia Central UPeU", "farmacia@upeu.edu.pe", "951234567"),
            Cliente(3L, "Policlínico Los Ángeles", "compras@losangeles.com", "912345678"),
            Cliente(4L, "Droguería Santa María", "ventas@santamaria.pe", null)
        )
    }

    val clientesFiltrados = remember(busqueda) {
        if (busqueda.isBlank()) listaClientes
        else listaClientes.filter { it.nombre.contains(busqueda, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = AppIcons.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Directorio de Clientes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Farmacias, clínicas e instituciones registradas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de búsqueda con icono
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            leadingIcon = {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = "Buscar"
                )
            },
            placeholder = { Text("Buscar cliente por nombre...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta Informativa de Sesión 4
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Módulo demostrativo de navegación. El CRUD completo de clientes se implementará en sesiones posteriores.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tarjetas de clientes
        clientesFiltrados.forEach { cliente ->
            ClienteItemCard(cliente = cliente)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ClienteItemCard(
    cliente: Cliente,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = cliente.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                SuggestionChip(
                    onClick = {},
                    label = { Text("ID #${cliente.id}", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Correo
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cliente.correo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Teléfono con método seguro obtenerTelefono()
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cliente.obtenerTelefono(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (cliente.telefono != null) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
