package com.tp.franchises.branch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tp.franchises.branch.model.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

	boolean existsByFranchiseIdAndName(Long franchiseId, String name);

	boolean existsByFranchiseIdAndNameAndIdNot(Long franchiseId, String name, Long id);

}
