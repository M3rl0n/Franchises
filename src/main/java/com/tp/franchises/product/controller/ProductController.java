package com.tp.franchises.product.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tp.franchises.api.dtos.CreateProductRequest;
import com.tp.franchises.api.dtos.NameRequest;
import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.dtos.StockUpdateRequest;
import com.tp.franchises.api.dtos.TopStockProductResponse;
import com.tp.franchises.api.response.ApiResponse;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.product.services.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Productos")
public class ProductController {

	private final ProductService productService;

	@PostMapping("/branches/{branchId}/products")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Agregar un producto a una sucursal",
			description = "La URL del producto creado viene en el header `Location`. "
					+ "Errores: 400 si el nombre está vacío o supera 100 caracteres, o si el stock falta o es negativo; "
					+ "404 si la sucursal no existe; 409 si la sucursal ya tiene un producto con ese nombre.")
	public ResponseEntity<ApiResponse<Void>> create(@PathVariable Long branchId,
			@Valid @RequestBody CreateProductRequest request) {
		ProductResponse created = productService.create(branchId, request.name(), request.stock());
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path("/products/{id}")
			.buildAndExpand(created.id())
			.toUri();
		return ApiResponse.created(location, ApiMessages.CREATED);
	}

	@GetMapping("/products/{id}")
	@Operation(summary = "Consultar un producto",
			description = "Errores: 404 si el producto no existe.")
	public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
		return ApiResponse.ok(ApiMessages.FOUND, productService.findById(id));
	}

	@PatchMapping("/products/{id}")
	@Operation(summary = "Actualizar el nombre de un producto",
			description = "Errores: 400 si el nombre está vacío o supera 100 caracteres; 404 si el producto no existe; "
					+ "409 si otro producto de la misma sucursal ya usa ese nombre.")
	public ResponseEntity<ApiResponse<Void>> updateProduct(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
		productService.updateProduct(id, request.name());
		return ApiResponse.ok(ApiMessages.UPDATED);
	}

	@PatchMapping("/products/{id}/stock")
	@Operation(summary = "Modificar el stock de un producto",
			description = "Errores: 400 si el stock falta o es negativo; 404 si el producto no existe.")
	public ResponseEntity<ApiResponse<Void>> updateStock(@PathVariable Long id,
			@Valid @RequestBody StockUpdateRequest request) {
		productService.updateStock(id, request.stock());
		return ApiResponse.ok(ApiMessages.UPDATED);
	}

	@DeleteMapping("/products/{id}")
	@Operation(summary = "Eliminar un producto de su sucursal",
			description = "Errores: 404 si el producto no existe.")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		productService.delete(id);
		return ApiResponse.ok(ApiMessages.DELETED);
	}

	@GetMapping("/franchises/{franchiseId}/products/top-stock")
	@Operation(summary = "Producto con más stock por sucursal de una franquicia",
			description = "Devuelve, por cada sucursal de la franquicia, el producto con más stock e indica a qué sucursal pertenece. "
					+ "Si hay empate se devuelven todos los empatados; las sucursales sin productos no aparecen. "
					+ "Errores: 404 si la franquicia no existe.")
	public ResponseEntity<ApiResponse<List<TopStockProductResponse>>> findTopStockByFranchise(
			@PathVariable Long franchiseId) {
		return ApiResponse.ok(ApiMessages.FOUND, productService.findTopStockByFranchise(franchiseId));
	}

}
