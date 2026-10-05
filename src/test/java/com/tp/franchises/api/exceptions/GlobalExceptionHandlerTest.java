package com.tp.franchises.api.exceptions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.tp.franchises.api.utils.ApiMessages;
import com.tp.franchises.franchise.controller.FranchiseController;
import com.tp.franchises.franchise.services.FranchiseService;

@WebMvcTest(FranchiseController.class)
class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FranchiseService franchiseService;

	@Test
	void malformedJsonReturns400() throws Exception {
		mockMvc.perform(post("/franchises").contentType(MediaType.APPLICATION_JSON).content("{\"name\":"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value(ApiMessages.BAD_REQUEST));
	}

	@Test
	void unknownRouteReturns404() throws Exception {
		mockMvc.perform(get("/unknown"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.message").value(ApiMessages.RESOURCE_NOT_FOUND));
	}

	@Test
	void unsupportedMethodReturns405() throws Exception {
		mockMvc.perform(delete("/franchises"))
			.andExpect(status().isMethodNotAllowed())
			.andExpect(jsonPath("$.status").value(405))
			.andExpect(jsonPath("$.message").value(ApiMessages.METHOD_NOT_ALLOWED));
	}

	@Test
	void unsupportedContentTypeReturns415() throws Exception {
		mockMvc.perform(post("/franchises").contentType(MediaType.TEXT_PLAIN).content("Norte"))
			.andExpect(status().isUnsupportedMediaType())
			.andExpect(jsonPath("$.status").value(415))
			.andExpect(jsonPath("$.message").value(ApiMessages.UNSUPPORTED_MEDIA_TYPE));
	}

	@Test
	void otherFrameworkErrorsKeepTheirStatusWithAGenericMessage() throws Exception {
		when(franchiseService.findAll()).thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));

		mockMvc.perform(get("/franchises"))
			.andExpect(status().isServiceUnavailable())
			.andExpect(jsonPath("$.status").value(503))
			.andExpect(jsonPath("$.message").value(ApiMessages.REQUEST_ERROR));
	}

	@Test
	void dataIntegrityViolationReturns409() throws Exception {
		when(franchiseService.findAll()).thenThrow(new DataIntegrityViolationException("duplicate key"));

		mockMvc.perform(get("/franchises"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.status").value(409))
			.andExpect(jsonPath("$.message").value(ApiMessages.DATA_CONFLICT));
	}

	@Test
	void unexpectedErrorReturns500WithoutInternalDetails() throws Exception {
		when(franchiseService.findAll()).thenThrow(new IllegalStateException("detalle interno"));

		mockMvc.perform(get("/franchises"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.status").value(500))
			.andExpect(jsonPath("$.message").value(ApiMessages.UNEXPECTED_ERROR))
			.andExpect(content().string(not(containsString("detalle interno"))));
	}

}
