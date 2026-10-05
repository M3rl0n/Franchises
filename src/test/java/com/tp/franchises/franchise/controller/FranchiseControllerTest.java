package com.tp.franchises.franchise.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import com.tp.franchises.api.dtos.FranchiseDetailResponse;
import com.tp.franchises.api.dtos.FranchiseDetailResponse.BranchDetail;
import com.tp.franchises.api.dtos.FranchiseResponse;
import com.tp.franchises.api.dtos.ProductResponse;
import com.tp.franchises.api.exceptions.DuplicateResourceException;
import com.tp.franchises.api.exceptions.ResourceNotFoundException;
import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.franchise.services.FranchiseService;

@WebMvcTest(FranchiseController.class)
class FranchiseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FranchiseService franchiseService;

	@Test
	void createReturns201WithTheLocationOfTheNewFranchise() throws Exception {
		when(franchiseService.create("Norte")).thenReturn(new FranchiseResponse(1L, "Norte"));

		mockMvc.perform(post("/franchises").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Norte"}
				"""))
			.andExpect(status().isCreated())
			.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/franchises/1"))
			.andExpect(jsonPath("$.status").value(201))
			.andExpect(jsonPath("$.message").value(ApiMessages.CREATED))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void createReturns400WhenTheNameIsBlank() throws Exception {
		mockMvc.perform(post("/franchises").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": " "}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value(ApiMessages.VALIDATION_FAILED))
			.andExpect(jsonPath("$.errors.name").value(ApiMessages.NAME_REQUIRED));
		verifyNoInteractions(franchiseService);
	}

	@Test
	void createReturns400WhenTheNameIsTooLong() throws Exception {
		mockMvc.perform(post("/franchises").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"%s\"}".formatted("a".repeat(101))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors.name").value(ApiMessages.NAME_TOO_LONG));
		verifyNoInteractions(franchiseService);
	}

	@Test
	void createReturns409WhenTheNameAlreadyExists() throws Exception {
		when(franchiseService.create("Norte"))
			.thenThrow(new DuplicateResourceException(ApiMessages.FRANCHISE_ALREADY_EXISTS.formatted("Norte")));

		mockMvc.perform(post("/franchises").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Norte"}
				"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.status").value(409))
			.andExpect(jsonPath("$.message").value(ApiMessages.FRANCHISE_ALREADY_EXISTS.formatted("Norte")));
	}

	@Test
	void findAllReturnsTheFranchises() throws Exception {
		when(franchiseService.findAll()).thenReturn(List.of(new FranchiseResponse(1L, "Norte")));

		mockMvc.perform(get("/franchises"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.message").value(ApiMessages.FOUND))
			.andExpect(jsonPath("$.data[0].id").value(1))
			.andExpect(jsonPath("$.data[0].name").value("Norte"));
	}

	@Test
	void findByIdReturnsTheFranchiseWithBranchesAndProducts() throws Exception {
		when(franchiseService.findById(1L)).thenReturn(new FranchiseDetailResponse(1L, "Norte",
				List.of(new BranchDetail(2L, "Centro", List.of(new ProductResponse(3L, "Café", 10, 2L))))));

		mockMvc.perform(get("/franchises/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.name").value("Norte"))
			.andExpect(jsonPath("$.data.branches[0].name").value("Centro"))
			.andExpect(jsonPath("$.data.branches[0].products[0].name").value("Café"))
			.andExpect(jsonPath("$.data.branches[0].products[0].stock").value(10));
	}

	@Test
	void findByIdReturns404WhenTheFranchiseDoesNotExist() throws Exception {
		when(franchiseService.findById(9L))
			.thenThrow(new ResourceNotFoundException(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L)));

		mockMvc.perform(get("/franchises/9"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.message").value(ApiMessages.FRANCHISE_NOT_FOUND.formatted(9L)));
	}

	@Test
	void updateFranchiseReturns200WithoutData() throws Exception {
		mockMvc.perform(patch("/franchises/1").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Sur"}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.message").value(ApiMessages.UPDATED))
			.andExpect(jsonPath("$.data").doesNotExist());
		verify(franchiseService).updateFranchise(1L, "Sur");
	}

}
