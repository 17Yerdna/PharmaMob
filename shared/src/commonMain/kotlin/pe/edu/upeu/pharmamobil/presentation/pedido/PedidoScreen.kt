package pe.edu.upeu.pharmamobil.presentation.pedido

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
import pe.edu.upeu.pharmamobil.domain.model.*
import pe.edu.upeu.pharmamobil.presentation.theme.AppIcons

@Composable
fun PedidoScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Datos de demostración basados en las clases Pedido, Cliente, DetallePedido y EstadoPedido
    val listaPedidos = remember {
        val prod1 = Producto(1L, "Paracetamol 500 mg", 8.50, 100)
        val prod2 = Producto(2L, "Amoxicilina 500 mg", 18.50, 45)
        val prod3 = Producto(3L, "Loratadina 10 mg", 10.00, 30)

        val cliente1 = Cliente(1L, "Botica San Jerónimo", "contacto@sanjeronimo.pe", "987654321")
        val cliente2 = Cliente(2L, "Farmacia Central UPeU", "farmacia@upeu.edu.pe", "951234567")
        val cliente3 = Cliente(3L, "Policlínico Los Ángeles", "compras@losangeles.com", "912345678")

        listOf(
            Pedido(
                id = 101L,
                cliente = cliente1,
                detalles = listOf(DetallePedido(prod1, 10), DetallePedido(prod2, 5)),
                estado = EstadoPedido.Entregado
            ),
            Pedido(
                id = 102L,
                cliente = cliente2,
                detalles = listOf(DetallePedido(prod2, 2), DetallePedido(prod3, 4)),
                estado = EstadoPedido.Procesando
            ),
            Pedido(
                id = 103L,
                cliente = cliente3,
                detalles = listOf(DetallePedido(prod1, 20)),
                estado = EstadoPedido.Pendiente
            ),
            Pedido(
                id = 104L,
                cliente = cliente1,
                detalles = listOf(DetallePedido(prod3, 15)),
                estado = EstadoPedido.Rechazado("Falta de comprobante de pago")
            )
        )
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
                imageVector = AppIcons.List,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Control de Pedidos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Órdenes de compra y estado de despachos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta informativa de Sesión 4
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Módulo demostrativo de navegación. El flujo completo de facturación y despacho se implementará en sesiones posteriores.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de pedidos
        listaPedidos.forEach { pedido ->
            PedidoItemCard(pedido = pedido)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PedidoItemCard(
    pedido: Pedido,
    modifier: Modifier = Modifier
) {
    val total = remember(pedido) {
        pedido.detalles.sumOf { it.producto.precio * it.cantidad }
    }
    val totalFormateado = remember(total) {
        val centavos = ((total * 100).toLong() % 100).let { if (it < 10) "0$it" else "$it" }
        "${total.toLong()}.$centavos"
    }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orden #${pedido.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                EstadoChip(estado = pedido.estado)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cliente
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = pedido.cliente.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Resumen de productos y total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${pedido.detalles.size} producto(s) incluidos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Total: S/. $totalFormateado",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (pedido.estado is EstadoPedido.Rechazado) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Motivo: ${pedido.estado.motivo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EstadoChip(
    estado: EstadoPedido
) {
    val (texto, colorFondo, colorTexto) = when (estado) {
        is EstadoPedido.Pendiente -> Triple(
            "Pendiente",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        is EstadoPedido.Procesando -> Triple(
            "Procesando",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        is EstadoPedido.Entregado -> Triple(
            "Entregado",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        is EstadoPedido.Rechazado -> Triple(
            "Rechazado",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }

    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
