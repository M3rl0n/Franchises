package com.tp.franchises.branch.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tp.franchises.api.dtos.BranchResponse;
import com.tp.franchises.api.exceptions.DuplicateResourceException;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.branch.model.Branch;
import com.tp.franchises.branch.repository.BranchRepository;
import com.tp.franchises.franchise.model.Franchise;
import com.tp.franchises.franchise.repository.FranchiseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

	private final BranchRepository branchRepository;

	private final FranchiseRepository franchiseRepository;

	@Override
	@Transactional
	public BranchResponse create(Long franchiseId, String name) {
		Franchise franchise = franchiseRepository.findById(franchiseId)
			.orElseThrow(() -> new ResourceNotFoundException(ApiMessages.FRANCHISE_NOT_FOUND.formatted(franchiseId)));
		String trimmedName = name.trim();
		if (branchRepository.existsByFranchiseIdAndName(franchiseId, trimmedName)) {
			throw new DuplicateResourceException(ApiMessages.BRANCH_ALREADY_EXISTS.formatted(trimmedName));
		}
		return BranchResponse.from(branchRepository.save(new Branch(trimmedName, franchise)));
	}

	@Override
	@Transactional(readOnly = true)
	public BranchResponse findById(Long id) {
		return BranchResponse.from(getBranch(id));
	}

	@Override
	@Transactional
	public void updateBranch(Long id, String name) {
		Branch branch = getBranch(id);
		String trimmedName = name.trim();
		if (branchRepository.existsByFranchiseIdAndNameAndIdNot(branch.getFranchise().getId(), trimmedName, id)) {
			throw new DuplicateResourceException(ApiMessages.BRANCH_ALREADY_EXISTS.formatted(trimmedName));
		}
		branch.setName(trimmedName);
	}

	private Branch getBranch(Long id) {
		return branchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ApiMessages.BRANCH_NOT_FOUND.formatted(id)));
	}

}
