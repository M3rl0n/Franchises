package com.tp.franchises.api.dtos;

import com.tp.franchises.api.utils.ApiMessages;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(
		@Schema(example = "Hamburguesa clásica")
		@NotBlank(message = ApiMessages.NAME_REQUIRED)
		@Size(max = 100, message = ApiMessages.NAME_TOO_LONG) String name,
		@Schema(example = "50")
		@NotNull(message = ApiMessages.STOCK_REQUIRED)
		@PositiveOrZero(message = ApiMessages.STOCK_NEGATIVE) Integer stock) {
}
