package com.tp.franchises.api.dtos;

import com.tp.franchises.api.utils.ApiMessages;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record StockUpdateRequest(
		@Schema(example = "30")
		@NotNull(message = ApiMessages.STOCK_REQUIRED)
		@PositiveOrZero(message = ApiMessages.STOCK_NEGATIVE) Integer stock) {
}
