package com.tp.franchises.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tp.franchises.product.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByBranchIdAndName(Long branchId, String name);

	boolean existsByBranchIdAndNameAndIdNot(Long branchId, String name, Long id);

	@Query("""
			SELECT p FROM Product p JOIN FETCH p.branch b
			WHERE b.franchise.id = :franchiseId
			  AND p.stock = (SELECT MAX(p2.stock) FROM Product p2 WHERE p2.branch = b)
			ORDER BY b.id, p.id
			""")
	List<Product> findTopStockByFranchiseId(Long franchiseId);

}
