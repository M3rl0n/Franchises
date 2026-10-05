package com.tp.franchises.api.response;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int status, String message, T data, Map<String, String> errors) {

	public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
		return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), message, data, null));
	}

	public static ResponseEntity<ApiResponse<Void>> ok(String message) {
		return ok(message, null);
	}

	public static ResponseEntity<ApiResponse<Void>> created(URI location, String message) {
		return ResponseEntity.created(location)
			.body(new ApiResponse<>(HttpStatus.CREATED.value(), message, null, null));
	}

	public static ApiResponse<Void> error(HttpStatusCode status, String message) {
		return error(status, message, null);
	}

	public static ApiResponse<Void> error(HttpStatusCode status, String message, Map<String, String> errors) {
		return new ApiResponse<>(status.value(), message, null, errors);
	}

}
