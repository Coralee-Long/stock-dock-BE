package com.stockdock.exceptions;

public class InvalidSymbolException extends RuntimeException {
   // Constructor to pass just a message
   public InvalidSymbolException(String message) {
      super(message); // Call the RuntimeException constructor with the message
   }

   // Constructor to pass a message and a cause
   public InvalidSymbolException(String message, Throwable cause) {
      super(message, cause); // Call the RuntimeException constructor with both
   }
}
