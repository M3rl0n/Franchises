package com.tp.franchises.api.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tp.franchises.api.response.ApiResponse;
import com.tp.franchises.api.utils.ApiMessages;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
		return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getStatus(), ex.getMessage()));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(ApiResponse.error(HttpStatus.CONFLICT, ApiMessages.DATA_CONFLICT));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
		log.error("Error no controlado", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ApiMessages.UNEXPECTED_ERROR));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(error.getField(), error.getDefaultMessage());
		}
		ApiResponse<Void> body = ApiResponse.error(status, ApiMessages.VALIDATION_FAILED, errors);
		return handleExceptionInternal(ex, body, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		Object apiBody = (body instanceof ApiResponse<?>) ? body : ApiResponse.error(statusCode, messageFor(statusCode));
		return super.handleExceptionInternal(ex, apiBody, headers, statusCode, request);
	}

	private static String messageFor(HttpStatusCode statusCode) {
		return switch (statusCode.value()) {
			case 400 -> ApiMessages.BAD_REQUEST;
			case 404 -> ApiMessages.RESOURCE_NOT_FOUND;
			case 405 -> ApiMessages.METHOD_NOT_ALLOWED;
			case 415 -> ApiMessages.UNSUPPORTED_MEDIA_TYPE;
			default -> ApiMessages.REQUEST_ERROR;
		};
	}

}
