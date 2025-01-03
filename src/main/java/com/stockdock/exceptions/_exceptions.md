### Overview of Exception Handling in Java and Spring

#### **What are Exception Handlers?**
Exception handlers in Spring are mechanisms to manage errors in a clean, centralized, and user-friendly way. Instead of crashing the application or returning raw exceptions, exception handlers:
- Log meaningful error messages.
- Send appropriate HTTP status codes and error messages back to the client.

#### **Difference Between Global and Local Exception Handlers**
1. **Global Exception Handler:**
    - Applies to all controllers across the entire application.
    - Centralizes exception handling to one place.
    - Defined using `@ControllerAdvice`.
    - Best for handling generic exceptions (e.g., `NullPointerException`, `RuntimeException`) and common custom exceptions (e.g., `ApiRequestException`).

2. **Local Exception Handler:**
    - Applies only to a specific controller.
    - Defined using `@ExceptionHandler` directly within a controller.
    - Best for controller-specific errors that don’t apply to the rest of the application.

**Best Practice:** Use a global handler for common errors and a local handler only when an error is unique to a specific controller.

---

### **Key Principles for Exception Handling in Spring**

#### 1. **Custom Exceptions for Specific Errors**
- Define exceptions for predictable scenarios (e.g., invalid input, database connection failure).
- Custom exceptions should extend `RuntimeException` for simplicity.

#### 2. **Return Meaningful HTTP Status Codes**
- Use appropriate status codes for errors:
    - `400 Bad Request`: Invalid input or user error.
    - `404 Not Found`: Resource not found.
    - `500 Internal Server Error`: Unexpected server-side issues.
    - `401 Unauthorized` / `403 Forbidden`: Authentication or permission issues.

#### 3. **Centralized Logging**
- Always log errors in the `GlobalExceptionHandler`.
- Use `Logger` to log error details for debugging.

#### 4. **Consistent Response Structure**
- Return structured error responses, such as:
  ```json
  {
    "status": "error",
    "message": "Invalid input provided.",
    "details": "Symbol 'XYZ' is not recognized."
  }
  ```

#### 5. **Separate Layers for Validation and Exceptions**
- Validate inputs in controllers or services and throw exceptions for invalid cases.
- Avoid overloading exception handlers with validation logic.

---

### **Plan for Exception Handling in StockDock**

#### **1. Define Custom Exceptions**
Create exceptions for predictable scenarios:
- `InvalidSymbolException`
- `InvalidDateRangeException`
- `DataNotFoundException`

#### **2. Update the Global Exception Handler**
Enhance the `GlobalExceptionHandler` to:
- Handle all custom exceptions.
- Return structured responses with meaningful HTTP status codes.

#### **3. Apply Validations**
- Validate inputs in the controller and throw exceptions for invalid values.
- Example: If `startDate` > `endDate`, throw `InvalidDateRangeException`.

#### **4. Refactor Controllers**
Refactor controller methods to rely on exceptions for error handling instead of manually returning error messages.

#### **5. Implement Logging**
Log errors in the exception handler and avoid duplicate logging in the service layer.

---

### **Updated Code Implementation**

#### **Step 1: Create Custom Exceptions**
```java
package com.stockdock.exceptions;

public class InvalidSymbolException extends RuntimeException {
    public InvalidSymbolException(String message) {
        super(message);
    }
}

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
```

---

#### **Step 2: Update the Global Exception Handler**
```java
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

    @ExceptionHandler(InvalidSymbolException.class)
    public ResponseEntity<?> handleInvalidSymbolException(InvalidSymbolException e) {
        logger.error("Invalid symbol: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<?> handleInvalidDateRangeException(InvalidDateRangeException e) {
        logger.error("Invalid date range: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?> handleDataNotFoundException(DataNotFoundException e) {
        logger.error("Data not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ErrorResponse createErrorResponse(Exception e, HttpStatus status) {
        return new ErrorResponse("error", e.getMessage(), status.value());
    }

    private static class ErrorResponse {
        private String status;
        private String message;
        private int code;

        public ErrorResponse(String status, String message, int code) {
            this.status = status;
            this.message = message;
            this.code = code;
        }

        // Getters
        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public int getCode() {
            return code;
        }
    }
}
```

---

#### **Step 3: Add Validations in Controllers**
Example for `HistoricalStockController`:
```java
@PostMapping("/fetch-and-save")
public List<HistoricalStock> fetchAndSaveHistoricalDataForSymbol(
    @RequestParam String symbol,
    @RequestParam String startDate,
    @RequestParam String endDate
) {
    if (!symbol.matches("^[A-Z\\.]{1,5}$")) {
        throw new InvalidSymbolException("Symbol must be uppercase and 1-5 characters long.");
    }
    if (LocalDate.parse(startDate).isAfter(LocalDate.parse(endDate))) {
        throw new InvalidDateRangeException("Start date must be before end date.");
    }
    return historicalStockService.fetchAndSaveHistoricalDataForSymbol(symbol, startDate, endDate);
}
```

---

### Next Steps
1. Implement the updated exception handling and validations.
2. Test all endpoints in Postman to ensure proper error responses.
3. Review the response structure and refine as needed.

### Java Exception Handling Summary

#### **Key Concepts**
1. **`extends`**:
    - Used for inheritance in Java.
    - Allows a class to inherit properties and behaviors from another class.
    - Example:
      ```java
      public class InvalidSymbolException extends RuntimeException
      ```

2. **`super()`**:
    - Calls the constructor of the parent class.
    - Passes data (e.g., an error message) to the parent class for initialization.
    - Example:
      ```java
      super(message);
      ```

3. **Checked vs. Unchecked Exceptions**:
    - **Checked Exceptions**: Must be declared in the method signature or handled with `try-catch`.
    - **Unchecked Exceptions**: (e.g., `RuntimeException`) do not require explicit handling or declaration. Preferred for business logic validation.

---

#### **Best Practices for Custom Exceptions**
1. Use **normal classes** (not interfaces) for custom exceptions.
2. Extend from `RuntimeException` for unchecked exceptions.
3. Provide at least two constructors:
    - One for the error message:
      ```java
      public InvalidSymbolException(String message) {
          super(message);
      }
      ```
    - One for the error message and cause:
      ```java
      public InvalidSymbolException(String message, Throwable cause) {
          super(message, cause);
      }
      ```

---

#### **Why Extend `RuntimeException`?**
- Unchecked exceptions make it easier to manage business logic errors (e.g., invalid data, missing resources) without enforcing `try-catch` everywhere.
- Examples of when to use:
    - Invalid stock symbols or dates.
    - Data not found in the database.

---

#### **Common Custom Exceptions in This Project**
1. **`InvalidSymbolException`**: Thrown when the stock symbol is invalid.
2. **`InvalidDateRangeException`**: Thrown when the date range is invalid.
3. **`DataNotFoundException`**: Thrown when requested data does not exist in the database.

---

### HTTP Status Code Summary for Exception Handling

#### **When to Use `BAD_REQUEST` (400):**
- The error is caused by **invalid input from the client** (e.g., invalid symbol, malformed data, wrong query parameters).
- Example: The client sends a stock symbol that doesn't exist.
- Indicates the **client needs to fix their request** to proceed.

#### **When to Use `INTERNAL_SERVER_ERROR` (500):**
- The error occurs due to an **unexpected issue on the server** (e.g., null pointer exception, database failure, or bugs).
- Example: A server misconfiguration or unexpected application crash.
- Indicates an issue that is not the client's fault.

#### **Best Practice:**
- Use **400 (BAD_REQUEST)** for client-side errors (InvalidSymbolException, InvalidDateRangeException).
- Use **500 (INTERNAL_SERVER_ERROR)** for server-side issues or unexpected failures.
- Always provide a meaningful error message in the response body for easier debugging.

Example:
```java
@ExceptionHandler(InvalidSymbolException.class)
public ResponseEntity<String> handleInvalidSymbolException(InvalidSymbolException e) {
    logger.error("Invalid symbol: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid symbol: " + e.getMessage());
}
```
