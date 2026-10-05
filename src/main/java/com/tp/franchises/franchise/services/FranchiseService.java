package com.tp.franchises.franchise.services;

import java.util.List;

import com.tp.franchises.api.dtos.FranchiseDetailResponse;
import com.tp.franchises.api.dtos.FranchiseResponse;

public interface FranchiseService {

	FranchiseResponse create(String name);

	List<FranchiseResponse> findAll();

	FranchiseDetailResponse findById(Long id);

	void updateName(Long id, String name);

}
