package com.tp.franchises.api.dtos;

import com.tp.franchises.product.model.Product;

public record ProductResponse(Long id, String name, Integer stock, Long branchId) {

	public static ProductResponse from(Product product) {
		return new ProductResponse(product.getId(), product.getName(), product.getStock(),
				product.getBranch().getId());
	}

}
