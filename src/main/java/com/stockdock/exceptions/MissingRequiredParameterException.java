package com.stockdock.exceptions;

public class MissingRequiredParameterException extends RuntimeException {
   public MissingRequiredParameterException(String message) {
      super(message);
   }
}
