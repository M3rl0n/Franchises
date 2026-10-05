package com.tp.franchises.product.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.dtos.TopStockProductResponse;
import com.tp.franchises.api.exceptions.DuplicateResourceException;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.branch.model.Branch;
import com.tp.franchises.branch.repository.BranchRepository;
import com.tp.franchises.franchise.repository.FranchiseRepository;
import com.tp.franchises.product.model.Product;
import com.tp.franchises.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	private final BranchRepository branchRepository;

	private final FranchiseRepository franchiseRepository;

	@Override
	@Transactional
	public ProductResponse create(Long branchId, String name, Integer stock) {
		Branch branch = branchRepository.findById(branchId)
			.orElseThrow(() -> new ResourceNotFoundException(ApiMessages.BRANCH_NOT_FOUND.formatted(branchId)));
		String trimmedName = name.trim();
		if (productRepository.existsByBranchIdAndName(branchId, trimmedName)) {
			throw new DuplicateResourceException(ApiMessages.PRODUCT_ALREADY_EXISTS.formatted(trimmedName));
		}
		return ProductResponse.from(productRepository.save(new Product(trimmedName, stock, branch)));
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse findById(Long id) {
		return ProductResponse.from(getProduct(id));
	}

	@Override
	@Transactional
	public void updateProduct(Long id, String name) {
		Product product = getProduct(id);
		String trimmedName = name.trim();
		if (productRepository.existsByBranchIdAndNameAndIdNot(product.getBranch().getId(), trimmedName, id)) {
			throw new DuplicateResourceException(ApiMessages.PRODUCT_ALREADY_EXISTS.formatted(trimmedName));
		}
		product.setName(trimmedName);
	}

	@Override
	@Transactional
	public void updateStock(Long id, Integer stock) {
		getProduct(id).setStock(stock);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		productRepository.delete(getProduct(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<TopStockProductResponse> findTopStockByFranchise(Long franchiseId) {
		if (!franchiseRepository.existsById(franchiseId)) {
			throw new ResourceNotFoundException(ApiMessages.FRANCHISE_NOT_FOUND.formatted(franchiseId));
		}
		return productRepository.findTopStockByFranchiseId(franchiseId).stream()
			.map(TopStockProductResponse::from)
			.toList();
	}

	private Product getProduct(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ApiMessages.PRODUCT_NOT_FOUND.formatted(id)));
	}

}
