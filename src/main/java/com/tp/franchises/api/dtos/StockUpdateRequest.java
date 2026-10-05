package com.tp.franchises.api.dtos;

import com.tp.franchises.api.utils.ApiMessages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record StockUpdateRequest(
		@NotNull(message = ApiMessages.STOCK_REQUIRED)
		@PositiveOrZero(message = ApiMessages.STOCK_NEGATIVE) Integer stock) {
}
