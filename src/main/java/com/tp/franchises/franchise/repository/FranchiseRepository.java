package com.tp.franchises.franchise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tp.franchises.franchise.model.Franchise;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

	boolean existsByName(String name);

}
