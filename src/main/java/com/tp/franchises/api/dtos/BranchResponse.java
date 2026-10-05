package com.tp.franchises.api.dtos;

import java.util.List;

import com.tp.franchises.branch.model.Branch;

public record BranchResponse(Long id, String name, Long franchiseId, List<ProductResponse> products) {

	public static BranchResponse from(Branch branch) {
		return new BranchResponse(branch.getId(), branch.getName(), branch.getFranchise().getId(),
				branch.getProducts().stream().map(ProductResponse::from).toList());
	}

}
