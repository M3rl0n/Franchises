package com.tp.franchises.franchise.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tp.franchises.api.dtos.FranchiseDetailResponse;
import com.tp.franchises.api.dtos.FranchiseResponse;
import com.tp.franchises.api.exceptions.DuplicateResourceException;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.franchise.model.Franchise;
import com.tp.franchises.franchise.repository.FranchiseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FranchiseServiceImpl implements FranchiseService {

	private final FranchiseRepository franchiseRepository;

	@Override
	@Transactional
	public FranchiseResponse create(String name) {
		String trimmedName = name.trim();
		if (franchiseRepository.existsByName(trimmedName)) {
			throw new DuplicateResourceException(ApiMessages.FRANCHISE_ALREADY_EXISTS.formatted(trimmedName));
		}
		return FranchiseResponse.from(franchiseRepository.save(new Franchise(trimmedName)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<FranchiseResponse> findAll() {
		return franchiseRepository.findAll().stream().map(FranchiseResponse::from).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public FranchiseDetailResponse findById(Long id) {
		return FranchiseDetailResponse.from(getFranchise(id));
	}

	@Override
	@Transactional
	public void updateFranchise(Long id, String name) {
		Franchise franchise = getFranchise(id);
		String trimmedName = name.trim();
		if (franchiseRepository.existsByNameAndIdNot(trimmedName, id)) {
			throw new DuplicateResourceException(ApiMessages.FRANCHISE_ALREADY_EXISTS.formatted(trimmedName));
		}
		franchise.setName(trimmedName);
	}

	private Franchise getFranchise(Long id) {
		return franchiseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ApiMessages.FRANCHISE_NOT_FOUND.formatted(id)));
	}

}
