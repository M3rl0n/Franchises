package com.tp.franchises.api.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException {

	public DuplicateResourceException(String message) {
		super(HttpStatus.CONFLICT, message);
	}

}
