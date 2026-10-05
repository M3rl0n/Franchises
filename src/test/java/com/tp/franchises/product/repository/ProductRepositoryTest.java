package com.tp.franchises.product.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.tp.franchises.branch.model.Branch;
import com.tp.franchises.branch.repository.BranchRepository;
import com.tp.franchises.franchise.model.Franchise;
import com.tp.franchises.franchise.repository.FranchiseRepository;
import com.tp.franchises.product.model.Product;

@DataJpaTest
class ProductRepositoryTest {

	@Autowired
	private FranchiseRepository franchiseRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private ProductRepository productRepository;

	@Test
	void findTopStockReturnsTheProductsWithMostStockOfEachBranchIncludingTies() {
		Franchise franchise = franchiseRepository.save(new Franchise("Norte"));
		Branch centro = branchRepository.save(new Branch("Centro", franchise));
		Branch sur = branchRepository.save(new Branch("Sur", franchise));
		branchRepository.save(new Branch("Sin productos", franchise));
		productRepository.save(new Product("Café", 25, centro));
		productRepository.save(new Product("Té", 25, centro));
		productRepository.save(new Product("Agua", 10, centro));
		productRepository.save(new Product("Pan", 7, sur));
		Branch otherFranchiseBranch = branchRepository.save(new Branch("Centro", franchiseRepository.save(new Franchise("Sur"))));
		productRepository.save(new Product("Jugo", 99, otherFranchiseBranch));

		assertThat(productRepository.findTopStockByFranchiseId(franchise.getId()))
			.extracting(product -> product.getBranch().getName(), Product::getName, Product::getStock)
			.containsExactly(tuple("Centro", "Café", 25), tuple("Centro", "Té", 25), tuple("Sur", "Pan", 7));
	}

	@Test
	void findTopStockReturnsAnEmptyListWhenTheFranchiseHasNoProducts() {
		Franchise franchise = franchiseRepository.save(new Franchise("Norte"));
		branchRepository.save(new Branch("Centro", franchise));

		assertThat(productRepository.findTopStockByFranchiseId(franchise.getId())).isEmpty();
	}

}
