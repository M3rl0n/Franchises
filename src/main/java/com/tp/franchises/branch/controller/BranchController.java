package com.tp.franchises.branch.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tp.franchises.api.dtos.BranchResponse;
import com.tp.franchises.api.dtos.NameRequest;
import com.tp.franchises.api.response.ApiResponse;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.branch.services.BranchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Sucursales")
public class BranchController {

	private final BranchService branchService;

	@PostMapping("/franchises/{franchiseId}/branches")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Agregar una sucursal a una franquicia",
			description = "La URL de la sucursal creada viene en el header `Location`. "
					+ "Errores: 400 si el nombre está vacío o supera 100 caracteres; 404 si la franquicia no existe; "
					+ "409 si la franquicia ya tiene una sucursal con ese nombre.")
	public ResponseEntity<ApiResponse<Void>> create(@PathVariable Long franchiseId,
			@Valid @RequestBody NameRequest request) {
		BranchResponse created = branchService.create(franchiseId, request.name());
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path("/branches/{id}")
			.buildAndExpand(created.id())
			.toUri();
		return ApiResponse.created(location, ApiMessages.CREATED);
	}

	@GetMapping("/branches/{id}")
	@Operation(summary = "Consultar una sucursal con sus productos",
			description = "Errores: 404 si la sucursal no existe.")
	public ResponseEntity<ApiResponse<BranchResponse>> findById(@PathVariable Long id) {
		return ApiResponse.ok(ApiMessages.FOUND, branchService.findById(id));
	}

	@PatchMapping("/branches/{id}")
	@Operation(summary = "Actualizar el nombre de una sucursal",
			description = "Errores: 400 si el nombre está vacío o supera 100 caracteres; 404 si la sucursal no existe; "
					+ "409 si otra sucursal de la misma franquicia ya usa ese nombre.")
	public ResponseEntity<ApiResponse<Void>> updateBranch(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
		branchService.updateBranch(id, request.name());
		return ApiResponse.ok(ApiMessages.UPDATED);
	}

}
