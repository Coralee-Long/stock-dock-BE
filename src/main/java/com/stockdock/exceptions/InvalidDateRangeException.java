package com.stockdock.exceptions;

public class InvalidDateRangeException extends RuntimeException {
   // Constructor to pass just a message
   public InvalidDateRangeException(String message) {
      super(message);
   }

   // Constructor to pass a message and a cause
   public InvalidDateRangeException(String message, Throwable cause) {
      super(message, cause);
   }

}
