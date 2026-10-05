package com.tp.franchises.api.dtos;

import com.tp.franchises.franchise.model.Franchise;

public record FranchiseResponse(Long id, String name) {

	public static FranchiseResponse from(Franchise franchise) {
		return new FranchiseResponse(franchise.getId(), franchise.getName());
	}

}
