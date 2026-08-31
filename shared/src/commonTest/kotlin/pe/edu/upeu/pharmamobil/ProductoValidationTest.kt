package pe.edu.upeu.pharmamobil

import pe.edu.upeu.pharmamobil.presentation.producto.validarRegistroProducto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProductoValidationTest {

    @Test
    fun caso1_registroValido_exito() {
        val resultado = validarRegistroProducto(
            nombre = "Paracetamol 500 mg",
            precio = "8.50",
            stock = "100"
        )
        assertTrue(resultado.esExitoso, "El registro debería ser exitoso")
        assertNotNull(resultado.producto)
        assertEquals("Paracetamol 500 mg", resultado.producto.nombre)
        assertEquals(8.50, resultado.producto.precio)
        assertEquals(100, resultado.producto.stock)
        assertTrue(resultado.mensaje.startsWith("¡Registro exitoso!"))
    }

    @Test
    fun caso2_nombreVacio_errorObligatorio() {
        val resultado = validarRegistroProducto(
            nombre = "",
            precio = "8.50",
            stock = "100"
        )
        assertFalse(resultado.esExitoso)
        assertEquals("El nombre es obligatorio.", resultado.mensaje)
    }

    @Test
    fun caso2_nombreEspacios_errorObligatorio() {
        val resultado = validarRegistroProducto(
            nombre = "   ",
            precio = "8.50",
            stock = "100"
        )
        assertFalse(resultado.esExitoso)
        assertEquals("El nombre es obligatorio.", resultado.mensaje)
    }

    @Test
    fun caso3_precioNoNumerico_errorFormato() {
        val resultado = validarRegistroProducto(
            nombre = "Ibuprofeno",
            precio = "abc",
            stock = "50"
        )
        assertFalse(resultado.esExitoso)
        assertEquals("Ingrese un precio numérico.", resultado.mensaje)
    }

    @Test
    fun caso4_precioCero_errorMayorQueCero() {
        val resultado = validarRegistroProducto(
            nombre = "Ibuprofeno",
            precio = "0",
            stock = "50"
        )
        assertFalse(resultado.esExitoso)
        assertEquals("El precio debe ser mayor que cero.", resultado.mensaje)
    }

    @Test
    fun caso5_stockNoEntero_errorFormato() {
        val resultado = validarRegistroProducto(
            nombre = "Amoxicilina",
            precio = "18.50",
            stock = "abc"
        )
        assertFalse(resultado.esExitoso)
        assertEquals("Ingrese un stock entero.", resultado.mensaje)
    }

    @Test
    fun caso6_stockNegativo_errorNoNegativo() {
        val resultado = validarRegistroProducto(
            nombre = "Amoxicilina",
            precio = "18.50",
            stock = "-5"
        )
        assertFalse(resultado.esExitoso)
        assertEquals("El stock no puede ser negativo.", resultado.mensaje)
    }

    @Test
    fun caso7_stockCero_registroValido() {
        val resultado = validarRegistroProducto(
            nombre = "Loratadina",
            precio = "10",
            stock = "0"
        )
        assertTrue(resultado.esExitoso, "El registro con stock 0 debe ser válido")
        assertNotNull(resultado.producto)
        assertEquals("Loratadina", resultado.producto.nombre)
        assertEquals(10.0, resultado.producto.precio)
        assertEquals(0, resultado.producto.stock)
        assertTrue(resultado.mensaje.startsWith("¡Registro exitoso!"))
    }
}
