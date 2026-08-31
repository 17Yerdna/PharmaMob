package pe.edu.upeu.pharmamobil.demo

import pe.edu.upeu.pharmamobil.domain.result.ResultadoProductos

fun mostrarResultado(resultado: ResultadoProductos){
    when(resultado){
        ResultadoProductos.cargando ->{
            println(
                "Cargando Producto"
            )
        }
        is ResultadoProductos.Exito ->{
            println(
                "Productos Encontrados: ${resultado.productos.size}"
            )
        }
        is ResultadoProductos.Error -> {
            println(
                "Error: ${resultado.mensaje}"
            )
        }
    }
}