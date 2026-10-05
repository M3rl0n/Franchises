package com.tp.franchises.branch.services;

import com.tp.franchises.api.dtos.BranchResponse;

public interface BranchService {

	BranchResponse create(Long franchiseId, String name);

	BranchResponse findById(Long id);

	void updateBranch(Long id, String name);

}
