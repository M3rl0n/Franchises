package com.tp.franchises.api.dtos;

import java.util.List;

import com.tp.franchises.branch.model.Branch;
import com.tp.franchises.franchise.model.Franchise;

public record FranchiseDetailResponse(Long id, String name, List<BranchDetail> branches) {

	public record BranchDetail(Long id, String name, List<ProductResponse> products) {

		static BranchDetail from(Branch branch) {
			return new BranchDetail(branch.getId(), branch.getName(),
					branch.getProducts().stream().map(ProductResponse::from).toList());
		}

	}

	public static FranchiseDetailResponse from(Franchise franchise) {
		return new FranchiseDetailResponse(franchise.getId(), franchise.getName(),
				franchise.getBranches().stream().map(BranchDetail::from).toList());
	}

}
