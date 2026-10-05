package com.tp.franchises.branch.services;

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

import com.tp.franchises.api.dtos.BranchResponse;
import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.exceptions.DuplicateResourceException;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.branch.model.Branch;
import com.tp.franchises.branch.repository.BranchRepository;
import com.tp.franchises.franchise.model.Franchise;
import com.tp.franchises.franchise.repository.FranchiseRepository;
import com.tp.franchises.product.model.Product;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

	@Mock
	private BranchRepository branchRepository;

	@Mock
	private FranchiseRepository franchiseRepository;

	@InjectMocks
	private BranchServiceImpl branchService;

	@Test
	void createSavesTheBranchInTheFranchise() {
		Franchise franchise = franchise(1L);
		when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));
		when(branchRepository.existsByFranchiseIdAndName(1L, "Centro")).thenReturn(false);
		when(branchRepository.save(any(Branch.class))).thenAnswer(invocation -> {
			Branch branch = invocation.getArgument(0);
			branch.setId(2L);
			return branch;
		});

		BranchResponse response = branchService.create(1L, "  Centro  ");

		assertThat(response).isEqualTo(new BranchResponse(2L, "Centro", 1L, List.of()));
	}

	@Test
	void createThrowsNotFoundWhenTheFranchiseDoesNotExist() {
		when(franchiseRepository.findById(9L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> branchService.create(9L, "Centro"))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L));
	}

	@Test
	void createThrowsDuplicateWhenTheFranchiseAlreadyHasTheName() {
		when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise(1L)));
		when(branchRepository.existsByFranchiseIdAndName(1L, "Centro")).thenReturn(true);

		assertThatThrownBy(() -> branchService.create(1L, "Centro"))
			.isInstanceOf(DuplicateResourceException.class)
			.hasMessage(ApiMessages.BRANCH_ALREADY_EXISTS.formatted("Centro"));
		verify(branchRepository, never()).save(any());
	}

	@Test
	void findByIdReturnsTheBranchWithItsProducts() {
		Branch branch = branch(2L, "Centro");
		Product product = new Product("Café", 10, branch);
		product.setId(3L);
		branch.getProducts().add(product);
		when(branchRepository.findById(2L)).thenReturn(Optional.of(branch));

		assertThat(branchService.findById(2L))
			.isEqualTo(new BranchResponse(2L, "Centro", 1L, List.of(new ProductResponse(3L, "Café", 10, 2L))));
	}

	@Test
	void findByIdThrowsNotFoundWhenTheBranchDoesNotExist() {
		when(branchRepository.findById(9L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> branchService.findById(9L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage(ApiMessages.BRANCH_NOT_FOUND.formatted(9L));
	}

	@Test
	void updateBranchRenamesWithTheTrimmedName() {
		Branch branch = branch(2L, "Centro");
		when(branchRepository.findById(2L)).thenReturn(Optional.of(branch));
		when(branchRepository.existsByFranchiseIdAndNameAndIdNot(1L, "Norte", 2L)).thenReturn(false);

		branchService.updateBranch(2L, " Norte ");

		assertThat(branch.getName()).isEqualTo("Norte");
	}

	@Test
	void updateBranchThrowsDuplicateWhenAnotherBranchOfTheFranchiseHasTheName() {
		Branch branch = branch(2L, "Centro");
		when(branchRepository.findById(2L)).thenReturn(Optional.of(branch));
		when(branchRepository.existsByFranchiseIdAndNameAndIdNot(1L, "Norte", 2L)).thenReturn(true);

		assertThatThrownBy(() -> branchService.updateBranch(2L, "Norte"))
			.isInstanceOf(DuplicateResourceException.class)
			.hasMessage(ApiMessages.BRANCH_ALREADY_EXISTS.formatted("Norte"));
		assertThat(branch.getName()).isEqualTo("Centro");
	}

	private static Franchise franchise(Long id) {
		Franchise franchise = new Franchise("Norte");
		franchise.setId(id);
		return franchise;
	}

	private static Branch branch(Long id, String name) {
		Branch branch = new Branch(name, franchise(1L));
		branch.setId(id);
		return branch;
	}

}
