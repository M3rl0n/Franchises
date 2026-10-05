package com.tp.franchises.api.utils;

public final class ApiMessages {

	// Éxito
	public static final String CREATED = "Creado exitosamente";
	public static final String UPDATED = "Actualizado exitosamente";
	public static final String FOUND = "Consulta exitosa";
	public static final String DELETED = "Eliminado exitosamente";

	// Validación de campos
	public static final String NAME_REQUIRED = "El nombre es obligatorio";
	public static final String NAME_TOO_LONG = "El nombre no puede superar los 100 caracteres";
	public static final String STOCK_REQUIRED = "El stock es obligatorio";
	public static final String STOCK_NEGATIVE = "El stock no puede ser negativo";

	// Errores generales
	public static final String VALIDATION_FAILED = "La solicitud tiene campos inválidos";
	public static final String BAD_REQUEST = "La solicitud no es válida";
	public static final String RESOURCE_NOT_FOUND = "El recurso solicitado no existe";
	public static final String METHOD_NOT_ALLOWED = "Método HTTP no permitido";
	public static final String UNSUPPORTED_MEDIA_TYPE = "Tipo de contenido no soportado";
	public static final String DATA_CONFLICT = "La solicitud entra en conflicto con datos existentes";
	public static final String REQUEST_ERROR = "Error al procesar la solicitud";
	public static final String UNEXPECTED_ERROR = "Ocurrió un error inesperado";

	// Franquicias
	public static final String FRANCHISE_NOT_FOUND = "No se encontró la franquicia con id %d";
	public static final String FRANCHISE_ALREADY_EXISTS = "La franquicia '%s' ya existe";

	// Sucursales
	public static final String BRANCH_NOT_FOUND = "No se encontró la sucursal con id %d";
	public static final String BRANCH_ALREADY_EXISTS = "La sucursal '%s' ya existe en la franquicia";

	// Productos
	public static final String PRODUCT_NOT_FOUND = "No se encontró el producto con id %d";
	public static final String PRODUCT_ALREADY_EXISTS = "El producto '%s' ya existe en la sucursal";

	private ApiMessages() {
	}

}
