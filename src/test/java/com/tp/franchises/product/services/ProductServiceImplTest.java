package com.tp.franchises.product.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.dtos.TopStockProductResponse;
import com.tp.franchises.api.exceptions.DuplicateResourceException;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.branch.model.Branch;
import com.tp.franchises.branch.repository.BranchRepository;
import com.tp.franchises.franchise.model.Franchise;
import com.tp.franchises.franchise.repository.FranchiseRepository;
import com.tp.franchises.product.model.Product;
import com.tp.franchises.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private BranchRepository branchRepository;

	@Mock
	private FranchiseRepository franchiseRepository;

	@InjectMocks
	private ProductServiceImpl productService;

	@Test
	void createSavesTheProductInTheBranch() {
		when(branchRepository.findById(2L)).thenReturn(Optional.of(branch(2L, "Centro")));
		when(productRepository.existsByBranchIdAndName(2L, "Café")).thenReturn(false);
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
			Product product = invocation.getArgument(0);
			product.setId(3L);
			return product;
		});

		ProductResponse response = productService.create(2L, "  Café  ", 10);

		assertThat(response).isEqualTo(new ProductResponse(3L, "Café", 10, 2L));
	}

	@Test
	void createThrowsNotFoundWhenTheBranchDoesNotExist() {
		when(branchRepository.findById(9L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productService.create(9L, "Café", 10))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage(ApiMessages.BRANCH_NOT_FOUND.formatted(9L));
	}

	@Test
	void createThrowsDuplicateWhenTheBranchAlreadyHasTheName() {
		when(branchRepository.findById(2L)).thenReturn(Optional.of(branch(2L, "Centro")));
		when(productRepository.existsByBranchIdAndName(2L, "Café")).thenReturn(true);

		assertThatThrownBy(() -> productService.create(2L, "Café", 10))
			.isInstanceOf(DuplicateResourceException.class)
			.hasMessage(ApiMessages.PRODUCT_ALREADY_EXISTS.formatted("Café"));
		verify(productRepository, never()).save(any());
	}

	@Test
	void findByIdReturnsTheProduct() {
		when(productRepository.findById(3L)).thenReturn(Optional.of(product(3L, "Café", 10, branch(2L, "Centro"))));

		assertThat(productService.findById(3L)).isEqualTo(new ProductResponse(3L, "Café", 10, 2L));
	}

	@Test
	void findByIdThrowsNotFoundWhenTheProductDoesNotExist() {
		when(productRepository.findById(9L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productService.findById(9L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage(ApiMessages.PRODUCT_NOT_FOUND.formatted(9L));
	}

	@Test
	void updateProductRenamesWithTheTrimmedName() {
		Product product = product(3L, "Café", 10, branch(2L, "Centro"));
		when(productRepository.findById(3L)).thenReturn(Optional.of(product));
		when(productRepository.existsByBranchIdAndNameAndIdNot(2L, "Té", 3L)).thenReturn(false);

		productService.updateProduct(3L, " Té ");

		assertThat(product.getName()).isEqualTo("Té");
	}

	@Test
	void updateProductThrowsDuplicateWhenAnotherProductOfTheBranchHasTheName() {
		Product product = product(3L, "Café", 10, branch(2L, "Centro"));
		when(productRepository.findById(3L)).thenReturn(Optional.of(product));
		when(productRepository.existsByBranchIdAndNameAndIdNot(2L, "Té", 3L)).thenReturn(true);

		assertThatThrownBy(() -> productService.updateProduct(3L, "Té"))
			.isInstanceOf(DuplicateResourceException.class)
			.hasMessage(ApiMessages.PRODUCT_ALREADY_EXISTS.formatted("Té"));
		assertThat(product.getName()).isEqualTo("Café");
	}

	@Test
	void updateStockChangesTheStock() {
		Product product = product(3L, "Café", 10, branch(2L, "Centro"));
		when(productRepository.findById(3L)).thenReturn(Optional.of(product));

		productService.updateStock(3L, 25);

		assertThat(product.getStock()).isEqualTo(25);
	}

	@Test
	void deleteRemovesTheProduct() {
		Product product = product(3L, "Café", 10, branch(2L, "Centro"));
		when(productRepository.findById(3L)).thenReturn(Optional.of(product));

		productService.delete(3L);

		verify(productRepository).delete(product);
	}

	@Test
	void findTopStockByFranchiseMapsEachProductWithItsBranch() {
		Branch centro = branch(2L, "Centro");
		Branch sur = branch(4L, "Sur");
		when(franchiseRepository.existsById(1L)).thenReturn(true);
		when(productRepository.findTopStockByFranchiseId(1L))
			.thenReturn(List.of(product(3L, "Café", 25, centro), product(5L, "Té", 25, centro), product(6L, "Pan", 7, sur)));

		assertThat(productService.findTopStockByFranchise(1L)).containsExactly(
				new TopStockProductResponse(2L, "Centro", 3L, "Café", 25),
				new TopStockProductResponse(2L, "Centro", 5L, "Té", 25),
				new TopStockProductResponse(4L, "Sur", 6L, "Pan", 7));
	}

	@Test
	void findTopStockByFranchiseThrowsNotFoundWhenTheFranchiseDoesNotExist() {
		when(franchiseRepository.existsById(9L)).thenReturn(false);

		assertThatThrownBy(() -> productService.findTopStockByFranchise(9L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L));
		verify(productRepository, never()).findTopStockByFranchiseId(any());
	}

	private static Branch branch(Long id, String name) {
		Franchise franchise = new Franchise("Norte");
		franchise.setId(1L);
		Branch branch = new Branch(name, franchise);
		branch.setId(id);
		return branch;
	}

	private static Product product(Long id, String name, Integer stock, Branch branch) {
		Product product = new Product(name, stock, branch);
		product.setId(id);
		return product;
	}

}
