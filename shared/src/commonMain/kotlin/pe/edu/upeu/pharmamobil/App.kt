package pe.edu.upeu.pharmamobil

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import pharmamobil.shared.generated.resources.Res
import pharmamobil.shared.generated.resources.pharmamobil_logo
import pe.edu.upeu.pharmamobil.navigation.Screen
import pe.edu.upeu.pharmamobil.navigation.tituloPantalla
import pe.edu.upeu.pharmamobil.presentation.cliente.ClienteScreen
import pe.edu.upeu.pharmamobil.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobil.presentation.pedido.PedidoScreen
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoScreen
import pe.edu.upeu.pharmamobil.presentation.theme.AppIcons
import pe.edu.upeu.pharmamobil.presentation.theme.PharmaMobilTheme

/**
 * Contenedor principal de PharmaMobil con soporte de Navegación Adaptativa:
 * - Teléfono (< 600dp): ModalNavigationDrawer + TopAppBar con icono hamburguesa
 * - Tablet (>= 600dp): NavigationRail lateral + Scaffold central
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun App() {
    // Estado del tema (Light / Dark)
    var darkTheme by remember { mutableStateOf(false) }

    // Estado de navegación y destino actual
    var pantallaActual by remember { mutableStateOf<Screen>(Screen.Inicio) }

    // Estado del Navigation Drawer y CoroutineScope para animar su apertura/cierre
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    PharmaMobilTheme(darkTheme = darkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // Pasos 14 y 15: Evaluación de Breakpoint Adaptativo (600dp)
            val esPantallaMedianaOAmplia = maxWidth >= 600.dp

            if (esPantallaMedianaOAmplia) {
                // Modo Adaptativo Tablet / Pantalla Amplia con NavigationRail
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail(
                        modifier = Modifier.width(80.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        header = {
                            Image(
                                painter = painterResource(Res.drawable.pharmamobil_logo),
                                contentDescription = "Logo PharmaMobil",
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .size(36.dp)
                            )
                        }
                    ) {
                        NavigationRailItem(
                            selected = pantallaActual is Screen.Inicio,
                            onClick = { pantallaActual = Screen.Inicio },
                            icon = { Icon(AppIcons.Home, contentDescription = "Inicio") },
                            label = { Text("Inicio", fontSize = 11.sp) }
                        )
                        NavigationRailItem(
                            selected = pantallaActual is Screen.Productos,
                            onClick = { pantallaActual = Screen.Productos },
                            icon = { Icon(AppIcons.ShoppingCart, contentDescription = "Productos") },
                            label = { Text("Productos", fontSize = 11.sp) }
                        )
                        NavigationRailItem(
                            selected = pantallaActual is Screen.Clientes,
                            onClick = { pantallaActual = Screen.Clientes },
                            icon = { Icon(AppIcons.Person, contentDescription = "Clientes") },
                            label = { Text("Clientes", fontSize = 11.sp) }
                        )
                        NavigationRailItem(
                            selected = pantallaActual is Screen.Pedidos,
                            onClick = { pantallaActual = Screen.Pedidos },
                            icon = { Icon(AppIcons.List, contentDescription = "Pedidos") },
                            label = { Text("Pedidos", fontSize = 11.sp) }
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = { darkTheme = !darkTheme },
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (darkTheme) AppIcons.DarkMode else AppIcons.LightMode,
                                contentDescription = "Cambiar tema",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    VerticalDivider()

                    // Scaffold central para Tablet
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = tituloPantalla(pantallaActual),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp
                                    )
                                },
                                actions = {
                                    IconButton(onClick = { darkTheme = !darkTheme }) {
                                        Icon(
                                            imageVector = if (darkTheme) AppIcons.DarkMode else AppIcons.LightMode,
                                            contentDescription = "Alternar Modo Claro/Oscuro"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    ) { paddingValues ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ContenidoDestino(
                                pantallaActual = pantallaActual,
                                onNavigate = { pantallaActual = it }
                            )
                        }
                    }
                }
            } else {
                // Modo Teléfono estándar (< 600dp) con ModalNavigationDrawer
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(300.dp)
                        ) {
                            // Encabezado institucional del Drawer con recurso compartido (Logo)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.pharmamobil_logo),
                                        contentDescription = "Logo PharmaMobil",
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "PharmaMobil",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Gestión de Farmacias",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Destino 1: Inicio
                            NavigationDrawerItem(
                                icon = { Icon(AppIcons.Home, contentDescription = "Inicio") },
                                label = { Text("Inicio") },
                                selected = pantallaActual is Screen.Inicio,
                                onClick = {
                                    pantallaActual = Screen.Inicio
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            // Destino 2: Productos con Tabs
                            NavigationDrawerItem(
                                icon = { Icon(AppIcons.ShoppingCart, contentDescription = "Productos") },
                                label = { Text("Productos") },
                                selected = pantallaActual is Screen.Productos,
                                onClick = {
                                    pantallaActual = Screen.Productos
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            // Destino 3: Clientes
                            NavigationDrawerItem(
                                icon = { Icon(AppIcons.Person, contentDescription = "Clientes") },
                                label = { Text("Clientes") },
                                selected = pantallaActual is Screen.Clientes,
                                onClick = {
                                    pantallaActual = Screen.Clientes
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            // Destino 4: Pedidos
                            NavigationDrawerItem(
                                icon = { Icon(AppIcons.List, contentDescription = "Pedidos") },
                                label = { Text("Pedidos") },
                                selected = pantallaActual is Screen.Pedidos,
                                onClick = {
                                    pantallaActual = Screen.Pedidos
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            Spacer(modifier = Modifier.weight(1f))
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                            // Control de Modo Oscuro en el Drawer
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (darkTheme) AppIcons.DarkMode else AppIcons.LightMode,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (darkTheme) "Modo Oscuro" else "Modo Claro",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Switch(
                                    checked = darkTheme,
                                    onCheckedChange = { darkTheme = it }
                                )
                            }
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = tituloPantalla(pantallaActual),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 19.sp
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            scope.launch { drawerState.open() }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = AppIcons.Menu,
                                            contentDescription = "Abrir Menú de Navegación"
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { darkTheme = !darkTheme }
                                    ) {
                                        Icon(
                                            imageVector = if (darkTheme) AppIcons.DarkMode else AppIcons.LightMode,
                                            contentDescription = "Alternar Modo Claro/Oscuro"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    ) { paddingValues ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ContenidoDestino(
                                pantallaActual = pantallaActual,
                                onNavigate = { pantallaActual = it }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renderizado desacoplado del contenido dinámico de la aplicación.
 */
@Composable
private fun ContenidoDestino(
    pantallaActual: Screen,
    onNavigate: (Screen) -> Unit
) {
    when (pantallaActual) {
        is Screen.Inicio -> InicioScreen(
            onNavigateToProductos = { onNavigate(Screen.Productos) },
            onNavigateToClientes = { onNavigate(Screen.Clientes) },
            onNavigateToPedidos = { onNavigate(Screen.Pedidos) }
        )
        is Screen.Productos -> ProductoScreen()
        is Screen.Clientes -> ClienteScreen()
        is Screen.Pedidos -> PedidoScreen()
    }
}