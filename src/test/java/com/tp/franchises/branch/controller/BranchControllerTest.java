package com.tp.franchises.branch.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

import com.tp.franchises.api.dtos.BranchResponse;
import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.exceptions.DuplicateResourceException;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.branch.services.BranchService;

@WebMvcTest(BranchController.class)
class BranchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BranchService branchService;

	@Test
	void createReturns201WithTheLocationOfTheNewBranch() throws Exception {
		when(branchService.create(1L, "Centro")).thenReturn(new BranchResponse(2L, "Centro", 1L, List.of()));

		mockMvc.perform(post("/franchises/1/branches").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Centro"}
				"""))
			.andExpect(status().isCreated())
			.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/branches/2"))
			.andExpect(jsonPath("$.status").value(201))
			.andExpect(jsonPath("$.message").value(ApiMessages.CREATED))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void createReturns404WhenTheFranchiseDoesNotExist() throws Exception {
		when(branchService.create(9L, "Centro"))
			.thenThrow(new ResourceNotFoundException(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L)));

		mockMvc.perform(post("/franchises/9/branches").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Centro"}
				"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.message").value(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L)));
	}

	@Test
	void findByIdReturnsTheBranchWithItsProducts() throws Exception {
		when(branchService.findById(2L))
			.thenReturn(new BranchResponse(2L, "Centro", 1L, List.of(new ProductResponse(3L, "Café", 10, 2L))));

		mockMvc.perform(get("/branches/2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(ApiMessages.FOUND))
			.andExpect(jsonPath("$.data.name").value("Centro"))
			.andExpect(jsonPath("$.data.franchiseId").value(1))
			.andExpect(jsonPath("$.data.products[0].id").value(3))
			.andExpect(jsonPath("$.data.products[0].branchId").value(2));
	}

	@Test
	void updateBranchReturns200WithoutData() throws Exception {
		mockMvc.perform(patch("/branches/2").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Norte"}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(ApiMessages.UPDATED))
			.andExpect(jsonPath("$.data").doesNotExist());
		verify(branchService).updateBranch(2L, "Norte");
	}

	@Test
	void updateBranchReturns409WhenAnotherBranchHasTheName() throws Exception {
		doThrow(new DuplicateResourceException(ApiMessages.BRANCH_ALREADY_EXISTS.formatted("Norte")))
			.when(branchService).updateBranch(2L, "Norte");

		mockMvc.perform(patch("/branches/2").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Norte"}
				"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value(ApiMessages.BRANCH_ALREADY_EXISTS.formatted("Norte")));
	}

}
