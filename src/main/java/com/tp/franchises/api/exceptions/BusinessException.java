package com.tp.franchises.api.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

	private final HttpStatus status;

	protected BusinessException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

}
