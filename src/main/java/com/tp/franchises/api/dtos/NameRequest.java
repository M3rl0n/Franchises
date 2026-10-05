package com.tp.franchises.api.dtos;

import com.tp.franchises.api.utils.ApiMessages;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NameRequest(
		@Schema(example = "Nombre de ejemplo")
		@NotBlank(message = ApiMessages.NAME_REQUIRED)
		@Size(max = 100, message = ApiMessages.NAME_TOO_LONG) String name) {
}
