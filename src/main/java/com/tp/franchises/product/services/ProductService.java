package com.tp.franchises.product.services;

import java.util.List;

import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.dtos.TopStockProductResponse;

public interface ProductService {

	ProductResponse create(Long branchId, String name, Integer stock);

	ProductResponse findById(Long id);

	void updateProduct(Long id, String name);

	void updateStock(Long id, Integer stock);

	void delete(Long id);

	List<TopStockProductResponse> findTopStockByFranchise(Long franchiseId);

}
