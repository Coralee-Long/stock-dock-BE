package com.stockdock.exceptions;

public class EmptyResponseException extends RuntimeException {
   public EmptyResponseException(String message) {
      super(message);
   }
}
