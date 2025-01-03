package com.stockdock.exceptions;

public class DataNotFoundException extends RuntimeException {
   // Constructor to pass just a message
   public DataNotFoundException(String message) {
      super(message);
   }

   // Constructor to pass a message and a cause
   public DataNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }
}

