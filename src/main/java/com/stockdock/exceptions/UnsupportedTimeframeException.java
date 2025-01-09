package com.stockdock.exceptions;

public class UnsupportedTimeframeException extends RuntimeException {
   public UnsupportedTimeframeException(String message) {
      super(message);
   }
}
