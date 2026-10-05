package com.tp.franchises.franchise.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tp.franchises.api.dtos.FranchiseDetailResponse;
import com.tp.franchises.api.dtos.FranchiseResponse;
import com.tp.franchises.api.dtos.NameRequest;
import com.tp.franchises.api.response.ApiResponse;
import com.tp.franchises.franchise.services.FranchiseService;
import com.tp.franchises.franchise.utils.FranchiseMessages;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/franchises")
@RequiredArgsConstructor
public class FranchiseController {

	private final FranchiseService franchiseService;

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody NameRequest request) {
		FranchiseResponse created = franchiseService.create(request.name());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.id())
			.toUri();
		return ApiResponse.created(location, FranchiseMessages.CREATED);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<FranchiseResponse>>> findAll() {
		return ApiResponse.ok(FranchiseMessages.FOUND_ALL, franchiseService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<FranchiseDetailResponse>> findById(@PathVariable Long id) {
		return ApiResponse.ok(FranchiseMessages.FOUND, franchiseService.findById(id));
	}

	@PatchMapping ("/{id}")
	public ResponseEntity<ApiResponse<Void>> updateName(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
		franchiseService.updateName(id, request.name());
		return ApiResponse.ok(FranchiseMessages.UPDATED);
	}

}
