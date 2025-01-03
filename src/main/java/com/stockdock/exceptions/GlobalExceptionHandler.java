package com.stockdock.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ApiRequestException.class)
	public ResponseEntity<String> handleApiRequestException (ApiRequestException e) {
		logger.error("API request failed: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception e) {
		logger.error("An unexpected error occurred: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
	}

	@ExceptionHandler(InvalidSymbolException.class)
	public ResponseEntity<String> handleInvalidSymbolException(InvalidSymbolException e) {
		logger.error("Invalid symbol: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid symbol: " + e.getMessage());
	}

	@ExceptionHandler(InvalidDateRangeException.class)
	public ResponseEntity<String> handleInvalidDateRangeException(InvalidDateRangeException e) {
		logger.error("Invalid date range: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date range: " + e.getMessage());
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<String> handleDataNotFoundException(DataNotFoundException e) {
		logger.error("Data not found: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found: " + e.getMessage());
	}
}
