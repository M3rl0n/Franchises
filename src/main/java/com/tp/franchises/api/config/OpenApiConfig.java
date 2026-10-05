package com.tp.franchises.api.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "Franchises API",
				version = "1.0.0",
				description = """
						API para gestionar franquicias, sucursales y productos con su stock.

						Todas las respuestas usan el mismo formato:
						- `status`: código HTTP de la respuesta.
						- `message`: resultado de la operación.
						- `data`: datos devueltos; solo en las consultas.
						- `errors`: errores por campo; solo en los errores de validación (400).

						Crear y actualizar no devuelven `data`. Al crear, la URL del nuevo recurso viene en el header `Location`.
						"""),
		tags = {
				@Tag(name = "Franquicias", description = "Crear, consultar y renombrar franquicias"),
				@Tag(name = "Sucursales", description = "Agregar sucursales a una franquicia, consultarlas y renombrarlas"),
				@Tag(name = "Productos", description = "Gestionar los productos de cada sucursal y su stock") })
public class OpenApiConfig {
}
