package com.tp.franchises.api.dtos;

import com.tp.franchises.product.model.Product;

public record TopStockProductResponse(Long branchId, String branchName, Long productId, String productName,
		Integer stock) {

	public static TopStockProductResponse from(Product product) {
		return new TopStockProductResponse(product.getBranch().getId(), product.getBranch().getName(),
				product.getId(), product.getName(), product.getStock());
	}

}
