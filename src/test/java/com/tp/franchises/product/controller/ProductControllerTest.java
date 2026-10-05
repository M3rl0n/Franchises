package com.tp.franchises.product.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.dtos.TopStockProductResponse;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.product.services.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProductService productService;

	@Test
	void createReturns201WithTheLocationOfTheNewProduct() throws Exception {
		when(productService.create(2L, "Café", 10)).thenReturn(new ProductResponse(3L, "Café", 10, 2L));

		mockMvc.perform(post("/branches/2/products").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Café", "stock": 10}
				"""))
			.andExpect(status().isCreated())
			.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/products/3"))
			.andExpect(jsonPath("$.status").value(201))
			.andExpect(jsonPath("$.message").value(ApiMessages.CREATED))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void createReturns400WithEveryInvalidField() throws Exception {
		mockMvc.perform(post("/branches/2/products").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "", "stock": -1}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(ApiMessages.VALIDATION_FAILED))
			.andExpect(jsonPath("$.errors.name").value(ApiMessages.NAME_REQUIRED))
			.andExpect(jsonPath("$.errors.stock").value(ApiMessages.STOCK_NEGATIVE));
		verifyNoInteractions(productService);
	}

	@Test
	void findByIdReturnsTheProduct() throws Exception {
		when(productService.findById(3L)).thenReturn(new ProductResponse(3L, "Café", 10, 2L));

		mockMvc.perform(get("/products/3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(ApiMessages.FOUND))
			.andExpect(jsonPath("$.data.id").value(3))
			.andExpect(jsonPath("$.data.name").value("Café"))
			.andExpect(jsonPath("$.data.stock").value(10))
			.andExpect(jsonPath("$.data.branchId").value(2));
	}

	@Test
	void updateProductReturns200WithoutData() throws Exception {
		mockMvc.perform(patch("/products/3").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Té"}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(ApiMessages.UPDATED))
			.andExpect(jsonPath("$.data").doesNotExist());
		verify(productService).updateProduct(3L, "Té");
	}

	@Test
	void updateStockReturns200WithoutData() throws Exception {
		mockMvc.perform(patch("/products/3/stock").contentType(MediaType.APPLICATION_JSON).content("""
				{"stock": 25}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(ApiMessages.UPDATED))
			.andExpect(jsonPath("$.data").doesNotExist());
		verify(productService).updateStock(3L, 25);
	}

	@Test
	void updateStockReturns400WhenTheStockIsMissing() throws Exception {
		mockMvc.perform(patch("/products/3/stock").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors.stock").value(ApiMessages.STOCK_REQUIRED));
		verifyNoInteractions(productService);
	}

	@Test
	void deleteReturns200WithoutData() throws Exception {
		mockMvc.perform(delete("/products/3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.message").value(ApiMessages.DELETED))
			.andExpect(jsonPath("$.data").doesNotExist());
		verify(productService).delete(3L);
	}

	@Test
	void findTopStockByFranchiseReturnsEachProductWithItsBranch() throws Exception {
		when(productService.findTopStockByFranchise(1L)).thenReturn(List.of(
				new TopStockProductResponse(2L, "Centro", 3L, "Café", 25),
				new TopStockProductResponse(4L, "Sur", 6L, "Pan", 7)));

		mockMvc.perform(get("/franchises/1/products/top-stock"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(ApiMessages.FOUND))
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[0].branchId").value(2))
			.andExpect(jsonPath("$.data[0].branchName").value("Centro"))
			.andExpect(jsonPath("$.data[0].productId").value(3))
			.andExpect(jsonPath("$.data[0].productName").value("Café"))
			.andExpect(jsonPath("$.data[0].stock").value(25))
			.andExpect(jsonPath("$.data[1].branchName").value("Sur"));
	}

	@Test
	void findTopStockByFranchiseReturns404WhenTheFranchiseDoesNotExist() throws Exception {
		when(productService.findTopStockByFranchise(9L))
			.thenThrow(new ResourceNotFoundException(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L)));

		mockMvc.perform(get("/franchises/9/products/top-stock"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L)));
	}

}
