package com.tp.franchises.franchise.services;

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

import com.tp.franchises.api.dtos.FranchiseDetailResponse;
import com.tp.franchises.api.dtos.FranchiseDetailResponse.BranchDetail;
import com.tp.franchises.api.dtos.FranchiseResponse;
import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.exceptions.DuplicateResourceException;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.branch.model.Branch;
import com.tp.franchises.franchise.model.Franchise;
import com.tp.franchises.franchise.repository.FranchiseRepository;
import com.tp.franchises.product.model.Product;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceImplTest {

	@Mock
	private FranchiseRepository franchiseRepository;

	@InjectMocks
	private FranchiseServiceImpl franchiseService;

	@Test
	void createSavesTheTrimmedName() {
		when(franchiseRepository.existsByName("Norte")).thenReturn(false);
		when(franchiseRepository.save(any(Franchise.class))).thenAnswer(invocation -> {
			Franchise franchise = invocation.getArgument(0);
			franchise.setId(1L);
			return franchise;
		});

		FranchiseResponse response = franchiseService.create("  Norte  ");

		assertThat(response).isEqualTo(new FranchiseResponse(1L, "Norte"));
	}

	@Test
	void createThrowsDuplicateWhenTheNameExists() {
		when(franchiseRepository.existsByName("Norte")).thenReturn(true);

		assertThatThrownBy(() -> franchiseService.create("Norte"))
			.isInstanceOf(DuplicateResourceException.class)
			.hasMessage(ApiMessages.FRANCHISE_ALREADY_EXISTS.formatted("Norte"));
		verify(franchiseRepository, never()).save(any());
	}

	@Test
	void findAllMapsEveryFranchise() {
		when(franchiseRepository.findAll()).thenReturn(List.of(franchise(1L, "Norte"), franchise(2L, "Sur")));

		assertThat(franchiseService.findAll())
			.containsExactly(new FranchiseResponse(1L, "Norte"), new FranchiseResponse(2L, "Sur"));
	}

	@Test
	void findByIdReturnsTheFranchiseWithBranchesAndProducts() {
		Franchise franchise = franchise(1L, "Norte");
		Branch branch = new Branch("Centro", franchise);
		branch.setId(2L);
		Product product = new Product("Café", 10, branch);
		product.setId(3L);
		branch.getProducts().add(product);
		franchise.getBranches().add(branch);
		when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));

		FranchiseDetailResponse response = franchiseService.findById(1L);

		assertThat(response).isEqualTo(new FranchiseDetailResponse(1L, "Norte",
				List.of(new BranchDetail(2L, "Centro", List.of(new ProductResponse(3L, "Café", 10, 2L))))));
	}

	@Test
	void findByIdThrowsNotFoundWhenTheFranchiseDoesNotExist() {
		when(franchiseRepository.findById(9L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> franchiseService.findById(9L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L));
	}

	@Test
	void updateFranchiseRenamesWithTheTrimmedName() {
		Franchise franchise = franchise(1L, "Norte");
		when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));
		when(franchiseRepository.existsByNameAndIdNot("Sur", 1L)).thenReturn(false);

		franchiseService.updateFranchise(1L, " Sur ");

		assertThat(franchise.getName()).isEqualTo("Sur");
	}

	@Test
	void updateFranchiseThrowsDuplicateWhenAnotherFranchiseHasTheName() {
		Franchise franchise = franchise(1L, "Norte");
		when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));
		when(franchiseRepository.existsByNameAndIdNot("Sur", 1L)).thenReturn(true);

		assertThatThrownBy(() -> franchiseService.updateFranchise(1L, "Sur"))
			.isInstanceOf(DuplicateResourceException.class)
			.hasMessage(ApiMessages.FRANCHISE_ALREADY_EXISTS.formatted("Sur"));
		assertThat(franchise.getName()).isEqualTo("Norte");
	}

	private static Franchise franchise(Long id, String name) {
		Franchise franchise = new Franchise(name);
		franchise.setId(id);
		return franchise;
	}

}
