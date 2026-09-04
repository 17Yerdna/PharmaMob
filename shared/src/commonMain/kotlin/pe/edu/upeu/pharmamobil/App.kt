package pe.edu.upeu.pharmamobil

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
import pe.edu.upeu.pharmamobil.navigation.Screen
import pe.edu.upeu.pharmamobil.navigation.tituloPantalla
import pe.edu.upeu.pharmamobil.presentation.cliente.ClienteScreen
import pe.edu.upeu.pharmamobil.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobil.presentation.pedido.PedidoScreen
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoScreen
import pe.edu.upeu.pharmamobil.presentation.theme.AppIcons
import pe.edu.upeu.pharmamobil.presentation.theme.PharmaMobilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun App() {
    // Estado del tema (Light / Dark)
    var darkTheme by remember { mutableStateOf(false) }

    // Paso 4: Estado de navegación y destino actual
    var pantallaActual by remember { mutableStateOf<Screen>(Screen.Inicio) }

    // Estado del Navigation Drawer y CoroutineScope para animar su apertura/cierre
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    PharmaMobilTheme(darkTheme = darkTheme) {
        // Paso 6: Estructura del ModalNavigationDrawer envolviendo al Scaffold
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp)
                ) {
                    // Encabezado institucional del Drawer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = AppIcons.Home,
                                        contentDescription = "Logo",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
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
                        icon = {
                            Icon(
                                imageVector = AppIcons.Home,
                                contentDescription = "Inicio"
                            )
                        },
                        label = { Text("Inicio") },
                        selected = pantallaActual is Screen.Inicio,
                        onClick = {
                            pantallaActual = Screen.Inicio
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    // Destino 2: Productos (Reutilización Sesión 3)
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = AppIcons.ShoppingCart,
                                contentDescription = "Productos"
                            )
                        },
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
                        icon = {
                            Icon(
                                imageVector = AppIcons.Person,
                                contentDescription = "Clientes"
                            )
                        },
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
                        icon = {
                            Icon(
                                imageVector = AppIcons.List,
                                contentDescription = "Pedidos"
                            )
                        },
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

                    // Control de Modo Oscuro en el menú lateral
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
            // Paso 5: Estructura general con Scaffold y TopAppBar
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
                            // Paso 9: Control dinámico de modo oscuro desde la TopAppBar
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
                // Área de contenido dinámico según pantallaActual
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (pantallaActual) {
                        is Screen.Inicio -> InicioScreen(
                            onNavigateToProductos = { pantallaActual = Screen.Productos },
                            onNavigateToClientes = { pantallaActual = Screen.Clientes },
                            onNavigateToPedidos = { pantallaActual = Screen.Pedidos }
                        )
                        // Paso 7: Reutilización limpia y estricta de ProductoScreen
                        is Screen.Productos -> ProductoScreen()
                        is Screen.Clientes -> ClienteScreen()
                        is Screen.Pedidos -> PedidoScreen()
                    }
                }
            }
        }
    }
}