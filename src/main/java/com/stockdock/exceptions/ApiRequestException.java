package com.stockdock.exceptions;

/**
 * Custom exception for API-related errors.
 */
public class ApiRequestException extends RuntimeException {
	 public ApiRequestException(String message) {
			super(message);
	 }
	 public ApiRequestException(String message, Throwable cause) {
			super(message, cause);
	 }
}
