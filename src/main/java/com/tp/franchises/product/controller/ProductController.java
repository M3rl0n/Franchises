package com.tp.franchises.product.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping("/branches/{branchId}/products")
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
	public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
		return ApiResponse.ok(ApiMessages.FOUND, productService.findById(id));
	}

	@PatchMapping("/products/{id}")
	public ResponseEntity<ApiResponse<Void>> updateProduct(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
		productService.updateProduct(id, request.name());
		return ApiResponse.ok(ApiMessages.UPDATED);
	}

	@PatchMapping("/products/{id}/stock")
	public ResponseEntity<ApiResponse<Void>> updateStock(@PathVariable Long id,
			@Valid @RequestBody StockUpdateRequest request) {
		productService.updateStock(id, request.stock());
		return ApiResponse.ok(ApiMessages.UPDATED);
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		productService.delete(id);
		return ApiResponse.ok(ApiMessages.DELETED);
	}

	@GetMapping("/franchises/{franchiseId}/products/top-stock")
	public ResponseEntity<ApiResponse<List<TopStockProductResponse>>> findTopStockByFranchise(
			@PathVariable Long franchiseId) {
		return ApiResponse.ok(ApiMessages.FOUND, productService.findTopStockByFranchise(franchiseId));
	}

}
