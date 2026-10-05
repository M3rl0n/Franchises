package com.tp.franchises.branch.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tp.franchises.api.dtos.BranchResponse;
import com.tp.franchises.api.dtos.NameRequest;
import com.tp.franchises.api.response.ApiResponse;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.branch.services.BranchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BranchController {

	private final BranchService branchService;

	@PostMapping("/franchises/{franchiseId}/branches")
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
	public ResponseEntity<ApiResponse<BranchResponse>> findById(@PathVariable Long id) {
		return ApiResponse.ok(ApiMessages.FOUND, branchService.findById(id));
	}

	@PatchMapping("/branches/{id}")
	public ResponseEntity<ApiResponse<Void>> updateBranch(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
		branchService.updateBranch(id, request.name());
		return ApiResponse.ok(ApiMessages.UPDATED);
	}

}
