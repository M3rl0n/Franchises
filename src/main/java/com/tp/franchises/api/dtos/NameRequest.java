package com.tp.franchises.api.dtos;

import com.tp.franchises.api.utils.ApiMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NameRequest(
		@NotBlank(message = ApiMessages.NAME_REQUIRED)
		@Size(max = 100, message = ApiMessages.NAME_TOO_LONG) String name) {
}
