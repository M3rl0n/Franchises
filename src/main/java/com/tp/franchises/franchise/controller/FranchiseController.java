package com.tp.franchises.franchise.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tp.franchises.api.dtos.FranchiseDetailResponse;
import com.tp.franchises.api.dtos.FranchiseResponse;
import com.tp.franchises.api.dtos.NameRequest;
import com.tp.franchises.api.response.ApiResponse;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.franchise.services.FranchiseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/franchises")
@RequiredArgsConstructor
@Tag(name = "Franquicias")
public class FranchiseController {

	private final FranchiseService franchiseService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Crear una franquicia",
			description = "La URL de la franquicia creada viene en el header `Location`. "
					+ "Errores: 400 si el nombre está vacío o supera 100 caracteres; 409 si ya existe una franquicia con ese nombre.")
	public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody NameRequest request) {
		FranchiseResponse created = franchiseService.create(request.name());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.id())
			.toUri();
		return ApiResponse.created(location, ApiMessages.CREATED);
	}

	@GetMapping
	@Operation(summary = "Listar las franquicias")
	public ResponseEntity<ApiResponse<List<FranchiseResponse>>> findAll() {
		return ApiResponse.ok(ApiMessages.FOUND, franchiseService.findAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Consultar una franquicia con sus sucursales y productos",
			description = "Errores: 404 si la franquicia no existe.")
	public ResponseEntity<ApiResponse<FranchiseDetailResponse>> findById(@PathVariable Long id) {
		return ApiResponse.ok(ApiMessages.FOUND, franchiseService.findById(id));
	}

	@PatchMapping ("/{id}")
	@Operation(summary = "Actualizar el nombre de una franquicia",
			description = "Errores: 400 si el nombre está vacío o supera 100 caracteres; 404 si la franquicia no existe; "
					+ "409 si otra franquicia ya usa ese nombre.")
	public ResponseEntity<ApiResponse<Void>> updateFranchise(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
		franchiseService.updateFranchise(id, request.name());
		return ApiResponse.ok(ApiMessages.UPDATED);
	}

}
